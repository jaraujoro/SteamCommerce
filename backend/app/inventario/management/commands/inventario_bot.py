import time

import requests
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from django.conf import settings
from django.core.management.base import BaseCommand, CommandError

from ...models import Inventario
from app.steam.dota_heroe import SteamDotaHero


APP_ID = 570
CONTEXT_ID = 2

# Cantidad máxima solicitada por cada página.
# Si hay más items, Steam devuelve more_items y last_assetid.
PAGE_SIZE = 2000

REQUEST_TIMEOUT = (10, 40)
DELAY_BETWEEN_PAGES = 1


HEADERS = {
    "User-Agent": (
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
        "AppleWebKit/537.36 (KHTML, like Gecko) "
        "Chrome/120.0.0.0 Safari/537.36"
    ),
    "Accept": "application/json,text/plain,*/*",
    "Accept-Language": "es-ES,es;q=0.9,en;q=0.8",
}


class Command(BaseCommand):
    help = (
        "Sincroniza únicamente los items tradeables "
        "del inventario de Dota 2 del bot."
    )

    def handle(self, *args, **options):
        steam_id = getattr(settings, "STEAM_BOT_ID", None)

        if not steam_id:
            raise CommandError(
                "STEAM_BOT_ID no está configurado en settings.py."
            )

        steam_id = str(steam_id).strip()

        if not steam_id.isdigit():
            raise CommandError(
                "STEAM_BOT_ID debe ser un SteamID64 numérico."
            )

        base_url = (
            f"https://steamcommunity.com/inventory/"
            f"{steam_id}/{APP_ID}/{CONTEXT_ID}"
        )

        session = self.crear_sesion_http()

        self.stdout.write(
            f"Obteniendo inventario completo de Steam: {steam_id}"
        )

        (
            assets,
            descriptions,
            total_reportado,
            respuesta_completa,
        ) = self.obtener_inventario_completo(
            session=session,
            base_url=base_url,
        )

        self.stdout.write(
            f"Items únicos obtenidos desde Steam: {len(assets)}"
        )

        self.stdout.write(
            f"Descripciones únicas obtenidas: {len(descriptions)}"
        )

        if total_reportado is not None:
            self.stdout.write(
                f"Total reportado por Steam: {total_reportado}"
            )

        descripcion_map = self.crear_mapa_descripciones(
            descriptions
        )

        dota_hero = SteamDotaHero()

        creados = 0
        actualizados = 0
        omitidos = 0
        errores = 0

        items_tradeables = 0
        items_no_tradeables = 0
        items_sin_descripcion = 0

        # Solamente contiene los assetid tradeables actuales.
        assetids_tradeables_actuales = set()

        for indice, asset in enumerate(assets, start=1):
            assetid = str(
                asset.get("assetid") or ""
            ).strip()

            classid = str(
                asset.get("classid") or ""
            ).strip()

            instanceid = str(
                asset.get("instanceid") or "0"
            ).strip()

            if not assetid or not classid:
                omitidos += 1

                self.stdout.write(
                    self.style.WARNING(
                        "Item omitido porque no tiene "
                        f"assetid o classid: {asset}"
                    )
                )

                continue

            descripcion = descripcion_map.get(
                (classid, instanceid)
            )

            # Sin descripción no se puede saber de manera segura
            # si el item es tradeable.
            if descripcion is None:
                items_sin_descripcion += 1
                omitidos += 1

                self.stdout.write(
                    self.style.WARNING(
                        "Item sin descripción omitido | "
                        f"assetid={assetid} | "
                        f"classid={classid} | "
                        f"instanceid={instanceid}"
                    )
                )

                continue

            tradable = self.convertir_booleano(
                descripcion.get("tradable", 0)
            )

            # Los items no tradeables no se guardan.
            if not tradable:
                items_no_tradeables += 1
                continue

            # Desde aquí el item sí es tradeable.
            items_tradeables += 1

            # Cada item es único por su assetid.
            assetids_tradeables_actuales.add(assetid)

            marketable = self.convertir_booleano(
                descripcion.get("marketable", 0)
            )

            commodity = self.convertir_booleano(
                descripcion.get("commodity", 0)
            )

            heroe = self.obtener_heroe(
                dota_hero=dota_hero,
                descripcion=descripcion,
            )

            rareza, tipo_tag = self.obtener_tags(
                descripcion
            )

            try:
                _, created = Inventario.objects.update_or_create(
                    assetid=assetid,
                    defaults={
                        "classid": classid,
                        "instanceid": instanceid,
                        "market_hash_name": (
                            descripcion.get("market_hash_name")
                            or ""
                        ),
                        "market_name": (
                            descripcion.get("market_name")
                            or descripcion.get("name")
                            or ""
                        ),
                        "name": (
                            descripcion.get("name")
                            or ""
                        ),
                        "type": (
                            descripcion.get("type")
                            or ""
                        ),
                        "icon_url": (
                            descripcion.get("icon_url")
                            or ""
                        ),
                        "icon_url_large": (
                            descripcion.get("icon_url_large")
                            or ""
                        ),
                        "name_color": (
                            descripcion.get("name_color")
                            or ""
                        ),
                        "background_color": (
                            descripcion.get("background_color")
                            or ""
                        ),
                        "heroe": heroe,
                        "rareza_tag": rareza or "",
                        "tipo_tag": tipo_tag or "",

                        # Siempre será True porque solamente
                        # guardamos items tradeables.
                        "tradable": True,

                        "marketable": marketable,
                        "commodity": commodity,
                        "appid": self.convertir_entero(
                            asset.get("appid"),
                            APP_ID,
                        ),
                        "contextid": str(
                            asset.get("contextid")
                            or CONTEXT_ID
                        ),
                    },
                )

                if created:
                    creados += 1
                else:
                    actualizados += 1

            except Exception as error:
                errores += 1

                nombre_item = (
                    descripcion.get("market_hash_name")
                    or descripcion.get("name")
                    or "Sin nombre"
                )

                self.stderr.write(
                    self.style.ERROR(
                        "Error guardando item "
                        f"{indice}/{len(assets)} | "
                        f"assetid={assetid} | "
                        f"nombre={nombre_item} | "
                        f"{type(error).__name__}: {error}"
                    )
                )

        eliminados = 0

        # Solamente se eliminan registros antiguos cuando:
        # 1. Steam devolvió el inventario completo.
        # 2. No hubo errores al guardar.
        # 3. Todos los assets tenían descripción.
        puede_eliminar = (
            respuesta_completa
            and errores == 0
            and items_sin_descripcion == 0
        )

        if puede_eliminar:
            eliminados, _ = Inventario.objects.exclude(
                assetid__in=assetids_tradeables_actuales
            ).delete()

            self.stdout.write(
                "Items eliminados porque dejaron de ser "
                f"tradeables o ya no están en Steam: {eliminados}"
            )

        else:
            self.stdout.write(
                self.style.WARNING(
                    "No se eliminaron registros antiguos porque "
                    "la respuesta de Steam pudo ser parcial, "
                    "hubo errores o faltaron descripciones."
                )
            )

        self.stdout.write(
            self.style.SUCCESS(
                "\nSincronización finalizada.\n"
                f"Items totales recibidos desde Steam: {len(assets)}\n"
                f"Items tradeables encontrados: {items_tradeables}\n"
                f"Items no tradeables ignorados: {items_no_tradeables}\n"
                f"Items sin descripción: {items_sin_descripcion}\n"
                f"Items tradeables creados: {creados}\n"
                f"Items tradeables actualizados: {actualizados}\n"
                f"Items omitidos: {omitidos}\n"
                f"Errores: {errores}\n"
                f"Registros eliminados: {eliminados}\n"
                f"Total tradeables actuales en DB: "
                f"{Inventario.objects.count()}"
            )
        )

    def crear_sesion_http(self):
        """
        Crea una sesión HTTP con reintentos automáticos
        ante errores temporales de Steam.
        """
        session = requests.Session()

        retry = Retry(
            total=5,
            connect=5,
            read=5,
            status=5,
            backoff_factor=1,
            status_forcelist=[
                429,
                500,
                502,
                503,
                504,
            ],
            allowed_methods=frozenset(["GET"]),
            respect_retry_after_header=True,
            raise_on_status=False,
        )

        adapter = HTTPAdapter(
            max_retries=retry,
            pool_connections=10,
            pool_maxsize=10,
        )

        session.mount("https://", adapter)
        session.mount("http://", adapter)

        session.headers.update(HEADERS)

        return session

    def obtener_inventario_completo(
        self,
        session,
        base_url,
    ):
        """
        Consulta todas las páginas del inventario de Steam.

        Aunque solamente se guardan items tradeables, se debe
        descargar el inventario completo para conocer cuáles
        son tradeables y cuáles dejaron de serlo.

        Los assets se almacenan usando assetid como clave,
        garantizando que cada item sea único.
        """
        assets_map = {}
        descriptions_map = {}

        start_assetid = None
        start_assetids_usados = set()

        total_reportado = None
        pagina = 1
        respuesta_completa = True

        while True:
            params = {
                "l": "spanish",
                "count": PAGE_SIZE,
            }

            if start_assetid:
                params["start_assetid"] = start_assetid

            self.stdout.write(
                f"Consultando página {pagina}..."
            )

            try:
                response = session.get(
                    base_url,
                    params=params,
                    timeout=REQUEST_TIMEOUT,
                )

            except requests.RequestException as error:
                raise CommandError(
                    f"Error de conexión con Steam: {error}"
                ) from error

            if response.status_code != 200:
                contenido = response.text[:500]

                raise CommandError(
                    f"Steam devolvió HTTP "
                    f"{response.status_code}.\n"
                    f"URL: {response.url}\n"
                    f"Respuesta: {contenido}"
                )

            try:
                data = response.json()

            except ValueError as error:
                contenido = response.text[:500]

                raise CommandError(
                    "Steam no devolvió un JSON válido.\n"
                    f"Respuesta recibida: {contenido}"
                ) from error

            if data.get("success") not in (1, True):
                raise CommandError(
                    "Steam indicó que la solicitud no fue "
                    "exitosa. Verifica que el inventario "
                    "sea público y que STEAM_BOT_ID sea correcto."
                )

            if total_reportado is None:
                total_reportado = (
                    self.convertir_entero_opcional(
                        data.get("total_inventory_count")
                    )
                )

            assets_pagina = (
                data.get("assets")
                or []
            )

            descriptions_pagina = (
                data.get("descriptions")
                or []
            )

            for asset in assets_pagina:
                assetid = str(
                    asset.get("assetid") or ""
                ).strip()

                if not assetid:
                    continue

                # Si Steam repite un item entre páginas,
                # se conserva solamente uno por assetid.
                assets_map[assetid] = asset

            for descripcion in descriptions_pagina:
                classid = str(
                    descripcion.get("classid") or ""
                ).strip()

                instanceid = str(
                    descripcion.get("instanceid") or "0"
                ).strip()

                if not classid:
                    continue

                # La descripción puede ser compartida por varios
                # items con el mismo classid e instanceid.
                descriptions_map[
                    (classid, instanceid)
                ] = descripcion

            self.stdout.write(
                f"Página {pagina}: "
                f"{len(assets_pagina)} items recibidos. "
                f"Items únicos acumulados: {len(assets_map)}"
            )

            more_items = self.convertir_booleano(
                data.get("more_items", 0)
            )

            if not more_items:
                break

            nuevo_start_assetid = str(
                data.get("last_assetid") or ""
            ).strip()

            if not nuevo_start_assetid:
                respuesta_completa = False

                self.stdout.write(
                    self.style.WARNING(
                        "Steam indicó que existen más items, "
                        "pero no devolvió last_assetid."
                    )
                )

                break

            if nuevo_start_assetid in start_assetids_usados:
                respuesta_completa = False

                self.stdout.write(
                    self.style.WARNING(
                        "Steam repitió "
                        f"last_assetid={nuevo_start_assetid}. "
                        "Se detuvo la paginación para evitar "
                        "un bucle infinito."
                    )
                )

                break

            start_assetids_usados.add(
                nuevo_start_assetid
            )

            start_assetid = nuevo_start_assetid
            pagina += 1

            time.sleep(DELAY_BETWEEN_PAGES)

        assets = list(assets_map.values())
        descriptions = list(
            descriptions_map.values()
        )

        if (
            total_reportado is not None
            and len(assets) != total_reportado
        ):
            respuesta_completa = False

            self.stdout.write(
                self.style.WARNING(
                    f"Steam reportó {total_reportado} items, "
                    f"pero se obtuvieron {len(assets)} items únicos. "
                    "No se eliminarán registros antiguos."
                )
            )

        return (
            assets,
            descriptions,
            total_reportado,
            respuesta_completa,
        )

    def crear_mapa_descripciones(
        self,
        descriptions,
    ):
        """
        Relaciona cada descripción con su combinación
        de classid e instanceid.
        """
        descripcion_map = {}

        for descripcion in descriptions:
            classid = str(
                descripcion.get("classid") or ""
            ).strip()

            instanceid = str(
                descripcion.get("instanceid") or "0"
            ).strip()

            if not classid:
                continue

            descripcion_map[
                (classid, instanceid)
            ] = descripcion

        return descripcion_map

    def obtener_heroe(
        self,
        dota_hero,
        descripcion,
    ):
        """
        Obtiene el héroe relacionado con el item.
        """
        if not descripcion:
            return "Sin héroe"

        try:
            heroe = dota_hero.extraer_heroe_de_item(
                descripcion
            )

            return heroe or "Sin héroe"

        except Exception as error:
            self.stdout.write(
                self.style.WARNING(
                    "No se pudo determinar "
                    f"el héroe: {error}"
                )
            )

            return "Sin héroe"

    def obtener_tags(self, descripcion):
        """
        Extrae los tags de rareza y tipo.
        """
        rareza = ""
        tipo = ""

        for tag in descripcion.get("tags") or []:
            categoria = str(
                tag.get("category") or ""
            ).strip()

            valor = (
                tag.get("localized_tag_name")
                or tag.get("name")
                or ""
            )

            if categoria == "Rarity":
                rareza = valor

            elif categoria == "Type":
                tipo = valor

        return rareza, tipo

    @staticmethod
    def convertir_booleano(valor):
        """
        Convierte valores como 1, '1', True
        o 'true' a booleano.
        """
        return str(valor).strip().lower() in {
            "1",
            "true",
            "yes",
        }

    @staticmethod
    def convertir_entero(
        valor,
        predeterminado=0,
    ):
        try:
            return int(valor)

        except (TypeError, ValueError):
            return predeterminado

    @staticmethod
    def convertir_entero_opcional(valor):
        try:
            return int(valor)

        except (TypeError, ValueError):
            return None
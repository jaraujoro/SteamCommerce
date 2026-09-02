import time
import re
import requests

from decimal import Decimal, InvalidOperation

from django.core.management.base import BaseCommand

from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from ...models import Inventario


class Command(BaseCommand):

    help = (
        "Sincroniza precio_mercado desde Steam Market "
        "para los items en Inventario"
    )

    def add_arguments(self, parser):

        parser.add_argument(
            "--delay",
            type=float,
            default=3.0
        )

    def handle(self, *args, **options):

        delay = options["delay"]

        session = self.crear_sesion_http()

        nombres = list(
            Inventario.objects
            .exclude(
                market_hash_name=""
            )
            .values_list(
                "market_hash_name",
                flat=True
            )
            .distinct()
        )

        total = len(nombres)

        self.stdout.write(
            f"Consultando precio_mercado de "
            f"{total} items únicos..."
        )

        actualizados = 0
        sin_cambios = 0

        for i, nombre in enumerate(
            nombres,
            start=1
        ):

            nombre = nombre.strip()

            precio = (
                self.obtener_precio_mercado(
                    session,
                    nombre
                )
            )

            if precio > 0:

                afectados = (
                    Inventario.objects
                    .filter(
                        market_hash_name=nombre
                    )
                    .update(
                        precio_mercado=precio
                    )
                )

                actualizados += afectados

                self.stdout.write(
                    self.style.SUCCESS(
                        f"OK: {nombre} "
                        f"-> S/. {precio}"
                    )
                )

            else:

                sin_cambios += 1

                self.stdout.write(
                    self.style.WARNING(
                        f"Sin cambio: {nombre}"
                    )
                )

            if i % 50 == 0:

                self.stdout.write(
                    f"Progreso: "
                    f"{i}/{total}"
                )

            time.sleep(delay)

        self.stdout.write(
            self.style.SUCCESS(
                f"Listo. "
                f"Items actualizados: "
                f"{actualizados}, "
                f"nombres sin cambio: "
                f"{sin_cambios}"
            )
        )

    def crear_sesion_http(self):

        session = requests.Session()

        retry = Retry(

            total=5,

            connect=5,

            read=5,

            status=5,

            backoff_factor=2,

            status_forcelist=[
                429,
                500,
                502,
                503,
                504,
            ],

            allowed_methods=frozenset(
                ["GET"]
            ),

            respect_retry_after_header=True,

            raise_on_status=False,
        )

        adapter = HTTPAdapter(

            max_retries=retry,

            pool_connections=10,

            pool_maxsize=10,
        )

        session.mount(
            "https://",
            adapter
        )

        session.mount(
            "http://",
            adapter
        )

        session.headers.update({

            "User-Agent": (
                "Mozilla/5.0 "
                "(Windows NT 10.0; Win64; x64) "
                "AppleWebKit/537.36 "
                "(KHTML, like Gecko) "
                "Chrome/150.0.0.0 "
                "Safari/537.36"
            ),

            "Accept": (
                "application/json,"
                "text/plain,*/*"
            ),

            "Accept-Language": (
                "es-419,es;q=0.9,en;q=0.8"
            ),
        })

        return session

    def limpiar_precio(
        self,
        precio_str
    ):

        if not precio_str:

            return Decimal("0")

        match = re.search(
            r"\d[\d.,]*",
            precio_str
        )

        if not match:

            return Decimal("0")

        numero = match.group(0)

        if (
            "," in numero
            and "." in numero
        ):

            numero = numero.replace(
                ",",
                ""
            )

        elif "," in numero:

            numero = numero.replace(
                ",",
                "."
            )

        try:

            return Decimal(
                numero
            )

        except InvalidOperation:

            return Decimal("0")

    def obtener_precio_mercado(
        self,
        session,
        market_hash_name
    ):

        if not market_hash_name:

            return Decimal("0")

        url = (
            "https://steamcommunity.com/"
            "market/priceoverview/"
        )

        params = {

            "country": "PE",

            "currency": 26,

            "appid": 570,

            "market_hash_name":
                market_hash_name,
        }

        try:

            response = session.get(

                url,

                params=params,

                timeout=15
            )

            self.stdout.write(
                f"[{market_hash_name}] "
                f"Status: "
                f"{response.status_code}"
            )

            if response.status_code == 429:

                self.stdout.write(
                    self.style.WARNING(
                        f"Steam sigue bloqueando "
                        f"'{market_hash_name}' "
                        f"después de los reintentos"
                    )
                )

                return Decimal("0")

            if response.status_code != 200:

                self.stdout.write(
                    self.style.WARNING(
                        f"Status "
                        f"{response.status_code} "
                        f"para "
                        f"'{market_hash_name}'"
                    )
                )

                return Decimal("0")

            data = response.json()

            if not data.get(
                "success"
            ):

                self.stdout.write(
                    self.style.WARNING(
                        f"Steam no devolvió "
                        f"precio para "
                        f"'{market_hash_name}'"
                    )
                )

                return Decimal("0")

            precio_str = (

                data.get(
                    "lowest_price"
                )

                or data.get(
                    "median_price"
                )
            )

            if not precio_str:

                return Decimal("0")

            return self.limpiar_precio(
                precio_str
            )

        except Exception as e:

            self.stdout.write(
                self.style.WARNING(
                    f"Error con "
                    f"'{market_hash_name}': "
                    f"{e}"
                )
            )

            return Decimal("0")
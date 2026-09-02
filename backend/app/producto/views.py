# views.py

import time
from django.conf import settings
import requests
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated
from app.steam.api_node import SteamBot
from app.steam.dota_heroe import SteamDotaHero
from .models import Usuario
import json

HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 '
                   '(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
}

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def inventario_bot(request):
    # steam_id = request.user.username  # LOGIN
    steam_id = settings.STEAM_BOT_ID  # BOT

    if not steam_id:
        return Response(
            {'error': 'Usuario no tiene steam_id asociado'},
            status=400
        )

    base_url = f'https://steamcommunity.com/inventory/{steam_id}/570/2'

    all_assets = []
    all_descriptions = []
    start_assetid = None

    while True:
        params = {
            'l': 'spanish',
            'count': 500
        }

        if start_assetid:
            params['start_assetid'] = start_assetid

        response = requests.get(base_url, params=params, headers=HEADERS)

        if response.status_code != 200:
            return Response({
                'error': 'No se pudo obtener el inventario. Verifica que tu inventario de Steam sea público.',
                'steam_status': response.status_code,
                'steam_body': response.text[:300],
            }, status=400)

        data = response.json()

        if not data or not data.get('success'):
            return Response({
                'error': 'Steam devolvió una respuesta vacía o inválida.',
            }, status=400)

        all_assets.extend(data.get('assets', []))
        all_descriptions.extend(data.get('descriptions', []))

        if data.get('more_items'):
            start_assetid = data.get('last_assetid')
            time.sleep(0.5)
        else:
            break

    # ==========================================================
    # Crear un mapa para acceder rápidamente a las descripciones
    # ==========================================================

    descripcion_map = {
        (desc['classid'], desc['instanceid']): desc
        for desc in all_descriptions
    }

    # ==========================================================
    # Construir inventario final
    # ==========================================================

    items_procesados = []
    dotaHero = SteamDotaHero()
    # steamPrecio = SteamPrecioItem()

    for asset in all_assets:

        desc = descripcion_map.get(
            (asset['classid'], asset['instanceid'])
        )

        if not desc:
            continue

        # Solo devolver items intercambiables
        if desc.get('tradable', 0) != 1:
            continue

        heroe = dotaHero.extraer_heroe_de_item(desc)

        rareza = None
        tipo = None

        for tag in desc.get('tags', []):
            categoria = tag.get('category')

            if categoria == 'Rarity':
                rareza = tag.get('localized_tag_name')
            elif categoria == 'Type':
                tipo = tag.get('localized_tag_name')

        market_name = desc.get('market_hash_name')
        # precio = steamPrecio.obtener_precio_mercado(market_name)

        items_procesados.append({
            **desc,
            'assetid': asset['assetid'],
            'amount': 0,
            'contextid': asset.get('contextid'),
            'appid': asset.get('appid'),
            'heroe': heroe or 'Sin héroe',
            'rareza_tag': rareza,
            'tipo_tag': tipo,
        })

    return Response({
        'items': items_procesados,
        'total_items': len(items_procesados),
    })

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def comprar_item(request):
    asset_id = request.data.get('asset_id')
    codigo_2fa = request.data.get('codigo_2fa')
    
    if isinstance(asset_id, str):
            try:
                # Intentar parsear como JSON
                parsed = json.loads(asset_id)
                if isinstance(parsed, list):
                    asset_id = parsed
                else:
                    asset_id = [parsed]
            except (json.JSONDecodeError, TypeError):
                # Si no es JSON válido, tratar como string único
                asset_id = [asset_id]
    
    if not isinstance(asset_id, list) or len(asset_id) == 0:
        return Response({'error': 'No hay items para comprar'}, status=400)
    
    if not codigo_2fa:
        return Response({'error': 'Se necesita código de 2FA'}, status=400)
    
    steam_id = request.user.username
    
    try:
        usuario = Usuario.objects.get(steam_id=steam_id)
        trade_url = usuario.trade_url
    except Usuario.DoesNotExist:
        return Response({'error': 'Usuario no encontrado'}, status=404)
    
    if not trade_url:
        return Response({'error': 'El usuario no tiene configurada una Trade URL'}, status=400)
    
    bot = SteamBot()
    try:
        resultado = bot.createSellOffer(trade_url, asset_id, steam_id, codigo_2fa)
        print(resultado)
        return Response(resultado)
    except requests.exceptions.ConnectionError:
        return Response({
            'error': 'No se pudo conectar con el servicio de Steam',
            'detail': 'El servicio no está disponible en este momento'
        }, status=503)
    except Exception as e:
        return Response({
            'error': 'Error al procesar la compra',
            'detail': str(e)
        }, status=500)
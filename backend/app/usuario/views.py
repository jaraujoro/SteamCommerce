# backend/app/usuario/views.py

from django.shortcuts import redirect
from django.conf import settings
from django.contrib.auth.models import User
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.response import Response
from rest_framework_simplejwt.tokens import RefreshToken
import requests
from .models import Usuario
import re
import time

HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 '
                   '(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
}

def extraer_heroe_de_item(desc):
    # Opción A: por tags
    for tag in desc.get('tags', []):
        if tag.get('category', '').lower() == 'hero':
            return tag.get('localized_tag_name')

    # Opción B: por texto "Used By: X" dentro de descriptions
    for d in desc.get('descriptions', []):
        match = re.match(r'Used By:\s*(.+)', d.get('value', ''))
        if match:
            return match.group(1).strip()

    return None

@api_view(['GET'])
@permission_classes([AllowAny])
def steam_login(request):
    params = {
        'openid.ns': 'http://specs.openid.net/auth/2.0',
        'openid.mode': 'checkid_setup',
        'openid.return_to': f'{settings.ALLOWED_HOST}/usuario/callback/',
        'openid.realm': settings.ALLOWED_HOST,
        'openid.identity': 'http://specs.openid.net/auth/2.0/identifier_select',
        'openid.claimed_id': 'http://specs.openid.net/auth/2.0/identifier_select',
    }
    
    login_url = 'https://steamcommunity.com/openid/login?' + '&'.join([f'{k}={v}' for k, v in params.items()])
    
    return Response({'login_url': login_url})

@api_view(['GET'])
@permission_classes([AllowAny])
def steam_callback(request):
    claimed_id = request.GET.get('openid.claimed_id')
    
    if isinstance(claimed_id, list):
        claimed_id = claimed_id[0]
    
    steam_id = claimed_id.split('/')[-1]
    
    if not steam_id:
        return Response({'error': 'No se pudo obtener steam_id'}, status=400)
    
    api_key = settings.STEAM_API_KEY
    url = f'http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key={api_key}&steamids={steam_id}'
    
    response = requests.get(url)
    data = response.json()
    
    if data['response']['players']:
        player = data['response']['players'][0]
        nombre = player.get('personaname', 'Usuario Steam')
        avatar = player.get('avatar', '')
        
        # Tabla Usuario
        usuario, created = Usuario.objects.get_or_create(
            steam_id=steam_id,
            defaults={
                'nombre': nombre,
                'avatar': avatar,
            }
        )
        
        # Admin Django
        user, _ = User.objects.get_or_create(
            username=steam_id, #Steam ID como username
            defaults={
                'first_name': nombre,
            }
        )
        
        refresh = RefreshToken.for_user(user)
        
        return redirect(
            f'{settings.ALLOWED_FRONT}?'
            f'access={refresh.access_token}&'
            f'refresh={refresh}&'
            f'steam_id={steam_id}&'
            f'nombre={nombre}&'
            f'avatar={avatar}'
        )
    
    return Response({'error': 'No se pudo obtener información'}, status=400)

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def obtener_perfil(request):
    steam_id = request.user.username
    
    try:
        usuario = Usuario.objects.get(steam_id=steam_id)
        return Response({
            'steam_id': usuario.steam_id,
            'nombre': usuario.nombre,
            'avatar': usuario.avatar,
            'trade_url': usuario.trade_url,
        })
    except Usuario.DoesNotExist:
        return Response({'error': 'Usuario no encontrado'}, status=404)

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def guardar_trade_url(request):
    
    steam_id = request.user.username

    trade_url = request.data.get('trade_url')

    if not trade_url:
        return Response({'error': 'Falta trade URL'}, status=400)

    if 'steamcommunity.com/tradeoffer/new/' not in trade_url:
        return Response({'error': 'La URL de intercambio no es válida'}, status=400)

    try:
        usuario = Usuario.objects.get(steam_id=steam_id)
        usuario.trade_url = trade_url
        usuario.save()
        return Response({'message': 'Trade URL guardada correctamente'})
    except Usuario.DoesNotExist:
        return Response({'error': 'Usuario no encontrado'}, status=404)

@api_view(['POST'])
@permission_classes([AllowAny])
def refresh_token(request):
    refresh_token = request.data.get('refresh')
    
    if not refresh_token:
        return Response({'error': 'Se requiere refresh token'}, status=400)
    
    try:
        refresh = RefreshToken(refresh_token)
        return Response({
            'access': str(refresh.access_token)
        })
    except:
        return Response({'error': 'Token inválido'}, status=401)
    
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def cargar_inventario(request):

    steam_id = request.user.username

    if not steam_id:
        return Response({'error': 'Usuario no tiene steam_id asociado'}, status=400)

    base_url = f'https://steamcommunity.com/inventory/{steam_id}/570/2'
    all_assets = []
    all_descriptions = []
    start_assetid = None

    while True:
        params = {'l': 'spanish', 'count': 500}
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

    items_procesados = []
    for desc in all_descriptions:
        heroe = extraer_heroe_de_item(desc)
        rareza = None
        tipo = None

        for tag in desc.get('tags', []):
            if tag.get('category') == 'Rarity':
                rareza = tag.get('localized_tag_name')
            elif tag.get('category') == 'Type':
                tipo = tag.get('localized_tag_name')

        items_procesados.append({
            **desc,
            'heroe': heroe or 'Sin héroe',
            'rareza_tag': rareza,
            'tipo_tag': tipo,
        })

    return Response({
        'assets': all_assets,
        'descriptions': items_procesados,
        'total_items': len(items_procesados),
        'total_assets': len(all_assets),
    })
# app/bot/steam_bot_manual.py
import requests
import re
from django.conf import settings

class SteamBot:
    
    def __init__(self):
        self.service_url = settings.STEAM_BOT_SERVICE_URL
        self.steam_api_key = settings.STEAM_SERVICE_API_KEY
    
    def createSellOffer(self, trade_url, asset_id, steam_id, codigo_2fa=None): # Bot → Usuario
        partner_match = re.search(r'partner=(\d+)', trade_url)
        token_match = re.search(r'token=([a-zA-Z0-9_-]+)', trade_url)
        
        partner_id = partner_match.group(1) if partner_match else None
        token = token_match.group(1) if token_match else ''

        if not isinstance(asset_id, list):
            asset_id = [asset_id]

        # API de Node.js
        response = requests.post(
            f"{self.service_url}/api/steam/comprar-item",
            json={
                "partnerId": partner_id,
                "assetId": asset_id,
                "token": token,
                "twoFactorCode": codigo_2fa,
                "steam_id": steam_id
            },
            headers={
                "x-api-key": self.steam_api_key
            },
        )

        if response.status_code != 200:
            return {"error": "Error con el servidor de Steam", "status": response.status_code}
        
        return response.json()
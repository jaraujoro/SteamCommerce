# backend/app/usuario/serializers.py

from rest_framework import serializers
from .models import Usuario

class UsuarioSerializer(serializers.ModelSerializer):
    class Meta:
        model = Usuario
        fields = ['steam_id', 'nombre', 'avatar', 'trade_url']
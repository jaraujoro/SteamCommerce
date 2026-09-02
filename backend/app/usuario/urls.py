# backend/app/usuario/urls.py

from django.urls import path
from . import views

urlpatterns = [
    path('login/', views.steam_login),
    path('callback/', views.steam_callback),
    path('perfil/', views.obtener_perfil),
    path('trade-url/', views.guardar_trade_url),
    path('inventario/', views.cargar_inventario),
    path('token/refresh/', views.refresh_token),
]
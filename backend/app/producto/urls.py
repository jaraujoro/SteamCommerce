# productos/urls.py

from django.urls import path
from . import views

urlpatterns = [
    path('catalogo/', views.inventario_bot),
    path('comprar-item/', views.comprar_item)
]
from django.urls import path
from . import views
urlpatterns = [
    path('bot_listar_inventario/', views.listar_inventario),
]
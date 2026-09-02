from django.db import models
from app.usuario.models import Usuario


class Producto(models.Model):

    ESTADO_CHOICES = [
        ('disponible', 'Disponible'),
        ('pendiente_envio', 'Pendiente de envío al bot'),
        ('en_bot', 'En poder del bot'),
        ('vendido', 'Vendido'),
        ('cancelado', 'Cancelado'),
    ]

    vendedor = models.ForeignKey(Usuario, on_delete=models.CASCADE, related_name='productos')

    # Datos del item que vienen de Steam
    market_hash_name = models.CharField(max_length=255)
    icon_url = models.CharField(max_length=500)
    heroe = models.CharField(max_length=100, blank=True, null=True)
    rareza = models.CharField(max_length=100, blank=True, null=True)
    tipo = models.CharField(max_length=100, blank=True, null=True)

    # Identificadores únicos de Steam para el trade (obligatorios para mover el item)
    classid = models.CharField(max_length=50)
    instanceid = models.CharField(max_length=50)
    assetid = models.CharField(max_length=50)  # el ID único de ESTA copia del item

    # Datos del marketplace
    precio = models.DecimalField(max_digits=10, decimal_places=2)
    estado = models.CharField(max_length=20, choices=ESTADO_CHOICES, default='pendiente_envio')

    creado_en = models.DateTimeField(auto_now_add=True)
    actualizado_en = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.market_hash_name} - S/.{self.precio} ({self.estado})"
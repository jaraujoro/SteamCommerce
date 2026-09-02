# models.py
from django.db import models


class Inventario(models.Model):
    # Identificadores únicos del item en Steam
    assetid = models.CharField(max_length=50, unique=True, db_index=True)
    classid = models.CharField(max_length=50, db_index=True)
    instanceid = models.CharField(max_length=50)

    # Info general del item
    market_hash_name = models.CharField(max_length=255, db_index=True)  # clave para buscar precio en Steam
    market_name = models.CharField(max_length=255)  # nombre en español, para mostrar
    name = models.CharField(max_length=255)
    type = models.CharField(max_length=255, blank=True, null=True)  # ej: "Cofre del tesoro de tipo Raro"

    # Imágenes
    icon_url = models.CharField(max_length=500, blank=True, null=True)
    icon_url_large = models.CharField(max_length=500, blank=True, null=True)

    # Metadata visual
    name_color = models.CharField(max_length=10, blank=True, null=True)
    background_color = models.CharField(max_length=10, blank=True, null=True)

    # Tags parseados (los que ya extraías en la vista)
    heroe = models.CharField(max_length=100, blank=True, null=True, default='Sin héroe')
    rareza_tag = models.CharField(max_length=100, blank=True, null=True)
    tipo_tag = models.CharField(max_length=100, blank=True, null=True)

    # Flags de Steam
    tradable = models.BooleanField(default=False)
    marketable = models.BooleanField(default=False)
    commodity = models.BooleanField(default=False)

    # Contexto del juego (Dota 2 = 570, contextid = 2, casi siempre fijo pero lo guardamos)
    appid = models.IntegerField(default=570)
    contextid = models.CharField(max_length=10, default='2')

    # ============================
    # Precios
    # ============================
    precio_mercado = models.DecimalField(
        max_digits=10, decimal_places=2, default=0,
        help_text="Precio actual en Steam Market (lowest_price)"
    )
    precio_venta = models.DecimalField(
        max_digits=10, decimal_places=2, default=0,
        help_text="Precio designado por vos para la venta"
    )

    # ============================
    # Timestamps
    # ============================
    creado_en = models.DateTimeField(auto_now_add=True)
    actualizado_en = models.DateTimeField(auto_now=True)

    class Meta:
        db_table = 'inventario'
        verbose_name = "Item de inventario bot"
        verbose_name_plural = "Items de inventario bot"
        indexes = [
            models.Index(fields=['market_hash_name']),
            models.Index(fields=['classid', 'instanceid']),
        ]

    def __str__(self):
        return f"{self.market_name} ({self.assetid})"
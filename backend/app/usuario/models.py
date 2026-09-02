from django.db import models

class Usuario(models.Model):
    steam_id = models.CharField(max_length=100, unique=True)
    nombre = models.CharField(max_length=200)
    avatar = models.URLField()
    trade_url = models.URLField(blank=True, null=True)
    created_at = models.DateTimeField(auto_now_add=True)
    
    def __str__(self):
        return self.nombre
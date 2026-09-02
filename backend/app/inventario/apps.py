from django.apps import AppConfig
import os

class InventarioConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'app.inventario'

    def ready(self):
        if os.environ.get('RUN_MAIN') != 'true':
            from . import scheduler
            scheduler.start()
import logging

from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.cron import CronTrigger
from django_apscheduler.jobstores import DjangoJobStore
from django.core.management import call_command


logger = logging.getLogger(__name__)

scheduler_started = False


def sync_completo_job():
    logger.info("Iniciando sincronización automática de inventario y precios...")

    inventario_ok = False

    try:
        call_command('inventario_bot')
        inventario_ok = True
        logger.info("Inventario sincronizado correctamente.")
    except Exception as e:
        logger.exception(f"Error en inventario_bot, se omite sync de precios: {e}")

    if inventario_ok:
        try:
            call_command('precios_steam', delay=3)
            logger.info("Precios sincronizados correctamente.")
        except Exception as e:
            logger.exception(f"Error en precios_steam: {e}")
    else:
        logger.warning("Sync de precios omitido porque el inventario falló.")

    logger.info("Sincronización automática finalizada.")


def start():
    global scheduler_started

    if scheduler_started:
        logger.warning("El scheduler ya estaba iniciado. Se omite inicio duplicado.")
        return

    scheduler = BackgroundScheduler(timezone="America/Lima")
    scheduler.add_jobstore(DjangoJobStore(), "default")

    scheduler.add_job(
        sync_completo_job,
        trigger=CronTrigger(hour="3", minute="0"),
        id="sync_completo_inventario",
        max_instances=1,
        replace_existing=True,
        coalesce=True,
        misfire_grace_time=3600,
    )

    scheduler.start()
    scheduler_started = True

    logger.info("Scheduler de inventario iniciado correctamente.")
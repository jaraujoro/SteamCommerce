from pathlib import Path
import os
from datetime import timedelta
from urllib.parse import urlparse

import dj_database_url
from dotenv import load_dotenv

load_dotenv()

BASE_DIR = Path(__file__).resolve().parent.parent


def _normalize_host(value: str) -> str:
    """Convierte URLs a hostnames válidos para ALLOWED_HOSTS."""
    value = value.strip()
    if not value:
        return ""
    if "://" in value:
        parsed = urlparse(value)
        return (parsed.hostname or "").strip()
    return value.rstrip("/")


def _parse_hosts(env_value: str, defaults: list[str]) -> list[str]:
    raw = env_value or ",".join(defaults)
    hosts: list[str] = []
    for item in raw.split(","):
        host = _normalize_host(item)
        if host and host not in hosts:
            hosts.append(host)
    return hosts


def _parse_origins(env_value: str, defaults: list[str]) -> list[str]:
    origins: list[str] = []
    raw = env_value or ",".join(defaults)
    for item in raw.split(","):
        origin = item.strip().rstrip("/")
        if not origin:
            continue
        if not origin.startswith(("http://", "https://")):
            origin = f"https://{origin}"
        if origin not in origins:
            origins.append(origin)
    return origins


SECRET_KEY = os.getenv(
    "SECRET_KEY",
    "django-insecure-darkdota-dev-key-change-in-production",
)

_default_debug = "False" if os.getenv("DATABASE_URL") else "True"
DEBUG = os.getenv("DEBUG", _default_debug) == "True"

# ============= ALLOWED_HOSTS =============
ALLOWED_HOSTS = _parse_hosts(
    os.getenv("ALLOWED_HOSTS", ""),
    [
        "127.0.0.1",
        "localhost",
        os.getenv("ALLOWED_HOST", "localhost"),  # BACKEND
    ],
)

INSTALLED_APPS = [
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'rest_framework',
    'rest_framework_simplejwt',
    'django_apscheduler',
    'corsheaders',
    'app.producto',
    'app.usuario',
    'app.inventario'
]

MIDDLEWARE = [
    'corsheaders.middleware.CorsMiddleware',
    'django.middleware.security.SecurityMiddleware',
    'whitenoise.middleware.WhiteNoiseMiddleware',  # Para archivos estáticos en producción
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
]

ROOT_URLCONF = 'config.urls'

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
            ],
        },
    },
]

WSGI_APPLICATION = 'config.wsgi.application'

# ============= BASE DE DATOS =============
if os.getenv("DATABASE_URL"):
    DATABASES = {
        "default": dj_database_url.config(
            default=os.getenv("DATABASE_URL"),
            conn_max_age=600,
            ssl_require=os.getenv("DATABASE_SSL", "False") == "True",
        )
    }
else:
    DATABASES = {
        'default': {
            'ENGINE': 'django.db.backends.postgresql',
            'NAME': os.getenv('DB_NAME'),
            'USER': os.getenv('DB_USER'),
            'PASSWORD': os.getenv('DB_PASSWORD'),
            'HOST': os.getenv('DB_HOST'),
            'PORT': os.getenv('DB_PORT', '5432'),
        }
    }

AUTH_PASSWORD_VALIDATORS = [
    {"NAME": "django.contrib.auth.password_validation.UserAttributeSimilarityValidator"},
    {"NAME": "django.contrib.auth.password_validation.MinimumLengthValidator"},
    {"NAME": "django.contrib.auth.password_validation.CommonPasswordValidator"},
    {"NAME": "django.contrib.auth.password_validation.NumericPasswordValidator"},
]

LANGUAGE_CODE = 'es-pe'
TIME_ZONE = 'America/Lima'
USE_I18N = True
USE_TZ = True

STATIC_URL = 'static/'
STATIC_ROOT = BASE_DIR / "staticfiles"
STATICFILES_STORAGE = "whitenoise.storage.CompressedManifestStaticFilesStorage"

DEFAULT_AUTO_FIELD = 'django.db.models.BigAutoField'

# ============= CORS CONFIGURACIÓN =============
CORS_ALLOWED_ORIGINS = _parse_origins(
    os.getenv("CORS_ALLOWED_ORIGINS", ""),
    [
        os.getenv("ALLOWED_FRONT", "http://localhost:5173"),  #FRON
    ],
)

CSRF_TRUSTED_ORIGINS = CORS_ALLOWED_ORIGINS  # Los mismos orígenes para CSRF

# ============= REST FRAMEWORK CON JWT =============
_api_browsable = os.getenv("API_BROWSABLE", "True" if DEBUG else "False") == "True"

REST_FRAMEWORK = {
    "DEFAULT_AUTHENTICATION_CLASSES": (
        "rest_framework_simplejwt.authentication.JWTAuthentication",
        "rest_framework.authentication.SessionAuthentication",
    ),
    "DEFAULT_PERMISSION_CLASSES": (
        "rest_framework.permissions.IsAuthenticated",
    ),
    "DEFAULT_RENDERER_CLASSES": (
        "rest_framework.renderers.JSONRenderer",
        *(
            ["rest_framework.renderers.BrowsableAPIRenderer"]
            if _api_browsable
            else []
        ),
    ),
}

SIMPLE_JWT = {
    'ACCESS_TOKEN_LIFETIME': timedelta(minutes=60),
    'REFRESH_TOKEN_LIFETIME': timedelta(days=7),
    'AUTH_HEADER_TYPES': ('Bearer',),
}

MEDIA_URL = '/media/' 
MEDIA_ROOT = os.path.join(BASE_DIR, 'media')

# ============= TUS VARIABLES =============
ALLOWED_HOST = os.getenv('ALLOWED_HOST')
ALLOWED_FRONT = os.getenv('ALLOWED_FRONT')

#Cuenta steam bot
STEAM_BOT_TRADE = os.getenv('STEAM_BOT_TRADE')
STEAM_API_KEY = os.getenv('STEAM_API_KEY')
STEAM_BOT_ID = os.getenv('STEAM_BOT_ID')

#Node
STEAM_BOT_SERVICE_URL = os.getenv('STEAM_BOT_SERVICE_URL')

#Contraseña segura para la api node
STEAM_SERVICE_API_KEY = os.getenv('STEAM_SERVICE_API_KEY')

# ============= SEGURIDAD PARA PRODUCCIÓN =============
if not DEBUG:
    SECURE_PROXY_SSL_HEADER = ("HTTP_X_FORWARDED_PROTO", "https")
    SECURE_SSL_REDIRECT = os.getenv("SECURE_SSL_REDIRECT", "True") == "True"
    SESSION_COOKIE_SECURE = True
    CSRF_COOKIE_SECURE = True
    SECURE_HSTS_SECONDS = 31536000
    SECURE_HSTS_INCLUDE_SUBDOMAINS = True
    SECURE_HSTS_PRELOAD = True
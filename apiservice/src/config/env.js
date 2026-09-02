const dotenv = require("dotenv");

dotenv.config();

module.exports = {
    PORT: process.env.PORT,

    DJANGO_URL: process.env.DJANGO_URL,

    STEAM_USERNAME: process.env.STEAM_USERNAME,
    STEAM_PASSWORD: process.env.STEAM_PASSWORD,
    STEAM_SHARED_SECRET: process.env.STEAM_SHARED_SECRET,
    STEAM_IDENTITY_SECRET: process.env.STEAM_IDENTITY_SECRET,
    STEAM_REVOCATION_CODE: process.env.STEAM_REVOCATION_CODE,
    STEAM_SERVICE_API_KEY: process.env.STEAM_SERVICE_API_KEY,
    STEAM_SESSION_HOURS: Number(process.env.STEAM_SESSION_HOURS)
};
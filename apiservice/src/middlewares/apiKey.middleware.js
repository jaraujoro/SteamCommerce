const { STEAM_SERVICE_API_KEY } = require("../config/env");

function apiKeyMiddleware(req, res, next) {
    const apiKey = req.headers["x-api-key"];

    if (!STEAM_SERVICE_API_KEY) {
        return res.status(500).json({
            error: "Key no está configurada en Node",
            detail:"No cuenta con las credenciales"
        });
    }

    if (!apiKey || apiKey !== STEAM_SERVICE_API_KEY) {
        return res.status(401).json({
            error: "No autorizado",
            detail:"No cuenta con las credenciales"
        });
    }

    next();
}

module.exports = apiKeyMiddleware;
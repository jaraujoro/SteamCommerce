// apiservice\src\services\tradeOffer.service.js

const { getSteamSession } = require("./steamAuth.service");

async function sendOffer(partnerId, assetIds, token, twoFactorCode, steamId) {
    try {
        const { manager } = await getSteamSession(twoFactorCode);

        const finalPartnerId = steamId || partnerId;

        if (!finalPartnerId) {
            return {
                success: false,
                error: "No se recibió steam_id o partnerId"
            };
        }

        console.log(`Solicitando items ${assetIds} para ${finalPartnerId}`);

        return new Promise((resolve, reject) => {
            const offer = manager.createOffer(finalPartnerId);

            assetIds.forEach((id) => {
                offer.addMyItem({
                    appid: 570,
                    contextid: "2",
                    assetid: String(id)
                });
            });

            if (token) {
                offer.setToken(token);
            }

            offer.send((err, status) => {
                if (err) {
                    console.error("Error al enviar oferta:", err.message);

                    reject({
                        success: false,
                        error: err.message
                    });

                    return;
                }

                console.log(`Estado: ${status}`);

                if (status === "pending" || status === "created") {
                    console.log("Oferta pendiente de confirmación");

                    const tradeId = offer.id || status;

                    resolve({
                        success: true,
                        trade_id: String(tradeId),
                        trade_url: `https://steamcommunity.com/tradeoffer/${tradeId}`,
                        steam_app_url: `steam://openurl/https://steamcommunity.com/tradeoffer/${tradeId}`,
                        message: "Oferta creada. Puede necesitar confirmación manual.",
                        necesita_confirmacion: true
                    });

                    return;
                }

                const tradeId = typeof status === "number" ? status : offer.id || status;

                resolve({
                    success: true,
                    trade_id: String(tradeId),
                    trade_url: `https://steamcommunity.com/tradeoffer/${tradeId}`,
                    steam_app_url: `steam://openurl/https://steamcommunity.com/tradeoffer/${tradeId}`,
                    message: "Oferta enviada correctamente",
                    necesita_confirmacion: false
                });
            });
        });

    } catch (error) {
        console.error("Error en sendOffer:", error);

        return {
            success: false,
            error: error.message || error.error || "Error desconocido"
        };
    }
}

module.exports = {
    sendOffer
};
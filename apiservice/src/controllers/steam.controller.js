// apiservice\src\controllers\steam.controller.js

const { sendOffer } = require("../services/tradeOffer.service");
const { normalizarAssetIds } = require("../utils/asset.util");

async function comprarItem(req, res, next) {
    try {
        const {
            partnerId,
            assetId,
            token,
            twoFactorCode,
            steam_id
        } = req.body;

        console.log(`partnerId=${partnerId}, assetId=${assetId}`);
        console.log(`Comprador: ${steam_id}`);

        const assetIds = normalizarAssetIds(assetId);

        if (assetIds.length === 0) {
            return res.status(400).json({
                success: false,
                error: "No hay items para enviar"
            });
        }

        const result = await sendOffer(
            partnerId,
            assetIds,
            token,
            twoFactorCode,
            steam_id
        );

        return res.json(result);

    } catch (error) {
        next(error);
    }
}

module.exports = {
    comprarItem
};
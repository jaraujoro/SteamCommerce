function normalizarAssetIds(assetId) {
    let assetIds = assetId;

    if (!Array.isArray(assetIds)) {
        if (typeof assetIds === "string") {
            try {
                assetIds = JSON.parse(assetIds);
            } catch (error) {
                assetIds = [assetIds];
            }
        } else {
            assetIds = assetIds ? [assetIds] : [];
        }
    }

    return assetIds;
}

module.exports = {
    normalizarAssetIds
};
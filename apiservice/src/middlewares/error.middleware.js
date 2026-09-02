function errorMiddleware(error, req, res, next) {
    console.error("Error:", error.message || error);

    res.status(error.statusCode || 500).json({
        success: false,
        error: error.message || "Error interno del servidor"
    });
}

module.exports = errorMiddleware;
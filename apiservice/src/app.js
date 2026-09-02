const express = require("express");

const steamRoutes = require("./routes/steam.routes");
const errorMiddleware = require("./middlewares/error.middleware");
const apiKeyMiddleware = require("./middlewares/apiKey.middleware");

const app = express();

app.use(express.json());

app.get("/", (req, res) => {
    res.json({
        success: true,
        service: "apiservice",
        message: "Servicio Node para Steam activo"
    });
});

app.get("/health", (req, res) => {
    res.json({
        success: true,
        status: "OK"
    });
});

// Rutas protegidas con clave secreta
app.use("/api/steam", apiKeyMiddleware, steamRoutes);

app.use((req, res) => {
    res.status(404).json({
        success: false,
        error: "Ruta no encontrada"
    });
});

app.use(errorMiddleware);

module.exports = app;
const express = require("express");
const router = express.Router();

const steamController = require("../controllers/steam.controller");

router.post("/comprar-item", steamController.comprarItem);

module.exports = router;
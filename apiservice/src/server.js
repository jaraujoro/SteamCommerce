const app = require("./app");
const configureDNS = require("./config/dns");
const { PORT } = require("./config/env");

configureDNS();

app.listen(PORT, () => {
    console.log(`apiservice ejecutándose en http://localhost:${PORT}`);
});
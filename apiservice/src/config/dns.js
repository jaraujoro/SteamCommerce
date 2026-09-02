const dns = require("dns");

function configureDNS() {
    dns.setServers(["8.8.8.8", "1.1.1.1"]);

    console.log("DNS configurado:", dns.getServers());

    const originalLookup = dns.lookup;

    dns.lookup = function (hostname, options, callback) {
        if (typeof options === "function") {
            callback = options;
            options = {};
        }

        options.family = 4;

        console.log(`Resolviendo: ${hostname}`);

        return originalLookup.call(this, hostname, options, (err, address, family) => {
            if (err) {
                console.error(`Error resolviendo ${hostname}:`, err.message);
            } else {
                console.log(`${hostname} → ${address}`);
            }

            callback(err, address, family);
        });
    };
}

module.exports = configureDNS;
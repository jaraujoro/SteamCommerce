// apiservice\src\services\steamAuth.service.js

const SteamCommunity = require("steamcommunity");
const TradeOfferManager = require("steam-tradeoffer-manager");

const {
    STEAM_USERNAME,
    STEAM_PASSWORD,
    STEAM_SESSION_HOURS
} = require("../config/env");

let steamSession = null;
let loginPromise = null;

const MAX_SESSION_AGE_MS = STEAM_SESSION_HOURS * 60 * 60 * 1000;

function loginSteamCommunity(twoFactorCode = null) {
    return new Promise((resolve, reject) => {
        if (!STEAM_USERNAME || !STEAM_PASSWORD) {
            reject({
                success: false,
                error: "Credenciales de Steam no configuradas en .env"
            });
            return;
        }

        console.log(`Iniciando login con: ${STEAM_USERNAME}`);

        const community = new SteamCommunity();

        const loginOptions = {
            accountName: STEAM_USERNAME,
            password: STEAM_PASSWORD
        };

        if (twoFactorCode && twoFactorCode.length > 0) {
            loginOptions.twoFactorCode = twoFactorCode;
        }

        community.login(loginOptions, (err, sessionID, cookies) => {
            if (err) {
                console.error("Error en login:", err.message);

                reject({
                    success: false,
                    error: err.message + " Invalid",
                    needs2FA:
                        err.message?.includes("SteamGuard") ||
                        err.message?.includes("TwoFactor")
                });

                return;
            }

            console.log("Login exitoso");

            const manager = new TradeOfferManager({
                community: community,
                domain: "localhost",
                language: "es"
            });

            manager.setCookies(cookies, (setCookiesErr) => {
                if (setCookiesErr) {
                    console.error("Error en setCookies:", setCookiesErr.message);

                    reject({
                        success: false,
                        error: setCookiesErr.message
                    });

                    return;
                }

                console.log("Sesión del manager lista");

                resolve({
                    community,
                    manager,
                    sessionID,
                    cookies,
                    loggedAt: Date.now()
                });
            });
        });
    });
}

function sessionIsValid() {
    if (!steamSession) {
        return false;
    }

    if (!steamSession.manager || !steamSession.community) {
        return false;
    }

    const sessionAge = Date.now() - steamSession.loggedAt;

    if (sessionAge > MAX_SESSION_AGE_MS) {
        console.log(`La sesión superó ${STEAM_SESSION_HOURS} horas. Se hará login nuevamente.`);
        return false;
    }

    return true;
}

async function getSteamSession(twoFactorCode = null) {
    if (sessionIsValid()) {
        console.log("Usando sesión existente de Steam");
        return steamSession;
    }

    if (loginPromise) {
        console.log("Ya hay un login en proceso. Esperando...");
        return loginPromise;
    }

    loginPromise = loginSteamCommunity(twoFactorCode)
        .then((session) => {
            steamSession = session;
            return session;
        })
        .finally(() => {
            loginPromise = null;
        });

    return loginPromise;
}

function resetSteamSession() {
    steamSession = null;
    loginPromise = null;
}

module.exports = {
    loginSteamCommunity,
    getSteamSession,
    resetSteamSession
};
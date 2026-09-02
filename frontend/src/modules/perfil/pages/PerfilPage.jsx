import { useState } from "react";
import { useAuth } from "../../auth/context/AuthContext";
import { guardarTradeUrl } from "../services/perfilService";

const PerfilPage = () => {
  const { usuario } = useAuth();
  const [tradeUrl, setTradeUrl] = useState("");
  const [estaGuardando, setEstaGuardando] = useState(false);
  const [mensajeExito, setMensajeExito] = useState(null);
  const [mensajeError, setMensajeError] = useState(null);

  if (!usuario) {
    return (
      <div className="flex justify-center items-center min-h-[50vh]">
        <p className="text-[#8f9aa7]">Inicia sesión para ver tu perfil</p>
      </div>
    );
  }

  const manejarGuardar = async () => {
    // if (!tradeUrl.includes("steamcommunity.com/tradeoffer/new/")) {
    //   setMensajeError("La URL no parece válida. Asegúrate de copiarla desde Steam.");
    //   return;
    // }

    setEstaGuardando(true);
    setMensajeError(null);
    setMensajeExito(null);

    try {
      const respuesta = await guardarTradeUrl(tradeUrl);
      setMensajeExito(respuesta.message);
    } catch (error) {
      setMensajeError(error.message);
    } finally {
      setEstaGuardando(false);
    }
  };

  return (
    <div className="p-4 max-w-xl mx-auto">
      {/* Info del usuario */}
      <div className="bg-[#2a475e] bg-opacity-30 border border-[#3a6a8a] rounded-lg p-4 mb-4 flex items-center gap-4">
        {usuario.avatar && (
          <img
            src={usuario.avatar}
            alt={usuario.nombre}
            className="w-16 h-16 rounded-full border-2 border-[#66c0f4]"
          />
        )}
        <div>
          <p className="text-white font-semibold text-lg">{usuario.nombre}</p>
          {/* <p className="text-[#8f9aa7] text-xs">Steam ID: {usuario.steam_id}</p> */}
        </div>
      </div>

      {/* Trade URL */}
      <div className="bg-[#2a475e] bg-opacity-30 border border-[#3a6a8a] rounded-lg p-4">
        <h2 className="text-white font-semibold mb-1">URL de intercambio</h2>
        <p className="text-xs text-[#8f9aa7] mb-3">
          Necesaria para recibir items cuando alguien te compre. Encuéntrala en{" "}
          <a
            href="https://steamcommunity.com/my/tradeoffers/privacy"
            target="_blank"
            rel="noreferrer"
            className="text-[#66c0f4] underline"
          >
            Privacidad de Steam
          </a>
        </p>

        <input
          type="text"
          value={tradeUrl}
          onChange={(e) => setTradeUrl(e.target.value)}
          placeholder="https://steamcommunity.com/tradeoffer/new/?partner=...&token=..."
          className="w-full bg-[#1b2838] text-white text-xs border border-[#3a6a8a] rounded px-3 py-2 mb-2 focus:outline-none focus:border-[#66c0f4]"
        />

        {mensajeError && <p className="text-red-400 text-xs mb-2">{mensajeError}</p>}
        {mensajeExito && <p className="text-green-400 text-xs mb-2">{mensajeExito}</p>}

        <button
          onClick={manejarGuardar}
          disabled={estaGuardando}
          className="bg-[#66c0f4] text-[#1b2838] text-sm font-semibold px-4 py-2 rounded hover:bg-white transition-all disabled:opacity-50"
        >
          {estaGuardando ? "Guardando..." : "Guardar"}
        </button>
      </div>
    </div>
  );
};

export default PerfilPage;
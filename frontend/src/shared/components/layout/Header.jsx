import { Menu, X } from "lucide-react";
import { useState } from "react";
import { useAuth } from "../../../modules/auth/context/AuthContext";
import { useCarrito } from "../../../modules/carrito/context/CarritoContext";
import { steamLogin } from "../../../modules/auth/services/authService";
import CarritoCompras from "../../../modules/carrito/components/CarritoCompra";

export default function Header({ sidebarOpen, toggleSidebar }) {
  const [ mostrarPerfil, setMostrarPerfil] = useState(false);
  const { usuario, cargando, logout } = useAuth();
  const { carrito, vaciarCarrito, toggleCarrito } = useCarrito();  

  if (cargando) {
    return (
      <header className="fixed top-0 left-0 right-0 z-30 bg-[#1b2838] border-b border-[#2a475e] h-16">
        <div className="flex items-center justify-between h-full px-4">
          <div className="text-[#8f9aa7]">Cargando...</div>
        </div>
      </header>
    );
  }

  const alternarPerfil = () => setMostrarPerfil(!mostrarPerfil);

  const handleSteamLogin = async () => {
    try {
      const response = await steamLogin();
      window.location.href = response.loginUrl;
    } catch (error) {
      alert("Error al conectar con Steam" + error);
    }
  };

  const handleLogout = () => {
    logout();
    window.location.href = "/";
  };

  return (
    <header className="fixed top-0 left-0 right-0 z-30 bg-[#1b2838] border-b border-[#2a475e] h-16">
      <div className="flex items-center justify-between h-full px-4">
        {/* Menú */}
        <div className="flex items-center gap-3">
          <button
            onClick={toggleSidebar}
            className="p-2 rounded hover:bg-[#2a475e]/50 transition-colors text-[#8f9aa7] hover:text-white"
          >
            {sidebarOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>

        {/* Derecha: Carrito + Usuario */}
        <div className="flex items-center gap-2">
          
          {/* ⭐ CARRITO AQUÍ */}
          <CarritoCompras 
            carrito={carrito}
            onVaciar={vaciarCarrito}
            onToggleItem={toggleCarrito}
          />

          {/* Usuario */}
          <div className="mr-2">
            {usuario && usuario.steam_id ? (
              <div className="flex items-center gap-2">
                {usuario.avatar ? (
                  <img
                    src={usuario.avatar}
                    alt={usuario.nombre}
                    className="w-8 h-8 rounded-full border border-[#66c0f4]"
                  />
                ) : (
                  <div className="w-8 h-8 bg-gradient-to-b from-[#66c0f4] to-[#4a9fd8] rounded-full flex items-center justify-center text-white font-medium text-sm">
                    {usuario.nombre?.charAt(0) || "?"}
                  </div>
                )}
                <span className="text-sm text-white font-medium leading-tight">
                  {usuario.nombre || "Usuario"}
                </span>
              </div>
            ) : (
              <button
                onClick={handleSteamLogin}
                className="bg-[#1b2838] text-white border border-[#66c0f4] px-4 py-1.5 rounded text-sm hover:bg-[#2a475e] transition-colors flex items-center gap-2"
              >
                <img
                  src="./steam.png"
                  alt="Steam"
                  className="w-4 h-4"
                />
                Steam
              </button>
            )}
          </div>

          {/* Perfil */}
          {usuario && usuario.steam_id && (
            <div className="relative">
              <button
                onClick={alternarPerfil}
                className="p-2 rounded hover:bg-[#2a475e]/50 transition-colors"
              >
                <svg
                  className="w-5 h-5 text-[#8f9aa7]"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M19 9l-7 7-7-7"
                  />
                </svg>
              </button>

              {mostrarPerfil && (
                <div className="absolute right-0 mt-2 w-48 bg-[#1b2838] rounded-lg shadow-2xl border border-[#2a475e] py-1">
                  <a
                    href="/perfil"
                    onClick={() => setMostrarPerfil(false)}
                    className="w-full px-4 py-2.5 text-left hover:bg-[#2a475e]/30 transition-colors text-sm flex items-center gap-2 text-[#66c0f4]"
                  >
                    Mi Perfil
                  </a>

                  <button
                    onClick={handleLogout}
                    className="w-full px-4 py-2.5 text-left hover:bg-[#2a475e]/30 transition-colors text-sm flex items-center gap-2 text-[#ff6b6b] hover:text-[#ff4444]"
                  >
                    Cerrar Sesión
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
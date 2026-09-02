import { useState, useEffect } from "react";
import { createPortal } from "react-dom";
import { comprarItem } from "../services/carritoService";
import Modal2FA from "./ModalF2A";
import ModalResultado from "./ModalResultadoCompra";

const CarritoCompras = ({ carrito, onVaciar, onToggleItem }) => {
  
  const [abierto, setAbierto] = useState(false);
  const [mostrarModal2FA, setMostrarModal2FA] = useState(false);
  const [mostrarModalResultado, setMostrarModalResultado] = useState(false);
  const [codigo2FA, setCodigo2FA] = useState("");
  const [enviando, setEnviando] = useState(false);
  const [resultadoCompra, setResultadoCompra] = useState(null);
  const totalItems = carrito.length;
  const totalPrecio = carrito.reduce((sum, item) => sum + (Number(item.precioVenta) || 0), 0);

  const formatearPrecio = (precio) => {
    if (!precio || precio === 0) return "S/. 0.00";
    return `S/. ${Number(precio).toFixed(2)}`;
  };

  // Manejar compra de todos los items
  const manejarComprarTodo = () => {
    if (totalItems === 0) return;
    setMostrarModal2FA(true);
    setCodigo2FA("");
  };

  // Ejecutar compra de todos los items
  const ejecutarCompra = async () => {
    if (!codigo2FA || codigo2FA.length !== 5) {
      setResultadoCompra({
        success: false,
        message: "Código inválido",
        necesita_confirmacion: false,
        trade_url: null,
        steam_app_url: null,
      });
      setMostrarModalResultado(true);
      return;
    }

    setEnviando(true);

    try {
      const publicIds = carrito.map((item) => item.publicId);

      const response = await comprarItem(publicIds, codigo2FA);

      if (response.success) {
        setResultadoCompra({
          ...response,
          message: `¡${totalItems} items comprados exitosamente!`,
          items_comprados: totalItems,
        });
        setMostrarModal2FA(false);
        setCodigo2FA("");
        onVaciar();
      } else {
        setResultadoCompra({
          success: false,
          message: response.error || response.message || "Error desconocido",
          necesita_confirmacion: false,
          trade_url: null,
          steam_app_url: null,
        });
        setMostrarModal2FA(false);
      }
      setMostrarModalResultado(true);
    } catch (error) {
      setResultadoCompra({
        success: false,
        message: error.message,
        necesita_confirmacion: false,
        trade_url: null,
        steam_app_url: null,
      });
      setMostrarModalResultado(true);
      setMostrarModal2FA(false);
    }

    setEnviando(false);
  };

  const handleCancelar2FA = () => {
    setMostrarModal2FA(false);
    setCodigo2FA("");
    setEnviando(false);
  };

  // Efecto para bloquear scroll cuando el sidebar está abierto
  useEffect(() => {
    if (abierto) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [abierto]);

  return (
    <>
      {/* Botón del carrito - Mejorado y más visible */}
      <div className="relative">
        <button
          onClick={() => setAbierto(!abierto)}
          className="bg-[#1b2838] text-white border border-[#66c0f4] px-4 py-1.5 rounded text-sm hover:bg-[#2a475e] transition-colors flex items-center gap-2"
        >
          <img 
            src="./carrito_compras.png" 
            alt="Carrito" 
            className="w-4 h-4"
          />
          {/* <span className="hidden sm:inline">Carritoooooo</span> */}

          {totalItems > 0 && (
            <span className="absolute -top-2 -right-2 bg-gradient-to-br from-[#ff6b35] to-[#e63946] text-white text-[10px] sm:text-xs font-bold min-w-[22px] min-h-[22px] w-[22px] h-[22px] sm:w-6 sm:h-6 md:w-7 md:h-7 rounded-full flex items-center justify-center shadow-lg ring-2 ring-[#ff6b35]/50 animate-bounce">
              {totalItems > 99 ? '99+' : totalItems}
            </span>
          )}
        </button>
      </div>

      {/* Sidebar del Carrito - Mejorado con más espacio */}
      {abierto &&
        createPortal(
          <>
            {/* Overlay oscuro con blur */}
            <div
              className="fixed inset-0 bg-black/60 backdrop-blur-sm z-40 transition-opacity duration-300"
              onClick={() => setAbierto(false)}
            />

            {/* Sidebar - Más espacioso */}
            <div className="fixed top-0 right-0 h-full w-full sm:w-[400px] md:w-[460px] lg:w-[500px] max-w-[100vw] bg-gradient-to-br from-[#1b2838] via-[#162230] to-[#0f1a24] border-l-2 border-[#3a6a8a] shadow-2xl shadow-black/70 z-50 flex flex-col animate-slide-in-right">
              
              {/* Header - Más grande y con más detalles */}
              <div className="px-4 sm:px-6 py-4 sm:py-5 bg-gradient-to-r from-[#2a475e]/40 to-transparent border-b-2 border-[#2a475e] flex justify-between items-center">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-[#2a475e]/50 rounded-lg">
                    <img 
                      src="./carrito_compras.png" 
                      alt="Carrito" 
                      className="w-5 h-5 sm:w-6 sm:h-6" 
                    />
                  </div>
                  <div>
                    <h3 className="text-white text-base sm:text-lg font-bold tracking-wide">
                      Mi Carrito
                    </h3>
                    <p className="text-[#8f9aa7] text-xs sm:text-sm">
                      {totalItems} {totalItems === 1 ? 'item' : 'items'} en tu carrito
                    </p>
                  </div>
                </div>
                
                <div className="flex items-center gap-3">
                  <div className="hidden sm:block text-right">
                    <p className="text-[#8f9aa7] text-xs">Total</p>
                    <p className="text-[#ffa500] text-sm font-bold">
                      {formatearPrecio(totalPrecio)}
                    </p>
                  </div>
                  <button
                    onClick={() => setAbierto(false)}
                    className="text-[#8f9aa7] hover:text-white p-2 rounded-xl hover:bg-[#2a475e]/50 transition-all duration-200 hover:scale-110"
                    aria-label="Cerrar carrito"
                  >
                    <svg className="w-5 h-5 sm:w-6 sm:h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              </div>

              {/* Resumen rápido - Solo visible en móvil */}
              <div className="sm:hidden px-4 py-2 bg-[#2a475e]/20 border-b border-[#2a475e]/50 flex justify-between items-center">
                <span className="text-[#8f9aa7] text-xs">Total</span>
                <span className="text-[#ffa500] text-sm font-bold">{formatearPrecio(totalPrecio)}</span>
              </div>

              {/* Lista de items - Más espaciosa */}
              <div className="flex-1 overflow-y-auto p-3 sm:p-4 scrollbar-thin scrollbar-track-[#1b2838] scrollbar-thumb-[#2a475e] hover:scrollbar-thumb-[#3a6a8a]">
                {totalItems === 0 ? (
                  <div className="flex flex-col items-center justify-center h-full text-center py-12">
                    <div className="w-20 h-20 sm:w-24 sm:h-24 bg-[#2a475e]/30 rounded-full flex items-center justify-center mb-4">
                      <img 
                        src="./carrito_compras.png" 
                        alt="Carrito vacío" 
                        className="w-10 h-10 sm:w-12 sm:h-12 opacity-50" 
                      />
                    </div>
                    <p className="text-[#8f9aa7] text-base sm:text-lg font-medium">
                      El carrito está vacío
                    </p>
                    <p className="text-[#4a6a7a] text-sm sm:text-base mt-2">
                      Agrega items para continuar
                    </p>
                  </div>
                ) : (
                  <div className="space-y-2 sm:space-y-3">
                    {carrito.map((item) => (
                      <div
                        key={item.publicId}
                        className="flex items-center gap-3 sm:gap-4 p-3 sm:p-4 bg-[#1b2838]/50 hover:bg-[#2a475e]/40 rounded-xl transition-all duration-300 group border border-transparent hover:border-[#3a6a8a]/40 hover:shadow-lg hover:shadow-[#3a6a8a]/10"
                      >
                        {/* Imagen - Más grande */}
                        <div className="relative flex-shrink-0">
                          {item.iconUrl ? (
                            <img
                              src={item.iconUrl}
                              alt={item.marketHashName}
                              className="w-12 h-12 sm:w-14 sm:h-14 object-contain rounded-lg bg-[#1b2838] p-1.5 border border-[#2a475e]"
                            />
                          ) : (
                            <div className="w-12 h-12 sm:w-14 sm:h-14 rounded-lg bg-[#2a475e]/30 flex items-center justify-center">
                              <span className="text-[#4a6a7a] text-xs">Sin imagen</span>
                            </div>
                          )}                          
                        </div>

                        {/* Información del item - Más detallada */}
                        <div className="flex-1 min-w-0">
                          <p className="text-white text-sm sm:text-base font-medium truncate">
                            {item.marketHashName}
                          </p>
                          <div className="flex items-center gap-3 mt-1">
                            {item.precio_venta && (
                              <span className="text-[#ffa500] text-xs sm:text-xs font-semibold">
                                S/{item.precioVenta}
                              </span>
                            )}
                            <span className="text-[#6a8a9a] text-[10px] sm:text-xs">
                              Héroe: {item.hero || 'Otros'}
                            </span>
                          </div>
                        </div>
                        
                        {/* Botón quitar - Más grande y visible */}
                        <button
                          onClick={() => onToggleItem(item)}
                          className="text-[#6a8a9a] hover:text-red-400 p-2 rounded-xl hover:bg-red-400/10 transition-all duration-200 hover:scale-110 flex-shrink-0 min-w-[36px] min-h-[36px] sm:min-w-[40px] sm:min-h-[40px] flex items-center justify-center"
                          title="Quitar del carrito"
                        >
                          <svg className="w-4 h-4 sm:w-5 sm:h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              {/* Footer con acciones - Más grande y con resumen */}
              <div className="px-4 sm:px-6 py-4 sm:py-5 bg-gradient-to-r from-[#2a475e]/30 to-transparent border-t-2 border-[#2a475e]">
                {/* Resumen de compra - Visible en desktop */}
                {totalItems > 0 && (
                  <div className="hidden sm:flex justify-between items-center mb-4 pb-3 border-b border-[#2a475e]/50">
                    {/* <div>
                      <p className="text-[#8f9aa7] text-xs">Subtotal</p>
                      <p className="text-white text-sm font-medium">{totalItems} items</p>
                    </div> */}
                    <div className="text-right">
                      <p className="text-[#8f9aa7] text-xs">Total a pagar</p>
                      <p className="text-[#ffa500] text-lg font-bold">{formatearPrecio(totalPrecio)}</p>
                    </div>
                  </div>
                )}
                <div className="flex flex-col sm:flex-row gap-3">
                  <button
                    onClick={manejarComprarTodo}
                    disabled={totalItems === 0}
                    className={`w-full sm:flex-1 py-3 sm:py-3.5 rounded-xl text-sm sm:text-base font-bold transition-all duration-300 flex items-center justify-center gap-2 min-h-[52px] sm:min-h-[48px] ${
                      totalItems > 0
                        ? "bg-gradient-to-r from-[#ff6b35] to-[#e63946] hover:from-[#ff8a5c] hover:to-[#ff4757] text-white shadow-lg hover:shadow-[#ff6b35]/40 active:scale-95 hover:scale-[1.02]"
                        : "bg-[#2a475e] text-[#6a8a9a] cursor-not-allowed opacity-50"
                    }`}
                  >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    Comprar Ahora
                    {totalItems > 0 && (
                      <span className="hidden sm:inline ml-1 bg-white/20 px-2 py-0.5 rounded-full text-xs">
                        {totalItems}
                      </span>
                    )}
                  </button>
                  
                  <button
                    onClick={onVaciar}
                    disabled={totalItems === 0}
                    className={`w-full sm:w-auto px-6 py-3 sm:py-3.5 rounded-xl text-sm sm:text-base font-medium transition-all duration-300 flex items-center justify-center gap-2 min-h-[52px] sm:min-h-[48px] ${
                      totalItems > 0
                        ? "bg-[#2a475e]/60 hover:bg-[#3a5a6e] text-[#8f9aa7] hover:text-white border-2 border-[#3a6a8a]/40 hover:border-[#66c0f4]/40 hover:scale-[1.02] active:scale-95"
                        : "bg-[#2a475e]/30 text-[#4a5a6a] cursor-not-allowed opacity-50"
                    }`}
                  >
                    <svg className="w-4 h-4 sm:w-5 sm:h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                    Vaciar
                  </button>
                </div>
              </div>
            </div>
          </>,
          document.body,
        )}

      {/* Modal 2FA */}
      <Modal2FA
        isOpen={mostrarModal2FA}
        codigo2FA={codigo2FA}
        setCodigo2FA={setCodigo2FA}
        enviando={enviando}
        onConfirmar={ejecutarCompra}
        onCancelar={handleCancelar2FA}
      />

      {/* Modal Resultado */}
      <ModalResultado
        isOpen={mostrarModalResultado}
        resultado={resultadoCompra}
        onCerrar={() => setMostrarModalResultado(false)}
      />
    </>
  );
};

export default CarritoCompras;
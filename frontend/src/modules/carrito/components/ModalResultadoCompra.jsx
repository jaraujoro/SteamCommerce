import { useEffect } from 'react';
import { createPortal } from 'react-dom';

const ModalResultado = ({ isOpen, resultado, onCerrar }) => {
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen]);

  if (!isOpen || !resultado) return null;

  const limpiarTexto = (texto) => {
    if (!texto) return '';
    return texto
      .replace(/<br\s*\/?>/gi, '\n')
      .replace(/&nbsp;/g, ' ')
      .replace(/<[^>]*>/g, '')
      .trim();
  };

  // Si es error, mostrar solo mensaje y botón cerrar
  if (!resultado.success) {
    return createPortal(
      <div 
        className="fixed inset-0 bg-black/80 flex items-center justify-center z-[9999] p-4"
        onClick={(e) => {
          if (e.target === e.currentTarget) {
            onCerrar();
          }
        }}
      >
        <div className="relative bg-[#1b2838] rounded shadow-xl max-w-sm w-full max-h-[90vh] overflow-y-auto border border-[#2a475e]">
          <div className="bg-[#171d26] px-5 py-3 border-b border-[#2a475e]">
            <h2 className="text-[#c7d5e0] text-sm font-medium tracking-wide">❌ Error</h2>
          </div>
          <div className="px-5 py-5">
            <div className="text-center">
              <h3 className="text-base font-medium mb-2 text-[#d95b3a]">
                {limpiarTexto(resultado.message) || 'Error'}
              </h3>
              <div className="w-8 h-px bg-[#2a475e] mx-auto my-3"></div>
              <button
                onClick={onCerrar}
                className="w-full bg-[#2a475e] hover:bg-[#3a6a8a] text-[#c7d5e0] hover:text-white font-medium py-2 px-4 rounded transition-colors duration-200 text-sm"
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      </div>,
      document.body
    );
  }

  return createPortal(
    <div 
      className="fixed inset-0 bg-black/80 flex items-center justify-center z-[9999] p-4"
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          onCerrar();
        }
      }}
    >
      <div className="relative bg-[#1b2838] rounded shadow-xl max-w-sm w-full max-h-[90vh] overflow-y-auto border border-[#2a475e]">
        
        <div className="bg-[#171d26] px-5 py-3 border-b border-[#2a475e]">
          <h2 className="text-[#c7d5e0] text-sm font-medium tracking-wide">Operación exitosa</h2>
        </div>

        <div className="px-5 py-5">
          <div className="text-center">
            
            <h3 className="text-base font-medium mb-2 text-[#a4d07c]">
              Oferta de trade enviada con éxito
            </h3>

            <div className="w-8 h-px bg-[#2a475e] mx-auto my-3"></div>
{/* 
            {resultado.necesita_confirmacion && (
              <p className="text-[#f0c27a] text-xs mb-3">
                ⚠️ Necesita confirmación en la app móvil de Steam
              </p>
            )} */}
         <div className="flex gap-2 mt-3">
              <button
                onClick={() => window.open(resultado.trade_url, '_blank')}
                className="flex-1 bg-[#2a475e] hover:bg-[#3a6a8a] text-[#c7d5e0] hover:text-white font-medium py-1.5 px-2 rounded transition-colors duration-200 text-xs flex items-center justify-center gap-1"
              >
              Aceptar Steam Web
              </button>
              
              <button
                onClick={() => window.open(resultado.steam_app_url, '_blank')}
                className="flex-1 bg-[#1a3a4a] hover:bg-[#2a5a6a] text-[#c7d5e0] hover:text-white font-medium py-1.5 px-2 rounded transition-colors duration-200 text-xs flex items-center justify-center gap-1 border border-[#2a475e]"
              >
              Aceptar Steam Aplicación
              </button>
            </div>


            <button
              onClick={onCerrar}
              className="mt-4 text-[#5c6b7a] hover:text-[#8f9aa7] text-xs transition-colors duration-200"
            >
              Cerrar
            </button>
          </div>
        </div>
      </div>
    </div>,
    document.body
  );
};

export default ModalResultado;
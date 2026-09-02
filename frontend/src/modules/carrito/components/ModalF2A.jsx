import { useRef, useEffect } from 'react';
import { createPortal } from 'react-dom';

const Modal2FA = ({ 
  isOpen, 
  codigo2FA, 
  setCodigo2FA, 
  enviando, 
  onConfirmar, 
  onCancelar
}) => {
  const inputRef = useRef(null);

  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
      // Auto-focus al input
      setTimeout(() => {
        if (inputRef.current) {
          inputRef.current.focus();
        }
      }, 100);
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isOpen]);

  if (!isOpen) return null;

  return createPortal(
    <div 
      className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4"
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          onCancelar();
        }
      }}
    >
      <div className="bg-[#1b2838] rounded-lg border border-[#3a6a8a] max-w-sm w-full max-h-[90vh] overflow-y-auto">
        {/* HEADER */}
        <div className="px-5 pt-4 pb-2 border-b border-[#2a475e]">
          <h3 className="text-white text-base font-semibold">
            Confirmar Compra
          </h3>
        </div>

        {/* CUERPO */}
        <div className="px-5 py-3">
          {/* Input 2FA */}
          <div>
            <label className="text-[#8f9aa7] text-[11px] block mb-1">
              Código Steam Guard (5 dígitos)
            </label>
            <input
              ref={inputRef}
              type="text"
              placeholder="12345"
              value={codigo2FA}
              onChange={(e) => setCodigo2FA(e.target.value)}
              className="w-full bg-[#2a475e] text-white border border-[#3a6a8a] rounded px-3 py-1.5 text-center text-base tracking-widest"
              maxLength={5}
              disabled={enviando}
            />
            <p className="text-[#8f9aa7] text-[10px] mt-1">
              Abre Steam en tu móvil y busca el código
            </p>
          </div>
        </div>

        {/* FOOTER */}
        <div className="px-5 py-3 border-t border-[#2a475e] flex gap-2">
          <button
            onClick={onConfirmar}
            disabled={codigo2FA.length !== 5 || enviando}
            className={`flex-1 py-1.5 rounded text-sm font-medium transition-colors ${
              codigo2FA.length === 5 && !enviando
                ? 'bg-[#ffa500] hover:bg-[#e69500] text-white'
                : 'bg-[#2a475e] text-[#6a8a9a] cursor-not-allowed'
            }`}
          >
            {enviando ? 'Procesando...' : 'Confirmar'}
          </button>
          <button
            onClick={onCancelar}
            className="flex-1 bg-[#2a475e] hover:bg-[#3a5a6e] text-[#8f9aa7] hover:text-white py-1.5 rounded text-sm font-medium transition-colors"
            disabled={enviando}
          >
            Cancelar
          </button>
        </div>

        {/* ESTADO DE ENVÍO */}
        {enviando && (
          <div className="px-5 pb-3 text-center">
            <span className="text-[#ffa500] text-[11px] animate-pulse">
              Procesando solicitud...
            </span>
          </div>
        )}
      </div>
    </div>,
    document.body
  );
};

export default Modal2FA;
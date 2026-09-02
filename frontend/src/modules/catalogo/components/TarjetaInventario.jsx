import { useState } from "react";

const TarjetaInventario = ({ item, enCarrito, onToggleCarrito }) => {
  const [hover, setHover] = useState(false);

  const estaSeleccionado = enCarrito(item.publicId);
  const esIntercambiable = item.tradable;

  // Color directamente de la base de datos
  const colorPrincipal = item.color ? `#${item.color}` : '#b0b0b0';

  const formatearPrecio = (precio) => {
    if (!precio || precio === 0) return "S/. 0.00";
    return `S/. ${Number(precio).toFixed(2)}`;
  };

  const getCardStyles = () => {
    const baseStyles = "rounded-2xl transition-all duration-300";
    
    if (!esIntercambiable) {
      return `${baseStyles} bg-gray-800/20 border border-gray-700/30 opacity-50 cursor-not-allowed`;
    }
    if (estaSeleccionado) {
      return `${baseStyles} bg-gradient-to-br from-orange-500/10 to-orange-600/5 border-2 border-orange-400/50 cursor-pointer shadow-lg shadow-orange-500/10 hover:shadow-orange-500/20`;
    }
    return `${baseStyles} bg-gray-800/20 border border-gray-700/30 cursor-pointer hover:border-white/20 hover:bg-gray-800/30 hover:shadow-xl hover:shadow-black/20`;
  };

  const handleClick = () => {
    if (esIntercambiable) {
      onToggleCarrito(item);
    }
  };

  return (
    <div
      className={`relative overflow-hidden p-3 sm:p-4 md:p-5 ${getCardStyles()}`}
      onClick={handleClick}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      title={
        !esIntercambiable
          ? "Item no intercambiable"
          : estaSeleccionado
            ? "Click para quitar del carrito"
            : "Click para agregar al carrito"
      }
    >
      {/* Glow de rareza usando el color de la DB */}
      <div 
        className="absolute -top-20 -right-20 w-40 h-40 rounded-full blur-3xl transition-opacity duration-500 pointer-events-none"
        style={{ 
          background: colorPrincipal,
          opacity: hover && esIntercambiable ? 0.15 : 0
        }}
      />

      {/* Badges - Reposicionados para mobile */}
      <div className="absolute top-2 sm:top-3 right-2 sm:right-3 z-10 flex flex-col gap-1.5 sm:gap-2">
        {estaSeleccionado && (
          <span className="bg-gradient-to-r from-orange-400 to-orange-500 text-white text-[10px] sm:text-xs font-bold px-2 sm:px-3 py-1 sm:py-1.5 rounded-full shadow-lg shadow-orange-500/30 flex items-center gap-1 sm:gap-1.5 whitespace-nowrap">
            <span className="hidden xs:inline">Seleccionado</span>
            <span className="xs:hidden">Seleccionado</span>
          </span>
        )}

        {!esIntercambiable && (
          <span className="bg-red-500/80 backdrop-blur-sm text-white text-[10px] sm:text-xs font-bold px-2 sm:px-3 py-1 sm:py-1.5 rounded-full shadow-lg shadow-red-500/30 flex items-center gap-1 sm:gap-1.5 whitespace-nowrap">
            <svg className="w-3 h-3 sm:w-4 sm:h-4" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
            </svg>
            <span className="hidden xs:inline">Bloqueado</span>
            <span className="xs:hidden">🔒</span>
          </span>
        )}
      </div>

      {/* Imagen - Más pequeña en mobile */}
      <div className="relative mb-3 sm:mb-4">
        <div className="relative rounded-xl overflow-hidden bg-black/40 p-2 sm:p-3">
          {item.iconUrl && (
            <img
              src={item.iconUrl}
              alt={item.marketHashName}
              className={`w-full h-24 sm:h-28 md:h-32 object-contain transition-transform duration-500 ${
                hover && esIntercambiable && !estaSeleccionado ? "scale-110" : ""
              }`}
            />
          )}
          
          {/* Borde de rareza usando el color de la DB */}
          <div 
            className="absolute inset-0 rounded-xl border-2 transition-all duration-300"
            style={{ 
              borderColor: colorPrincipal,
              opacity: hover && esIntercambiable ? 1 : 0.4
            }}
          />
        </div>
      </div>

      {/* Información - Texto más pequeño en mobile */}
      <div className="space-y-1.5 sm:space-y-2">
        <h3
          className="text-sm sm:text-base font-bold truncate"
          style={{ color: colorPrincipal }}
          title={item.marketHashName}
        >
          {item.marketHashName || item.name || "Sin nombre"}
        </h3>
        
        <div className="flex flex-wrap items-center gap-1.5 sm:gap-2">
          <span 
            className="text-[10px] sm:text-xs font-semibold px-2 py-0.5 rounded-full bg-black/40"
            style={{ color: colorPrincipal }}
          >
            {item.rarity || "Sin rareza"}
          </span>
          {item.type && (
            <span className="text-[10px] sm:text-xs text-gray-400">{item.type}</span>
          )}
        </div>

        <div className="flex items-center gap-1.5 sm:gap-2 text-xs sm:text-sm">
          <span className="text-gray-400">Héroe:</span>
          <span className="font-semibold text-sm sm:text-base" style={{ color: colorPrincipal }}>
            {item.hero || "Sin héroe"}
          </span>
        </div>
      </div>

      {/* Precios - Layout horizontal en mobile */}
      <div className="mt-3 sm:mt-4 pt-3 sm:pt-4 border-t border-gray-700/20">
        <div className="flex flex-col xs:flex-row xs:items-end justify-between gap-1.5 xs:gap-2">
          <div className="flex items-baseline gap-1">
            <span className="text-sm sm:text-base font-bold text-white tracking-tight">
              {formatearPrecio(item.precioVenta)}
            </span>
            <span className="text-[10px] sm:text-xs text-gray-400">PEN</span>
          </div>
          <div className="flex items-center gap-1 bg-black/30 px-2 py-1 rounded-full">
            <img
              src="./steam.png"
              alt="Steam"
              className="w-3 h-3 sm:w-4 sm:h-4 object-contain opacity-60"
            />
            <span className="text-[10px] sm:text-xs text-gray-400 line-through">
              {formatearPrecio(item.precioMercado)}
            </span>
          </div>
        </div>
      </div>

      {/* Estado - Más compacto en mobile */}
      <div className="mt-2 sm:mt-3">
        <div className="flex items-center gap-1.5 sm:gap-2">
          <div className={`w-1.5 h-1.5 sm:w-2 sm:h-2 rounded-full ${esIntercambiable ? 'bg-green-400 animate-pulse' : 'bg-red-400'}`} />
          <span className={`text-[10px] sm:text-xs ${esIntercambiable ? 'text-green-400/80' : 'text-red-400/80'}`}>
            {esIntercambiable ? 'Disponible' : 'No intercambiable'}
          </span>
        </div>
      </div>
    </div>
  );
};

export default TarjetaInventario;
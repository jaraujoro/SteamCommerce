import { useState } from "react";

const TarjetaInventario = ({ item, enCarrito, onToggleCarrito, onReservar }) => {
  const [hover, setHover] = useState(false);

  const estaSeleccionado = enCarrito(item.publicId);
  const esIntercambiable = item.tradable;

  const calcularTiempoRestante = (fechaIso) => {
    if (!fechaIso) return null;
    const diffMs = new Date(fechaIso) - new Date();
    if (diffMs <= 0) return null;

    const totalMin = Math.floor(diffMs / 60000);
    const d = Math.floor(totalMin / 1440);
    const h = Math.floor((totalMin % 1440) / 60);
    const m = totalMin % 60;

    if (d > 0) return `${d}d ${h}h`;
    if (h > 0) return `${h}h ${m}m`;
    return `${m}m`;
  };

  const tiempoRestante = calcularTiempoRestante(item.tradeCooldownUntil);
  const enCooldown = !esIntercambiable && tiempoRestante !== null;
  const bloqueadoPermanente = !esIntercambiable && tiempoRestante === null;

  const colorPrincipal = item.color ? `#${item.color}` : "#b0b0b0";

  const formatearPrecio = (p, moneda = "S/.") =>
    `${moneda} ${Number(p || 0).toFixed(2)}`;

  const getCardStyles = () => {
    // 👇 ancho mínimo + alto mínimo para que sea más grande
    const base = "rounded-xl transition-all duration-300 overflow-hidden w-full min-w-[220px] min-h-[380px] flex flex-col";
    if (bloqueadoPermanente) return `${base} bg-gray-900/60 border border-gray-700/30 opacity-60 cursor-not-allowed`;
    if (enCooldown) return `${base} bg-gray-900/60 border border-orange-500/40`;
    if (estaSeleccionado) return `${base} bg-gray-900/60 border-2 border-orange-400/60 cursor-pointer shadow-lg shadow-orange-500/10`;
    return `${base} bg-gray-900/60 border border-gray-700/40 cursor-pointer hover:border-white/30`;
  };

  const handleClick = () => esIntercambiable && onToggleCarrito(item);

  const handleReservar = (e) => {
    e.stopPropagation();
    onReservar?.(item);
  };

  return (
    <div
      className={`relative ${getCardStyles()}`}
      onClick={handleClick}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
      title={
        bloqueadoPermanente
          ? "Item no intercambiable"
          : enCooldown
            ? `Se desbloquea en ${tiempoRestante}`
            : estaSeleccionado
              ? "Click para quitar del carrito"
              : "Click para agregar al carrito"
      }
    >
      {/* Glow */}
      <div
        className="absolute -top-20 -right-20 w-40 h-40 rounded-full blur-3xl transition-opacity duration-500 pointer-events-none z-0"
        style={{ background: colorPrincipal, opacity: hover && esIntercambiable ? 0.15 : 0 }}
      />

      {/* HEADER */}
      <div className="absolute top-0 left-0 right-0 z-10 flex items-center justify-between px-2.5 py-2 bg-black/50 backdrop-blur-sm">
        <span
          className="text-[11px] font-bold uppercase tracking-widest flex items-center gap-1"
          style={{ color: colorPrincipal }}
        >
          <span>💠</span>
          <span>{item.rarity || "Sin rareza"}</span>
        </span>

        {enCooldown && (
          <span className="bg-orange-500/90 text-white text-[10px] font-bold px-2 py-0.5 rounded flex items-center gap-1 whitespace-nowrap">
            <span>🛒</span>
            <span>Reservable</span>
          </span>
        )}
        {esIntercambiable && !estaSeleccionado && (
          <span className="text-[10px] font-bold text-emerald-400 flex items-center gap-1 whitespace-nowrap">
            <span>✓</span>
            <span>Entrega inmediata</span>
          </span>
        )}
        {estaSeleccionado && (
          <span className="bg-orange-500 text-white text-[10px] font-bold px-2 py-0.5 rounded flex items-center gap-1 whitespace-nowrap">
            <span>✓</span>
            <span>Seleccionado</span>
          </span>
        )}
        {bloqueadoPermanente && (
          <span className="bg-red-500/80 text-white text-[10px] font-bold px-2 py-0.5 rounded">
            🔒
          </span>
        )}
      </div>

      {/* IMAGEN — más alta */}
      <div className="relative bg-gradient-to-br from-gray-800 to-gray-900 pt-10 pb-4 px-3">
        {item.iconUrl && (
          <img
            src={`https://steamcommunity-a.akamaihd.net/economy/image/${item.iconUrl}`}
            alt={item.marketHashName}
            className={`w-full h-32 object-contain transition-transform duration-500 ${hover && esIntercambiable && !estaSeleccionado ? "scale-110" : ""
              }`}
          />
        )}
        <div
          className="absolute inset-0 pointer-events-none"
          style={{
            boxShadow: `inset 0 0 70px ${colorPrincipal}22`,
            opacity: hover && esIntercambiable ? 1 : 0.6,
          }}
        />
      </div>

      {/* CINTA COOLDOWN */}
      {enCooldown && (
        <div className="bg-gradient-to-r from-orange-500 to-orange-600 px-2.5 py-1.5 flex items-center gap-1.5">
          <span className="text-[11px]">🔒</span>
          <p className="text-[11px] font-bold text-white tracking-wide">
            Se desbloquea en {tiempoRestante}
          </p>
        </div>
      )}

      {/* INFO — con flex-1 para empujar el botón hacia abajo */}
      <div className="px-3 pt-2.5 pb-1 flex-1">
        <p className="text-[10px] text-gray-400 uppercase tracking-wider font-semibold truncate">
          {item.hero || "Other"}
        </p>
        <h3
          className="text-[13px] font-bold leading-tight line-clamp-2 mt-1"
          style={{ color: colorPrincipal }}
          title={item.marketHashName}
        >
          {item.marketHashName || item.name || "Sin nombre"}
        </h3>
      </div>

      {/* PRECIO Y STOCK */}
      <div className="px-3 pb-2">
        <div className="flex items-baseline gap-1.5 flex-wrap">
          <span className="text-[15px] font-bold text-white tracking-tight">
            {formatearPrecio(item.precioVenta)}
          </span>
          <span className="text-[10px] text-gray-500">
            {formatearPrecio(item.precioMercado, "US$")}
          </span>
        </div>

        <p className="text-[10px] text-gray-400 mt-1">
          Stock: <span className="text-white font-semibold">1 unidad</span>
          <span className="text-amber-400"> · Bajo stock</span>
        </p>
      </div>

      {/* BOTÓN */}
      <div className="px-3 pb-3">
        {enCooldown ? (
          <button
            onClick={handleReservar}
            className="w-full bg-transparent hover:bg-orange-500/10 border border-orange-500/50 text-orange-400 font-bold text-[12px] py-2 rounded-md transition-colors flex items-center justify-center gap-1.5 cursor-pointer"
          >
            <span>🎟️</span>
            <span>Reservar</span>
          </button>
        ) : esIntercambiable ? (
          <button
            className={`w-full border font-bold text-[12px] py-2 rounded-md transition-colors flex items-center justify-center gap-1.5 cursor-pointer ${estaSeleccionado
              ? "bg-orange-500/20 border-orange-400 text-orange-300"
              : "bg-transparent hover:bg-blue-500/10 border-blue-500/50 text-blue-400"
              }`}
          >
            <span>🎟️</span>
            <span>{estaSeleccionado ? "Quitar" : "Agregar"}</span>
          </button>
        ) : null}
      </div>
    </div>
  );
};

export default TarjetaInventario;
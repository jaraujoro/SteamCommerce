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

  // Hex → RGBA con alpha
  const withAlpha = (hex, a) => {
    const clean = hex.replace("#", "");
    const bigint = parseInt(
      clean.length === 3
        ? clean.split("").map((c) => c + c).join("")
        : clean,
      16
    );
    const r = (bigint >> 16) & 255;
    const g = (bigint >> 8) & 255;
    const b = bigint & 255;
    return `rgba(${r}, ${g}, ${b}, ${a})`;
  };

  const handleClick = () => esIntercambiable && onToggleCarrito(item);
  const handleReservar = (e) => {
    e.stopPropagation();
    onReservar?.(item);
  };

  const borderColor = bloqueadoPermanente
    ? "rgba(120,120,120,0.35)"
    : estaSeleccionado
      ? "#fb923c"
      : withAlpha(colorPrincipal, hover ? 0.85 : 0.45);

  const shadowColor = bloqueadoPermanente
    ? "rgba(0,0,0,0.4)"
    : estaSeleccionado
      ? "rgba(251,146,60,0.35)"
      : withAlpha(colorPrincipal, hover ? 0.45 : 0.15);

  return (
    <div
      className={`relative rounded-xl overflow-hidden w-full flex flex-col transition-all duration-300 group ${esIntercambiable ? "cursor-pointer" : "cursor-not-allowed"
        } ${bloqueadoPermanente ? "opacity-50" : ""}`}
      style={{
        border: `1.5px solid ${borderColor}`,
        background: `
          radial-gradient(circle at 50% 0%, ${withAlpha(colorPrincipal, 0.18)} 0%, transparent 55%),
          linear-gradient(180deg, #111827 0%, #0b1220 100%)
        `,
        boxShadow: hover
          ? `0 0 0 1px ${withAlpha(colorPrincipal, 0.4)}, 0 10px 30px -8px ${shadowColor}, inset 0 0 40px ${withAlpha(colorPrincipal, 0.1)}`
          : `0 4px 14px -6px ${shadowColor}, inset 0 0 24px ${withAlpha(colorPrincipal, 0.05)}`,
        transform: hover && esIntercambiable ? "translateY(-3px)" : "translateY(0)",
      }}
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
      {/* Shine */}
      <div
        className="absolute inset-0 pointer-events-none z-30 opacity-0 group-hover:opacity-100 transition-opacity duration-500"
        style={{
          background: `linear-gradient(115deg, transparent 30%, ${withAlpha(
            colorPrincipal,
            0.18
          )} 50%, transparent 70%)`,
        }}
      />

      {/* Glow esquina - Reemplazado blur por gradiente suavizado */}
      <div
        className="absolute -top-16 -right-16 w-40 h-40 rounded-full pointer-events-none z-0 transition-opacity duration-500"
        style={{
          background: `radial-gradient(circle, ${withAlpha(colorPrincipal, 0.8)} 0%, transparent 70%)`,
          opacity: hover && esIntercambiable ? 0.28 : 0.1,
        }}
      />

      {/* HEADER */}
      <div
        className="relative z-20 flex items-center justify-between gap-1 px-2 py-1.5"
        style={{
          background: `linear-gradient(90deg, ${withAlpha(
            colorPrincipal,
            0.35
          )} 0%, ${withAlpha(colorPrincipal, 0.05)} 100%)`,
          borderBottom: `1px solid ${withAlpha(colorPrincipal, 0.35)}`,
        }}
      >
        <span
          className="text-[10px] sm:text-[11px] font-extrabold uppercase tracking-widest flex items-center gap-1 truncate min-w-0"
          style={{
            color: colorPrincipal,
            textShadow: `0 0 8px ${withAlpha(colorPrincipal, 0.6)}`,
          }}
        >
          <span className="truncate">{item.rarity || "Sin rareza"}</span>
        </span>

        {enCooldown && (
          <span className="bg-orange-500/95 text-white text-[9px] sm:text-[10px] font-bold px-1.5 sm:px-2 py-0.5 rounded flex items-center gap-1 whitespace-nowrap shrink-0">
            <span className="hidden sm:inline">Reservable</span>
          </span>
        )}
        {esIntercambiable && !estaSeleccionado && (
          <span className="text-[9px] sm:text-[10px] font-bold text-emerald-400 flex items-center gap-1 whitespace-nowrap shrink-0">
            <span className="hidden sm:inline">Entrega inmediata</span>
          </span>
        )}
        {estaSeleccionado && (
          <span className="bg-orange-500 text-white text-[9px] sm:text-[10px] font-bold px-1.5 sm:px-2 py-0.5 rounded flex items-center gap-1 whitespace-nowrap shrink-0 shadow-lg shadow-orange-500/40">
            <span className="hidden sm:inline">Seleccionado</span>
          </span>
        )}
        {bloqueadoPermanente && (
          <span className="bg-red-500/85 text-white text-[9px] sm:text-[10px] font-bold px-1.5 py-0.5 rounded shrink-0">
            <span className="hidden sm:inline">Bloqueado</span>
          </span>
        )}
      </div>

      {/* IMAGEN */}
      <div className="relative z-10 pt-3 pb-2 px-2 sm:px-3">
        <div
          className="relative rounded-lg overflow-hidden"
          style={{
            background: `radial-gradient(circle at 50% 40%, ${withAlpha(
              colorPrincipal,
              0.14
            )} 0%, transparent 70%)`,
            border: `1px solid ${withAlpha(colorPrincipal, 0.18)}`,
          }}
        >
          {item.iconUrl && (
            <img
              src={`https://steamcommunity-a.akamaihd.net/economy/image/${item.iconUrl}`}
              alt={item.marketHashName}
              loading="lazy"
              className={`w-full h-24 sm:h-28 md:h-32 object-contain transition-transform duration-500 ${hover && esIntercambiable && !estaSeleccionado
                ? "scale-110"
                : ""
                }`}
            />
          )}

          <div
            className="absolute inset-x-0 bottom-0 h-1/3 pointer-events-none"
            style={{
              background: `linear-gradient(to top, ${withAlpha(
                colorPrincipal,
                0.15
              )}, transparent)`,
            }}
          />
        </div>
      </div>

      {/* CINTA COOLDOWN */}
      {enCooldown && (
        <div className="relative z-20 bg-gradient-to-r from-orange-500 to-orange-600 px-2 py-1 sm:py-1.5 flex items-center gap-1.5">
          <span className="text-[10px] sm:text-[11px]"></span>
          <p className="text-[10px] sm:text-[11px] font-bold text-white tracking-wide truncate">
            Se desbloquea en {tiempoRestante}
          </p>
        </div>
      )}

      {/* INFO */}
      <div className="relative z-20 px-2 sm:px-3 pt-2 pb-1 flex-1 min-h-0">
        <p className="text-[10px] text-gray-400 uppercase tracking-wider font-semibold truncate">
          {item.hero || "Other"}
        </p>
        <h3
          className="text-xs sm:text-[13px] font-bold leading-tight line-clamp-2 mt-1"
          style={{
            color: colorPrincipal,
            textShadow: hover
              ? `0 0 10px ${withAlpha(colorPrincipal, 0.5)}`
              : "none",
          }}
          title={item.marketHashName}
        >
          {item.marketHashName || item.name || "Sin nombre"}
        </h3>
      </div>

      {/* PRECIO */}
      <div className="relative z-20 px-2 sm:px-3 pb-2">
        <div className="flex items-baseline gap-1.5 flex-wrap">
          <span className="text-sm sm:text-[15px] font-extrabold text-white tracking-tight">
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
      <div className="relative z-20 px-2 sm:px-3 pb-2 sm:pb-3">
        {enCooldown ? (
          <button
            onClick={handleReservar}
            className="w-full font-bold text-[11px] sm:text-[12px] py-2 rounded-md transition-all flex items-center justify-center gap-1.5 cursor-pointer"
            style={{
              background: withAlpha("#fb923c", hover ? 0.18 : 0.08),
              border: "1px solid rgba(251,146,60,0.55)",
              color: "#fdba74",
            }}
          >
            <span>🎟️</span>
            <span>Reservar</span>
          </button>
        ) : esIntercambiable ? (
          <button
            className="w-full font-bold text-[11px] sm:text-[12px] py-2 rounded-md transition-all flex items-center justify-center gap-1.5 cursor-pointer"
            style={
              estaSeleccionado
                ? {
                  background: "rgba(251,146,60,0.2)",
                  border: "1px solid rgba(251,146,60,0.7)",
                  color: "#fdba74",
                }
                : {
                  background: withAlpha(colorPrincipal, hover ? 0.16 : 0.06),
                  border: `1px solid ${withAlpha(colorPrincipal, 0.55)}`,
                  color: colorPrincipal,
                }
            }
          >
            <span></span>
            <span>{estaSeleccionado ? "Quitar" : "Agregar"}</span>
          </button>
        ) : null}
      </div>
    </div>
  );
};

export default TarjetaInventario;
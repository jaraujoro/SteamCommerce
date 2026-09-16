import { useState, useRef, useEffect, useMemo } from "react";

/* ============================================================
   Colores por rareza (fallback si item.color no viene)
============================================================ */
const RAREZA_COLORS = {
  common: "#b0c3d9",
  uncommon: "#5e98d9",
  rare: "#4b69ff",
  mythical: "#8847ff",
  legendary: "#d32ce6",
  immortal: "#e4ae39",
  arcana: "#ade55c",
  seasonal: "#f2a227",
  "sets collectors": "#e5732e",
};

const getRarezaColor = (rareza, items = []) => {
  if (!rareza) return "#8f9aa7";
  const found = items.find(
    (i) => i.rarity && i.rarity.toLowerCase() === rareza.toLowerCase()
  );
  if (found?.color) return `#${found.color}`;
  return RAREZA_COLORS[rareza.toLowerCase()] || "#8f9aa7";
};

/* ============================================================
   Iconos
============================================================ */
const SlidersIcon = () => (
  <svg className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <path strokeLinecap="round" d="M4 6h10M18 6h2M4 12h2M10 12h10M4 18h12M20 18h0" />
    <circle cx="16" cy="6" r="2" />
    <circle cx="8" cy="12" r="2" />
    <circle cx="18" cy="18" r="2" />
  </svg>
);

const ChevronDown = () => (
  <svg className="w-3 h-3 shrink-0" viewBox="0 0 10 6" fill="currentColor">
    <path d="M0 0l5 6 5-6z" />
  </svg>
);

const XIcon = ({ className = "w-3 h-3" }) => (
  <svg className={className} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
    <path strokeLinecap="round" d="M6 6l12 12M6 18L18 6" />
  </svg>
);

const SearchIcon = () => (
  <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-4.35-4.35M17 11a6 6 0 11-12 0 6 6 0 0112 0z" />
  </svg>
);

/* ============================================================
   Select compacto
============================================================ */
const CompactSelect = ({ label, value, onChange, options, minWidth = "150px" }) => {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    const h = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener("mousedown", h);
    return () => document.removeEventListener("mousedown", h);
  }, []);

  const currentLabel =
    options.find((o) => o.value === value)?.label || options[0].label;

  return (
    <div ref={ref} className="relative" style={{ minWidth }}>
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        className="w-full flex items-center justify-between gap-2 bg-[#16202d] hover:bg-[#1e2c3c] border border-[#2a475e]/60 hover:border-[#3a6a8a] rounded px-3 py-1.5 text-[13px] text-white transition-colors"
      >
        <span className="truncate">{currentLabel}</span>
        <ChevronDown />
      </button>

      {open && (
        <ul className="absolute z-50 mt-1 w-full bg-[#0d1b2a] border border-[#2a475e] rounded-md shadow-2xl shadow-black/60 py-1 max-h-64 overflow-y-auto">
          {options.map((opt) => (
            <li key={opt.value}>
              <button
                type="button"
                onClick={() => {
                  onChange(opt.value);
                  setOpen(false);
                }}
                className={`w-full text-left px-3 py-1.5 text-[13px] transition-colors ${value === opt.value
                  ? "bg-[#66c0f4]/15 text-[#66c0f4]"
                  : "text-[#c6d4df] hover:bg-[#2a475e]/60 hover:text-white"
                  }`}
              >
                {opt.label}
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

/* ============================================================
   Multiselect con contador + buscador interno
============================================================ */
const MultiSelect = ({ label, values = [], onChange, options }) => {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState("");
  const ref = useRef(null);

  useEffect(() => {
    const h = (e) => {
      if (ref.current && !ref.current.contains(e.target)) {
        setOpen(false);
        setQuery("");
      }
    };
    document.addEventListener("mousedown", h);
    return () => document.removeEventListener("mousedown", h);
  }, []);

  // Filtrar opciones por búsqueda
  const filtered = useMemo(() => {
    if (!query.trim()) return options;
    const q = query.toLowerCase().trim();
    return options.filter((o) => o.toLowerCase().includes(q));
  }, [options, query]);

  const toggle = (val) => {
    const set = new Set(values);
    set.has(val) ? set.delete(val) : set.add(val);
    onChange([...set]);
  };

  return (
    <div ref={ref} className="relative" style={{ minWidth: 150 }}>
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        className="w-full flex items-center justify-between gap-2 bg-[#16202d] hover:bg-[#1e2c3c] border border-[#2a475e]/60 hover:border-[#3a6a8a] rounded px-3 py-1.5 text-[13px] text-white transition-colors"
      >
        <span className="truncate">{label}</span>
        <span className="flex items-center gap-1.5 shrink-0">
          {values.length > 0 && (
            <span className="bg-[#66c0f4] text-[#0d1b2a] text-[10px] font-bold rounded-full w-4 h-4 flex items-center justify-center">
              {values.length}
            </span>
          )}
          <ChevronDown />
        </span>
      </button>

      {open && (
        <div className="absolute z-50 mt-1 w-full min-w-[240px] bg-[#0d1b2a] border border-[#2a475e] rounded-md shadow-2xl shadow-black/60 overflow-hidden">
          {/* 🔍 Buscador interno */}
          <div className="p-2 border-b border-[#2a475e]/60">
            <div className="relative">
              <span className="absolute left-2 top-1/2 -translate-y-1/2 text-[#6b8a9e] pointer-events-none">
                <SearchIcon />
              </span>
              <input
                autoFocus
                type="text"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Buscar..."
                className="w-full bg-[#1b2838] border border-[#2a475e] rounded pl-7 pr-7 py-1.5 text-xs text-white placeholder-[#6b8a9e] focus:border-[#66c0f4] focus:outline-none"
              />
              {query && (
                <button
                  type="button"
                  onClick={() => setQuery("")}
                  className="absolute right-1.5 top-1/2 -translate-y-1/2 text-[#6b8a9e] hover:text-white"
                >
                  <XIcon className="w-3 h-3" />
                </button>
              )}
            </div>
          </div>

          {/* Lista filtrada */}
          <ul className="max-h-64 overflow-y-auto py-1">
            {filtered.length === 0 ? (
              <li className="px-3 py-2 text-xs text-[#6b8a9e] italic">
                Sin resultados
              </li>
            ) : (
              filtered.map((opt) => {
                const selected = values.includes(opt);
                return (
                  <li key={opt}>
                    <button
                      type="button"
                      onClick={() => toggle(opt)}
                      className={`w-full flex items-center gap-2 text-left px-3 py-1.5 text-[13px] transition-colors ${selected
                        ? "bg-[#66c0f4]/10 text-[#66c0f4]"
                        : "text-[#c6d4df] hover:bg-[#2a475e]/60 hover:text-white"
                        }`}
                    >
                      <span
                        className={`w-3.5 h-3.5 rounded-sm border flex items-center justify-center shrink-0 ${selected
                          ? "bg-[#66c0f4] border-[#66c0f4]"
                          : "border-[#3a6a8a]"
                          }`}
                      >
                        {selected && (
                          <svg
                            viewBox="0 0 12 12"
                            className="w-2.5 h-2.5 text-[#0d1b2a]"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2.5"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              d="M2 6l3 3 5-6"
                            />
                          </svg>
                        )}
                      </span>
                      <span className="truncate">{opt}</span>
                    </button>
                  </li>
                );
              })
            )}
          </ul>

          {/* Footer con contador */}
          {values.length > 0 && (
            <div className="px-3 py-1.5 border-t border-[#2a475e]/60 flex items-center justify-between text-[11px] text-[#6b8a9e]">
              <span>
                {values.length} seleccionado{values.length !== 1 ? "s" : ""}
              </span>
              <button
                type="button"
                onClick={() => onChange([])}
                className="hover:text-white transition-colors"
              >
                Limpiar
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

/* ============================================================
   Chip de rareza
============================================================ */
const RarezaChip = ({ rareza, color, selected, onClick }) => (
  <button
    type="button"
    onClick={onClick}
    className="px-3 py-1 rounded text-[12px] font-bold tracking-wide transition-all whitespace-nowrap"
    style={{
      border: `1px solid ${color}`,
      background: selected ? `${color}30` : "transparent",
      color: selected ? color : "#c6d4df",
      boxShadow: selected ? `0 0 12px ${color}55, inset 0 0 10px ${color}22` : "none",
      textShadow: selected ? `0 0 8px ${color}88` : "none",
    }}
  >
    {rareza}
  </button>
);

/* ============================================================
   Badge activo
============================================================ */
const ActiveBadge = ({ color, label, onRemove }) => (
  <span
    className="inline-flex items-center gap-1.5 pl-2.5 pr-1.5 py-0.5 rounded-full text-[11px] border max-w-full"
    style={{
      borderColor: `${color}66`,
      background: `${color}15`,
      color: "#dbe6ef",
    }}
  >
    <span className="truncate max-w-[140px]">{label}</span>
    <button
      type="button"
      onClick={onRemove}
      className="w-4 h-4 flex items-center justify-center rounded-full hover:bg-white/10 transition-colors shrink-0"
      style={{ color: "#8f9aa7" }}
    >
      <XIcon className="w-2.5 h-2.5" />
    </button>
  </span>
);

/* ============================================================
   Componente principal
============================================================ */
const FiltrosInventario = ({
  filtros,
  setFiltros,
  rarezas = [],
  heroes = [],
  tiposItem = [],
  totalResultados,
  items = [],
}) => {
  const [precioMinLocal, setPrecioMinLocal] = useState(filtros.precioMin || "");
  const [precioMaxLocal, setPrecioMaxLocal] = useState(filtros.precioMax || "");

  const setFiltro = (key, value) => setFiltros({ ...filtros, [key]: value });

  const handleLimpiar = () => {
    setFiltros({
      busqueda: "",
      heroes: [],
      tiposItem: [],
      rarezas: [],
      precioMin: "",
      precioMax: "",
      orden: "relevancia",
      disponibilidad: "todos",
      estadoTrade: "todos",
      stock: "todos",
    });
    setPrecioMinLocal("");
    setPrecioMaxLocal("");
  };

  const toggleRareza = (r) => {
    const set = new Set(filtros.rarezas);
    set.has(r) ? set.delete(r) : set.add(r);
    setFiltro("rarezas", [...set]);
  };

  const aplicarPrecio = () => {
    setFiltros({
      ...filtros,
      precioMin: precioMinLocal,
      precioMax: precioMaxLocal,
    });
  };

  const hayFiltrosActivos =
    filtros.busqueda ||
    filtros.heroes.length > 0 ||
    filtros.tiposItem.length > 0 ||
    filtros.rarezas.length > 0 ||
    filtros.precioMin ||
    filtros.precioMax ||
    filtros.disponibilidad !== "todos" ||
    filtros.estadoTrade !== "todos" ||
    filtros.stock !== "todos";

  const ordenRarezas = [
    "Arcana", "Immortal", "Legendary", "Mythical",
    "Rare", "Uncommon", "Seasonal", "Sets Collectors", "Common",
  ];

  const rarezasOrdenadas = useMemo(
    () =>
      [...rarezas].sort(
        (a, b) => ordenRarezas.indexOf(a) - ordenRarezas.indexOf(b)
      ),
    [rarezas]
  );

  return (
    <div className="bg-[#101822] border border-[#2a475e]/70 rounded-lg p-3 sm:p-4 mb-4 sm:mb-6 shadow-2xl shadow-black/40">
      {/* Buscador arriba */}
      <div className="mb-3">
        <div className="relative">
          <span className="absolute left-3 top-1/2 -translate-y-1/2 text-[#6b8a9e] pointer-events-none">
            <SearchIcon />
          </span>
          <input
            type="text"
            placeholder="Buscar ítem..."
            value={filtros.busqueda}
            onChange={(e) => setFiltro("busqueda", e.target.value)}
            className="w-full bg-[#0d1b2a] border border-[#2a475e] rounded-md pl-9 pr-9 py-2 text-white text-sm placeholder-[#6b8a9e] focus:border-[#66c0f4] focus:outline-none"
          />
          {filtros.busqueda && (
            <button
              type="button"
              onClick={() => setFiltro("busqueda", "")}
              className="absolute right-2 top-1/2 -translate-y-1/2 text-[#6b8a9e] hover:text-white"
            >
              <XIcon className="w-3.5 h-3.5" />
            </button>
          )}
        </div>
      </div>

      {/* Fila 1: icono + multiselects + chips de rareza */}
      <div className="flex flex-wrap items-center gap-2">
        <div className="text-[#66c0f4] p-1.5 shrink-0">
          <SlidersIcon />
        </div>

        <MultiSelect
          label="Héroe"
          values={filtros.heroes}
          onChange={(v) => setFiltro("heroes", v)}
          options={heroes}
        />

        <MultiSelect
          label="Tipo de item"
          values={filtros.tiposItem}
          onChange={(v) => setFiltro("tiposItem", v)}
          options={tiposItem}
        />

        <div className="flex flex-wrap gap-1.5 items-center">
          {rarezasOrdenadas.map((r) => {
            const color = getRarezaColor(r, items);
            return (
              <RarezaChip
                key={r}
                rareza={r}
                color={color}
                selected={filtros.rarezas.includes(r)}
                onClick={() => toggleRareza(r)}
              />
            );
          })}
        </div>

        <CompactSelect
          label="Calidad"
          value={filtros.orden}
          onChange={(v) => setFiltro("orden", v)}
          options={[
            { value: "relevancia", label: "Calidad" },
            { value: "precio-asc", label: "Precio ↑" },
            { value: "precio-desc", label: "Precio ↓" },
            { value: "nombre-asc", label: "Nombre A-Z" },
            { value: "nombre-desc", label: "Nombre Z-A" },
          ]}
        />
      </div>

      {/* Fila 2: precio + orden + disponibilidad + trade + stock */}
      <div className="flex flex-wrap items-center gap-2 mt-3">
        <div className="flex items-center gap-1.5 bg-[#16202d] border border-[#2a475e]/60 rounded px-2 py-1">
          <span className="text-[12px] text-[#8f9aa7] whitespace-nowrap pl-1">
            Precio (S/):
          </span>
          <input
            type="number"
            placeholder="Mín"
            value={precioMinLocal}
            onChange={(e) => setPrecioMinLocal(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && aplicarPrecio()}
            className="w-20 bg-transparent border-b border-[#2a475e] text-[13px] text-white placeholder-[#6b8a9e] focus:border-[#66c0f4] focus:outline-none px-1"
          />
          <span className="text-[#6b8a9e]">–</span>
          <input
            type="number"
            placeholder="Máx"
            value={precioMaxLocal}
            onChange={(e) => setPrecioMaxLocal(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && aplicarPrecio()}
            className="w-20 bg-transparent border-b border-[#2a475e] text-[13px] text-white placeholder-[#6b8a9e] focus:border-[#66c0f4] focus:outline-none px-1"
          />
          <button
            type="button"
            onClick={aplicarPrecio}
            className="ml-1 px-2.5 py-1 bg-[#e4ae39] hover:bg-[#f2c14f] text-[#101822] font-bold text-[12px] rounded transition-colors"
          >
            Aplicar
          </button>
        </div>

        <CompactSelect
          label="Ordenar por..."
          value={filtros.orden}
          onChange={(v) => setFiltro("orden", v)}
          options={[
            { value: "relevancia", label: "Ordenar por..." },
            { value: "precio-asc", label: "Precio: menor a mayor" },
            { value: "precio-desc", label: "Precio: mayor a menor" },
            { value: "nombre-asc", label: "Nombre: A-Z" },
            { value: "nombre-desc", label: "Nombre: Z-A" },
          ]}
          minWidth="180px"
        />

        <CompactSelect
          label="Disponibilidad"
          value={filtros.disponibilidad}
          onChange={(v) => setFiltro("disponibilidad", v)}
          options={[
            { value: "todos", label: "Disponibilidad" },
            { value: "stock", label: "En stock" },
            { value: "agotado", label: "Agotado" },
          ]}
        />

        <CompactSelect
          label="Estado de trade"
          value={filtros.estadoTrade}
          onChange={(v) => setFiltro("estadoTrade", v)}
          options={[
            { value: "todos", label: "Estado de trade" },
            { value: "intercambiable", label: "Intercambiable" },
            { value: "cooldown", label: "En cooldown" },
            { value: "bloqueado", label: "Bloqueado" },
          ]}
        />

        <CompactSelect
          label="Stock"
          value={filtros.stock}
          onChange={(v) => setFiltro("stock", v)}
          options={[
            { value: "todos", label: "Stock" },
            { value: "bajo", label: "Bajo (≤2)" },
            { value: "alto", label: "Alto (>2)" },
          ]}
        />
      </div>

      {/* Fila 3: badges activos */}
      {hayFiltrosActivos && (
        <div className="flex flex-wrap items-center gap-2 mt-3 pt-3 border-t border-[#2a475e]/50">
          {filtros.heroes.map((h) => (
            <ActiveBadge
              key={`h-${h}`}
              color="#e4ae39"
              label={`Héroe: ${h}`}
              onRemove={() =>
                setFiltro("heroes", filtros.heroes.filter((x) => x !== h))
              }
            />
          ))}

          {filtros.tiposItem.map((t) => (
            <ActiveBadge
              key={`t-${t}`}
              color="#66c0f4"
              label={`Tipo: ${t}`}
              onRemove={() =>
                setFiltro(
                  "tiposItem",
                  filtros.tiposItem.filter((x) => x !== t)
                )
              }
            />
          ))}

          {filtros.rarezas.map((r) => (
            <ActiveBadge
              key={`r-${r}`}
              color={getRarezaColor(r, items)}
              label={r}
              onRemove={() => toggleRareza(r)}
            />
          ))}

          {filtros.busqueda && (
            <ActiveBadge
              color="#8f9aa7"
              label={`"${filtros.busqueda}"`}
              onRemove={() => setFiltro("busqueda", "")}
            />
          )}

          {(filtros.precioMin || filtros.precioMax) && (
            <ActiveBadge
              color="#ade55c"
              label={`S/. ${filtros.precioMin || "0"} – ${filtros.precioMax || "∞"}`}
              onRemove={() => {
                setFiltro("precioMin", "");
                setFiltro("precioMax", "");
                setPrecioMinLocal("");
                setPrecioMaxLocal("");
              }}
            />
          )}

          <button
            type="button"
            onClick={handleLimpiar}
            className="ml-2 text-[12px] text-[#8f9aa7] hover:text-white inline-flex items-center gap-1 transition-colors"
          >
            <XIcon className="w-3 h-3" />
            Limpiar filtros
          </button>

          {typeof totalResultados === "number" && (
            <span className="ml-auto text-[12px] text-[#6b8a9e] whitespace-nowrap">
              {totalResultados}{" "}
              {totalResultados === 1 ? "resultado" : "resultados"}
            </span>
          )}
        </div>
      )}
    </div>
  );
};

export default FiltrosInventario;
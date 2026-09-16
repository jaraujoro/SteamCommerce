import { useState, useEffect, useMemo } from "react";
import { useAuth } from "../../auth/context/AuthContext";
import { useCarrito } from "../../carrito/context/CarritoContext";
import { obtenerInventario } from "../services/catalogoService";
import FiltrosInventario from "../components/FiltrosInventario";
import GrillaInventario from "../components/GrillaInventario";
import CatalogoSkeleton from "../components/CatalogoSkeleton";

const CatalogoPage = () => {
  const { usuario } = useAuth();
  const { carrito, toggleCarrito, vaciarCarrito, estaEnCarrito } = useCarrito();

  const [items, setItems] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);
  const [filtros, setFiltros] = useState({
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

  useEffect(() => {
    const cargarInventario = async () => {
      try {
        const response = await obtenerInventario();
        setItems(response.data || []);
      } catch (error) {
        setError(error.message || "Error al cargar el inventario");
      } finally {
        setCargando(false);
      }
    };
    cargarInventario();
  }, []);

  // Filtrado + orden
  const itemsFiltrados = useMemo(() => {
    let resultado = [...items];

    // Búsqueda
    if (filtros.busqueda.trim()) {
      const q = filtros.busqueda.toLowerCase().trim();
      resultado = resultado.filter((i) =>
        (i.marketHashName || "").toLowerCase().includes(q)
      );
    }

    // Rarezas (multiselect)
    if (filtros.rarezas.length > 0) {
      const set = new Set(filtros.rarezas.map((r) => r.toLowerCase()));
      resultado = resultado.filter(
        (i) => i.rarity && set.has(i.rarity.toLowerCase())
      );
    }

    // Héroes (multiselect)
    if (filtros.heroes.length > 0) {
      const set = new Set(filtros.heroes);
      resultado = resultado.filter((i) => set.has(i.hero));
    }

    // Tipos (multiselect)
    if (filtros.tiposItem.length > 0) {
      const set = new Set(filtros.tiposItem.map((t) => t.toLowerCase()));
      resultado = resultado.filter(
        (i) => i.tipoItem && set.has(i.tipoItem.toLowerCase())
      );
    }

    // Precio
    const min = parseFloat(filtros.precioMin);
    const max = parseFloat(filtros.precioMax);
    if (!Number.isNaN(min)) {
      resultado = resultado.filter((i) => Number(i.precioVenta || 0) >= min);
    }
    if (!Number.isNaN(max)) {
      resultado = resultado.filter((i) => Number(i.precioVenta || 0) <= max);
    }

    // Estado de trade
    if (filtros.estadoTrade !== "todos") {
      resultado = resultado.filter((i) => {
        const restante = i.tradeCooldownUntil
          ? new Date(i.tradeCooldownUntil) - new Date()
          : 0;
        const enCooldown = !i.tradable && restante > 0;
        const bloqueado = !i.tradable && restante <= 0;
        if (filtros.estadoTrade === "intercambiable") return i.tradable;
        if (filtros.estadoTrade === "cooldown") return enCooldown;
        if (filtros.estadoTrade === "bloqueado") return bloqueado;
        return true;
      });
    }

    // Disponibilidad
    if (filtros.disponibilidad === "agotado") {
      resultado = resultado.filter((i) => (i.stock ?? 1) <= 0);
    } else if (filtros.disponibilidad === "stock") {
      resultado = resultado.filter((i) => (i.stock ?? 1) > 0);
    }

    // Stock bajo/alto
    if (filtros.stock === "bajo") {
      resultado = resultado.filter((i) => (i.stock ?? 1) <= 2);
    } else if (filtros.stock === "alto") {
      resultado = resultado.filter((i) => (i.stock ?? 1) > 2);
    }

    // Orden
    if (filtros.orden !== "relevancia") {
      resultado.sort((a, b) => {
        switch (filtros.orden) {
          case "precio-asc":
            return Number(a.precioVenta || 0) - Number(b.precioVenta || 0);
          case "precio-desc":
            return Number(b.precioVenta || 0) - Number(a.precioVenta || 0);
          case "nombre-asc":
            return (a.marketHashName || "").localeCompare(b.marketHashName || "");
          case "nombre-desc":
            return (b.marketHashName || "").localeCompare(a.marketHashName || "");
          default:
            return 0;
        }
      });
    }

    return resultado;
  }, [items, filtros]);

  // Opciones únicas
  const rarezasUnicas = useMemo(
    () =>
      [...new Set(items.map((i) => i.rarity).filter(Boolean))].sort((a, b) =>
        a.localeCompare(b)
      ),
    [items]
  );

  const heroesUnicos = useMemo(
    () =>
      [
        ...new Set(
          items.map((i) => i.hero).filter((h) => h && h !== "Sin héroe")
        ),
      ].sort((a, b) => a.localeCompare(b)),
    [items]
  );

  const tiposItemUnicos = useMemo(() => {
    const mapa = new Map();
    items.forEach((i) => {
      if (i.tipoItem) {
        const key = i.tipoItem.toLowerCase();
        if (!mapa.has(key)) mapa.set(key, i.tipoItem);
      }
    });
    return [...mapa.values()].sort((a, b) => a.localeCompare(b));
  }, [items]);

  if (cargando) return <CatalogoSkeleton />;

  if (error) {
    return (
      <div className="flex justify-center items-center min-h-[50vh] px-4">
        <div className="text-center">
          <p className="text-red-500 text-base sm:text-lg">{error}</p>
          <button
            onClick={() => window.location.reload()}
            className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  if (items.length === 0) {
    return (
      <div className="flex justify-center items-center min-h-[50vh] px-4">
        <div className="text-center">
          <p className="text-[#8f9aa7] text-base sm:text-lg">
            El catálogo está vacío
          </p>
          <p className="text-[#8f9aa7] text-sm mt-2">
            No hay items disponibles para vender
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="w-full max-w-[1600px] mx-auto px-3 sm:px-4 lg:px-6 py-4">
      <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-2 mb-4">
        <h2 className="text-lg sm:text-xl font-bold text-white">
          Catálogo de items
        </h2>
      </div>

      <FiltrosInventario
        filtros={filtros}
        setFiltros={setFiltros}
        rarezas={rarezasUnicas}
        heroes={heroesUnicos}
        tiposItem={tiposItemUnicos}
        totalResultados={itemsFiltrados.length}
        items={items}
      />

      {itemsFiltrados.length === 0 ? (
        <div className="flex justify-center items-center min-h-[40vh] px-4">
          <p className="text-[#8f9aa7] text-center">
            No se encontraron items con esos filtros
          </p>
        </div>
      ) : (
        <GrillaInventario
          items={itemsFiltrados}
          carrito={carrito}
          onToggleCarrito={toggleCarrito}
          onReservar={usuario ? undefined : undefined}
        />
      )}
    </div>
  );
};

export default CatalogoPage;
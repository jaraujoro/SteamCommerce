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
    rareza: "todos",
    busqueda: "",
    heroe: "todos",
  });

  // Cargar inventario
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

  // Filtrar items
  const itemsFiltrados = useMemo(() => {
    let resultado = [...items];

    if (filtros.rareza !== "todos") {
      const rarezaLower = filtros.rareza.toLowerCase();
      resultado = resultado.filter(
        (item) =>
          item.rarity &&
          item.rarity.toLowerCase().includes(rarezaLower),
      );
    }

    if (filtros.busqueda.trim()) {
      const busqueda = filtros.busqueda.toLowerCase().trim();
      resultado = resultado.filter(
        (item) =>
          item.marketHashName &&
          item.marketHashName.toLowerCase().includes(busqueda),
      );
    }

    if (filtros.heroe !== "todos") {
      resultado = resultado.filter((item) => item.hero === filtros.heroe);
    }

    return resultado;
  }, [items, filtros]);

  // Valores únicos
  const rarezasUnicas = useMemo(
    () => [...new Set(items.map((item) => item.rarity).filter(Boolean))],
    [items],
  );

  const heroesUnicos = useMemo(
    () =>
      [
        ...new Set(
          items.map((item) => item.hero).filter((h) => h && h !== "Sin héroe"),
        ),
      ].sort((a, b) => a.localeCompare(b)),
    [items],
  );

  // Estados de carga
  if (cargando) {
    return <CatalogoSkeleton />;
  }

  if (error) {
    return (
      <div className="flex justify-center items-center min-h-[50vh]">
        <div className="text-center">
          <p className="text-red-500 text-lg">{error}</p>
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
      <div className="flex justify-center items-center min-h-[50vh]">
        <div className="text-center">
          <p className="text-[#8f9aa7] text-lg">El catálogo está vacío</p>
          <p className="text-[#8f9aa7] text-sm mt-2">
            No hay items disponibles para vender
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold text-white">Catálogo de items</h2>
      </div>

      <FiltrosInventario
        filtros={filtros}
        setFiltros={setFiltros}
        rarezas={rarezasUnicas}
        heroes={heroesUnicos}
      />

      {itemsFiltrados.length === 0 ? (
        <div className="flex justify-center items-center min-h-[40vh]">
          <p className="text-[#8f9aa7]">
            No se encontraron items con esos filtros
          </p>
        </div>
      ) : (
        <GrillaInventario
          items={itemsFiltrados}
          carrito={carrito}
          onToggleCarrito={toggleCarrito}
        />
      )}
    </div>
  );
};

export default CatalogoPage;

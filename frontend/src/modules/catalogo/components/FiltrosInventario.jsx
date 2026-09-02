import { useState } from 'react';

const FiltrosInventario = ({
    filtros,
    setFiltros,
    rarezas,
    heroes,
  }) => {

  const [isExpanded, setIsExpanded] = useState(false);

  const handleLimpiar = () => {setFiltros({
      rareza: 'todos',
      intercambiable: 'todos',
      busqueda: '',
      heroe: 'todos',
    });
  };

  return (
    <div className="bg-[#1b2838] rounded-lg p-4 mb-6 border border-[#2a475e] shadow-xl transition-all duration-300">
      <div className="flex flex-col sm:flex-row sm:items-center gap-3">
        {/* Búsqueda - Siempre visible */}
        <div className="flex-1 min-w-[120px]">
          <div className="relative">
            <input
              type="text"
              placeholder="Buscar ítem..."
              value={filtros.busqueda}
              onChange={(e) =>
                setFiltros({ ...filtros, busqueda: e.target.value })
              }
              className="w-full bg-[#0d1b2a] border border-[#2a475e] rounded-md px-4 py-2 text-white text-sm placeholder-[#6b8a9e] focus:border-[#66c0f4] focus:outline-none focus:ring-2 focus:ring-[#66c0f4]/20 transition-all duration-200"
            />
          </div>
        </div>

        {/* Botones para mobile */}
        <div className="flex gap-2 sm:hidden">
          <button
            onClick={() => setIsExpanded(!isExpanded)}
            className="flex-1 bg-[#2a475e] hover:bg-[#3a6a8a] text-white text-sm px-4 py-2 rounded-md transition-colors duration-200"
          >
            {isExpanded ? '▲ Filtros' : '▼ Filtros'}
          </button>
          <button
            onClick={handleLimpiar}
            className="flex-1 bg-[#1b2838] hover:bg-[#2a475e] text-[#66c0f4] text-sm px-4 py-2 rounded-md border border-[#2a475e] transition-colors duration-200"
          >
            ✕ Limpiar
          </button>
        </div>

        {/* Filtros - Desktop siempre visible, Mobile expandible */}
        <div className={`flex flex-wrap gap-2 items-center ${!isExpanded ? 'hidden sm:flex' : 'flex'} sm:flex`}>
          <select
            value={filtros.rareza}
            onChange={(e) => setFiltros({ ...filtros, rareza: e.target.value })}
            className="bg-[#0d1b2a] border border-[#2a475e] rounded-md px-3 py-2 text-white text-sm focus:border-[#66c0f4] focus:outline-none focus:ring-2 focus:ring-[#66c0f4]/20 transition-all duration-200 appearance-none cursor-pointer hover:border-[#3a6a8a] min-w-[130px]"
            style={{
              backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='6'%3E%3Cpath d='M0 0l5 6 5-6z' fill='%2366c0f4'/%3E%3C/svg%3E")`,
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'right 10px center',
              paddingRight: '30px',
            }}
          >
            <option value="todos">Todas las rarezas</option>
            {rarezas.map((rareza) => (
              <option key={rareza} value={rareza}>
                {rareza}
              </option>
            ))}
          </select>

          <select
            value={filtros.heroe}
            onChange={(e) => setFiltros({ ...filtros, heroe: e.target.value })}
            className="bg-[#0d1b2a] border border-[#2a475e] rounded-md px-3 py-2 text-white text-sm focus:border-[#66c0f4] focus:outline-none focus:ring-2 focus:ring-[#66c0f4]/20 transition-all duration-200 appearance-none cursor-pointer hover:border-[#3a6a8a] min-w-[130px]"
            style={{
              backgroundImage: `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='6'%3E%3Cpath d='M0 0l5 6 5-6z' fill='%2366c0f4'/%3E%3C/svg%3E")`,
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'right 10px center',
              paddingRight: '30px',
            }}
          >
            <option value="todos">Todos los héroes</option>
            {heroes.map((heroe) => (
              <option key={heroe} value={heroe}>
                {heroe}
              </option>
            ))}
          </select>

          {/* Botón limpiar - Desktop */}
          <button
            onClick={handleLimpiar}
            className="hidden sm:block bg-[#0d1b2a] hover:bg-[#2a475e] text-[#66c0f4] text-sm px-4 py-2 rounded-md border border-[#2a475e] hover:border-[#66c0f4] transition-all duration-200 font-medium whitespace-nowrap"
          >
            ✕ Limpiar filtros
          </button>
        </div>
      </div>

      {/* Badges de filtros activos */}
      <div className="mt-3 flex flex-wrap gap-2">
        {filtros.busqueda && (
          <span className="inline-flex items-center bg-[#0d1b2a] px-2 py-1 rounded-full text-xs text-[#66c0f4] border border-[#2a475e]">
            {filtros.busqueda}
            <button
              onClick={() => setFiltros({ ...filtros, busqueda: '' })}
              className="ml-1 text-[#6b8a9e] hover:text-white transition-colors"
            >
              ×
            </button>
          </span>
        )}
        {filtros.rareza !== 'todos' && (
          <span className="inline-flex items-center bg-[#0d1b2a] px-2 py-1 rounded-full text-xs text-[#66c0f4] border border-[#2a475e]">
            {filtros.rareza}
            <button
              onClick={() => setFiltros({ ...filtros, rareza: 'todos' })}
              className="ml-1 text-[#6b8a9e] hover:text-white transition-colors"
            >
              ×
            </button>
          </span>
        )}
        {filtros.heroe !== 'todos' && (
          <span className="inline-flex items-center bg-[#0d1b2a] px-2 py-1 rounded-full text-xs text-[#66c0f4] border border-[#2a475e]">
            {filtros.heroe}
            <button
              onClick={() => setFiltros({ ...filtros, heroe: 'todos' })}
              className="ml-1 text-[#6b8a9e] hover:text-white transition-colors"
            >
              ×
            </button>
          </span>
        )}
      </div>
    </div>
  );
};

export default FiltrosInventario;
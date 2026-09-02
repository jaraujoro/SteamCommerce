const CatalogoSkeleton = () => {
  const skeletonItems = Array.from({ length: 10 });

  return (
    <div className="p-4">
      {/* Título */}
      <div className="flex justify-between items-center mb-4 animate-pulse">
        <div className="h-7 w-48 bg-gray-700/60 rounded" />
      </div>

      {/* Filtros skeleton parecido a FiltrosInventario */}
      <div className="bg-[#1b2838] rounded-lg p-4 mb-6 border border-[#2a475e] shadow-xl animate-pulse">
        <div className="flex flex-col sm:flex-row sm:items-center gap-3">
          {/* Búsqueda */}
          <div className="flex-1 min-w-[120px]">
            <div className="h-10 w-full bg-[#0d1b2a] border border-[#2a475e] rounded-md" />
          </div>

          {/* Botones mobile */}
          <div className="flex gap-2 sm:hidden">
            <div className="h-10 flex-1 bg-[#2a475e] rounded-md" />
            <div className="h-10 flex-1 bg-[#0d1b2a] border border-[#2a475e] rounded-md" />
          </div>

          {/* Selects desktop */}
          <div className="hidden sm:flex flex-wrap gap-2 items-center">
            <div className="h-10 w-[130px] bg-[#0d1b2a] border border-[#2a475e] rounded-md" />
            <div className="h-10 w-[130px] bg-[#0d1b2a] border border-[#2a475e] rounded-md" />
            <div className="h-10 w-32 bg-[#0d1b2a] border border-[#2a475e] rounded-md" />
          </div>
        </div>

        {/* Badges falsos de filtros */}
        <div className="mt-3 flex flex-wrap gap-2">
          <div className="h-6 w-20 bg-[#0d1b2a] border border-[#2a475e] rounded-full" />
          <div className="h-6 w-24 bg-[#0d1b2a] border border-[#2a475e] rounded-full" />
        </div>
      </div>

      {/* Grilla skeleton parecida a GrillaInventario */}
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
        {skeletonItems.map((_, index) => (
          <div
            key={index}
            className="relative overflow-hidden p-3 sm:p-4 md:p-5 rounded-2xl bg-gray-800/20 border border-gray-700/30 animate-pulse"
          >
            {/* Glow falso */}
            <div className="absolute -top-20 -right-20 w-40 h-40 rounded-full blur-3xl bg-gray-700/20 pointer-events-none" />

            {/* Badge superior */}
            <div className="absolute top-2 sm:top-3 right-2 sm:right-3 z-10">
              <div className="h-6 w-20 bg-gray-700/60 rounded-full" />
            </div>

            {/* Imagen */}
            <div className="relative mb-3 sm:mb-4">
              <div className="relative rounded-xl overflow-hidden bg-black/40 p-2 sm:p-3">
                <div className="w-full h-24 sm:h-28 md:h-32 bg-gray-700/50 rounded-lg" />

                {/* Borde falso de rareza */}
                <div className="absolute inset-0 rounded-xl border-2 border-gray-700/40" />
              </div>
            </div>

            {/* Información */}
            <div className="space-y-1.5 sm:space-y-2">
              <div className="h-4 sm:h-5 w-full bg-gray-700/60 rounded" />

              <div className="flex flex-wrap items-center gap-1.5 sm:gap-2">
                <div className="h-5 w-20 bg-gray-700/50 rounded-full" />
                <div className="h-3 w-16 bg-gray-700/40 rounded" />
              </div>

              <div className="flex items-center gap-1.5 sm:gap-2">
                <div className="h-3 w-10 bg-gray-700/40 rounded" />
                <div className="h-4 w-20 bg-gray-700/60 rounded" />
              </div>
            </div>

            {/* Precios */}
            <div className="mt-3 sm:mt-4 pt-3 sm:pt-4 border-t border-gray-700/20">
              <div className="flex flex-col xs:flex-row xs:items-end justify-between gap-1.5 xs:gap-2">
                <div className="flex items-baseline gap-1">
                  <div className="h-5 w-20 bg-gray-700/70 rounded" />
                  <div className="h-3 w-8 bg-gray-700/40 rounded" />
                </div>

                <div className="flex items-center gap-1 bg-black/30 px-2 py-1 rounded-full">
                  <div className="w-3 h-3 sm:w-4 sm:h-4 bg-gray-700/50 rounded-full" />
                  <div className="h-3 w-14 bg-gray-700/40 rounded" />
                </div>
              </div>
            </div>

            {/* Estado */}
            <div className="mt-2 sm:mt-3">
              <div className="flex items-center gap-1.5 sm:gap-2">
                <div className="w-1.5 h-1.5 sm:w-2 sm:h-2 rounded-full bg-gray-700/60" />
                <div className="h-3 w-20 bg-gray-700/40 rounded" />
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CatalogoSkeleton;
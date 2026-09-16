import TarjetaInventario from './TarjetaInventario';

const GrillaInventario = ({ items, carrito, onToggleCarrito, onReservar }) => {
  const estaEnCarrito = (publicId) => carrito.some(item => item.publicId === publicId);

  if (items.length === 0) {
    return (
      <div className="bg-[#2a475e] bg-opacity-30 rounded-lg p-8 text-center border border-[#3a6a8a]">
        <p className="text-[#8f9aa7]">No hay ítems para mostrar</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 xs:grid-cols-2 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-3 sm:gap-4">
      {items.map((item, index) => (
        <TarjetaInventario
          key={item.publicId || index}
          item={item}
          enCarrito={estaEnCarrito}
          onToggleCarrito={onToggleCarrito}
          onReservar={onReservar}
        />
      ))}
    </div>
  );
};

export default GrillaInventario;
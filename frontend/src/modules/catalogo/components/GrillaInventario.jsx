import TarjetaInventario from './TarjetaInventario';

const GrillaInventario = ({ items, carrito, onToggleCarrito }) => {

  const estaEnCarrito = (publicId) => {
    return carrito.some(item => item.publicId === publicId);
  };

  if (items.length === 0) {
    return (
      <div className="bg-[#2a475e] bg-opacity-30 rounded-lg p-8 text-center border border-[#3a6a8a]">
        <p className="text-[#8f9aa7]">No hay ítems para mostrar</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
      {items.map((item, index) => (
        <TarjetaInventario
          key={index}
          item={item}
          enCarrito={estaEnCarrito}
          onToggleCarrito={onToggleCarrito}
        />
      ))}
    </div>
  );
};

export default GrillaInventario;
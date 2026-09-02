import { createContext, useContext, useState } from 'react';

const CarritoContext = createContext(null);

export const useCarrito = () => {
  const context = useContext(CarritoContext);

  if (!context) {
    throw new Error(' debe usarse dentro de provider');
  }

  return context;
};

export const CarritoProvider = ({ children }) => {
  const [carrito, setCarrito] = useState([]);

  const toggleCarrito = (item) => {
    setCarrito((prev) => {
      const existe = prev.some((i) => i.publicId === item.publicId);

      if (existe) {
        return prev.filter((i) => i.publicId !== item.publicId);
      }

      if (item.tradable) {
        return [...prev, item];
      }

      return prev;
    });
  };

  const vaciarCarrito = () => {
    setCarrito([]);
  };

  const estaEnCarrito = (publicId) => {
    return carrito.some((item) => item.publicId === publicId);
  };

  return (
    <CarritoContext.Provider
      value={{
        carrito,
        toggleCarrito,
        vaciarCarrito,
        estaEnCarrito,
        totalItems: carrito.length,
      }}
    >
      {children}
    </CarritoContext.Provider>
  );
};

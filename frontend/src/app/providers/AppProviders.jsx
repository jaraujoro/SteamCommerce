import { AuthProvider } from '../../modules/auth/context/AuthContext';
import { CarritoProvider } from '../../modules/carrito/context/CarritoContext';

const AppProviders = ({ children }) => {
  return (
    <AuthProvider>
      <CarritoProvider>{children}</CarritoProvider>
    </AuthProvider>
  );
};

export default AppProviders;

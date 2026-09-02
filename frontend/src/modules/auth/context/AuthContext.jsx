import { createContext, useState, useContext, useEffect } from 'react';
import { verificarSesion, cerrarSesion, setTokens } from '../services/authService';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [usuario, setUsuario] = useState(null);
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    const usuarioData = verificarSesion();
    
    if (usuarioData) {
      setUsuario({
        steam_id: usuarioData.steam_id,
        nombre: usuarioData.nombre,
        avatar: usuarioData.avatar
      });
    } else {
      cerrarSesion();
      setUsuario(null);
    }
    
    setCargando(false);
  }, []);

  const login = (steamId, nombre, avatar, accessToken, refreshToken) => {
    // Guardar tokens JWT
    setTokens(accessToken, refreshToken);
    
    // Guardar datos del usuario
    const usuarioData = { steam_id: steamId, nombre, avatar };
    setUsuario(usuarioData);
    localStorage.setItem('steam_id', steamId);
    localStorage.setItem('nombre', nombre);
    localStorage.setItem('avatar', avatar || '');
  };

  const logout = () => {
    setUsuario(null);
    cerrarSesion();
    window.history.replaceState({}, document.title, '/');
  };

  return (
    <AuthContext.Provider value={{ usuario, cargando, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return context;
};
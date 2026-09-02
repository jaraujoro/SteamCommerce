import api from '../../../shared/services/api';

const TOKEN_KEY = 'access';
const REFRESH_TOKEN_KEY = 'refresh';

export const setTokens = (accessToken, refreshToken) => {
  localStorage.setItem(TOKEN_KEY, accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
};

export const getAccessToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

export const getRefreshToken = () => {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
};

export const steamLogin = async () => {
  try {
    const response = await api.get('/usuario/login');
    return response;
  } catch (error) {
    console.error('Error en login:', error);
    throw error;
  }
};

export const verificarSesion = () => {
  const accessToken = getAccessToken();
  const steamId = localStorage.getItem('steam_id');
  const nombre = localStorage.getItem('nombre');
  const avatar = localStorage.getItem('avatar');

  if (accessToken && steamId) {
    return {
      steam_id: steamId,
      nombre,
      avatar,
      access_token: accessToken,
    };
  }

  return null;
};

export const cerrarSesion = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem('steam_id');
  localStorage.removeItem('nombre');
  localStorage.removeItem('avatar');
};

export const refreshToken = async () => {
  const refresh = getRefreshToken();
  if (!refresh) return null;

  try {
    const response = await api.post('/usuario/refresh_token', { 
      refreshToken: refresh
    });
    
    if (response.accessToken) {
      setTokens(response.accessToken, refresh);
      return response.accessToken;
    }
    return null;
  } catch (error) {
    console.error('Error al refrescar token:', error);
    cerrarSesion();
    return null;
  }
};
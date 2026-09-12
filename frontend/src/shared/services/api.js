const API_URL = import.meta.env.VITE_API_URL;

// Funciones para manejar tokens
const getAccessToken = () => localStorage.getItem('access');
const getRefreshToken = () => localStorage.getItem('refresh');

const setTokens = (access, refresh) => {
  localStorage.setItem('access', access);
  localStorage.setItem('refresh', refresh);
};

const cerrarSesion = () => {
  localStorage.removeItem('access');
  localStorage.removeItem('refresh');
  localStorage.removeItem('steam_id');
  localStorage.removeItem('nombre');
  localStorage.removeItem('avatar');
};

// Función para hacer peticiones con autenticación
const fetchWithAuth = async (url, options = {}) => {
  const token = getAccessToken();

  const headers = {
    ...options.headers,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  let response;

  try {
    response = await fetch(url, {
      ...options,
      headers,
    });
  } catch (error) {
    throw new Error('Ocurrió un error al comunicarse con el servidor. Por favor, inténtalo de nuevo más tarde.');
  }

  // Si el token expiró (401), intentar refrescar
  if (response.status === 401 && token || response.status === 403) {
    const refresh = getRefreshToken();
    if (refresh) {
      try {
        const refreshResponse = await fetch(`${API_URL}/usuario/refresh_token`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ refreshToken: refresh }),
        });

        if (refreshResponse.ok) {
          const data = await refreshResponse.json();
          setTokens(data.accessToken, refresh);

          // Reintentar la petición original con el nuevo token
          headers['Authorization'] = `Bearer ${data.accessToken}`;
          response = await fetch(url, {
            ...options,
            headers,
          });
        } else {
          cerrarSesion();
          window.location.href = '/';
          throw new Error('Sesión expirada. Por favor, inicia sesión nuevamente.');
        }
      } catch (error) {
        cerrarSesion();
        window.location.href = '/';
        throw new Error('Error al refrescar la sesión. Por favor, inicia sesión nuevamente.');
      }
    } else {
      cerrarSesion();
      window.location.href = '/';
      throw new Error('No hay sesión activa. Por favor, inicia sesión.');
    }
  }

  return response;
};

const api = {
  get: async (endpoint) => {
    try {
      const response = await fetchWithAuth(`${API_URL}${endpoint}`, {
        method: 'GET',
      });

      if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new Error(error.error || error.detail);
      }
      return await response.json();
    } catch (error) {
      throw error;
    }
  },

  post: async (endpoint, data) => {
    try {
      const response = await fetchWithAuth(`${API_URL}${endpoint}`, {
        method: 'POST',
        body: data,
      });
      if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new Error(error.error || error.detail);
      }
      return await response.json();
    } catch (error) {
      throw error;
    }
  },

  put: async (endpoint, data) => {
    try {
      const response = await fetchWithAuth(`${API_URL}${endpoint}`, {
        method: 'PUT',
        body: data,
      });

      if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new Error(error.error || error.detail);
      }
      return await response.json();
    } catch (error) {
      throw error;
    }
  },

  delete: async (endpoint) => {
    try {
      const response = await fetchWithAuth(`${API_URL}${endpoint}`, {
        method: 'DELETE',
      });

      if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new Error(error.error || error.detail);
      }
      return await response.json();
    } catch (error) {
      throw error;
    }
  },
  patch: async (endpoint, data) => {
    try {
      const response = await fetchWithAuth(`${API_URL}${endpoint}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        const error = await response.json().catch(() => ({}));
        throw new Error(error.error || error.detail);
      }
      return await response.json();
    } catch (error) {
      throw error;
    }
  },
};

export default api;
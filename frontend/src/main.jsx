import { createRoot } from 'react-dom/client';
import App from './app/App.jsx';
import './styles/global.css';
import { setTokens } from './modules/auth/services/authService';

const params = new URLSearchParams(window.location.search);
const accessToken = params.get('access');
const refreshToken = params.get('refresh');
const steamId = params.get('steam_id');
const nombre = params.get('nombre');
const avatar = params.get('avatar');

if (accessToken && refreshToken && steamId) {
  setTokens(accessToken, refreshToken);

  localStorage.setItem('steam_id', steamId);
  localStorage.setItem('nombre', nombre || 'Usuario Steam');
  localStorage.setItem('avatar', avatar || '');

  window.history.replaceState({}, document.title, window.location.pathname);
}

createRoot(document.getElementById('root')).render(<App />);

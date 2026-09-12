import api from '../../../shared/services/api';

export const obtenerInventario = async () => {
  return await api.get('/item/listar_item');
};

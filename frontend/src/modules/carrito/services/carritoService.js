import api from '../../../shared/services/api';

export const comprarItem = async (assetId, codigo2fa = null) => {
  const formData = new FormData();

  if (Array.isArray(assetId)) {
    formData.append('asset_id', JSON.stringify(assetId));
  } else {
    formData.append('asset_id', assetId);
  }

  formData.append('codigo_2fa', codigo2fa);
  return await api.post('/producto/comprar-item/', formData);
};

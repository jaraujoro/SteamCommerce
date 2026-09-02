import api from '../../../shared/services/api';

export const guardarTradeUrl = async (tradeUrl) => {
  const data = {
    steamTradeUrl: tradeUrl
  };
  return await api.patch('/usuario/update_trade_url', data);
};

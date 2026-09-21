package com.SteamCommerce.tipo_pago.service;

import java.util.List;
import com.SteamCommerce.tipo_pago.dto.TipoPagoRequestDto;
import com.SteamCommerce.tipo_pago.dto.TipoPagoResponseDto;

public interface TipoPagoService {

    void crearTipoPago(TipoPagoRequestDto dto);

    void actualizarTipoPago(TipoPagoRequestDto dto, Long idTipoPago);

    void eliminarTipoPago(Long idTipoPago);

    List<TipoPagoResponseDto> listarTiposPago();

}

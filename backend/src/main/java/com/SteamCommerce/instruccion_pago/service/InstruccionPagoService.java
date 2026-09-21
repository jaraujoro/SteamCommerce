package com.SteamCommerce.instruccion_pago.service;

import com.SteamCommerce.instruccion_pago.dto.InstruccionPagoRequestDto;
import com.SteamCommerce.instruccion_pago.dto.InstruccionPagoResponseDto;
import java.util.List;

public interface InstruccionPagoService {

    InstruccionPagoResponseDto crearInstruccion(InstruccionPagoRequestDto dto);

    InstruccionPagoResponseDto actualizarInstruccion(InstruccionPagoRequestDto dto, Long idInstruccion);

    void eliminarInstruccion(Long idInstruccion);

    InstruccionPagoResponseDto obtenerInstruccionPorId(Long idInstruccion);

    List<InstruccionPagoResponseDto> listarInstrucciones();

    List<InstruccionPagoResponseDto> listarInstruccionesPorTipoPago(Long idTipoPago);
}

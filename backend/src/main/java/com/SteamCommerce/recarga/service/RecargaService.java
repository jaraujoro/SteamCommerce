package com.SteamCommerce.recarga.service;

import com.SteamCommerce.recarga.dto.RecargaComprobanteDto;
import com.SteamCommerce.recarga.dto.RecargaEstadoDto;
import com.SteamCommerce.recarga.dto.RecargaRequestDto;
import com.SteamCommerce.recarga.dto.RecargaResponseDto;
import java.util.List;
import java.util.UUID;

public interface RecargaService {

    RecargaResponseDto crearRecarga(RecargaRequestDto dto, UUID idUsuario);

    RecargaResponseDto actualizarEstado(Long idRecarga, RecargaEstadoDto dto);

    RecargaResponseDto subirComprobante(Long idRecarga, RecargaComprobanteDto dto);

    RecargaResponseDto obtenerRecargaPorId(Long idRecarga);

    RecargaResponseDto obtenerRecargaPorCodigo(String codigoRecarga);

    List<RecargaResponseDto> listarRecargas(String estado);

    List<RecargaResponseDto> listarMisRecargas(UUID idUsuario);

    void eliminarRecarga(Long idRecarga);
}
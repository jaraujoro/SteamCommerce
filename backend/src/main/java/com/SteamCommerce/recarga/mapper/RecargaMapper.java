package com.SteamCommerce.recarga.mapper;

import com.SteamCommerce.recarga.dto.RecargaRequestDto;
import com.SteamCommerce.recarga.dto.RecargaResponseDto;
import com.SteamCommerce.recarga.entity.RecargaEntity;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RecargaMapper {

    public RecargaEntity toEntity(RecargaRequestDto dto, UUID idUsuario, String codigoRecarga) {
        if (dto == null) {
            return null;
        }

        String moneda = (dto.getMoneda() != null && !dto.getMoneda().trim().isEmpty())
                ? dto.getMoneda().trim().toUpperCase()
                : "PEN";

        return RecargaEntity.builder()
                .idUsuario(idUsuario)
                .idTipoPago(dto.getIdTipoPago())
                .monto(dto.getMonto())
                .moneda(moneda)
                .codigoRecarga(codigoRecarga)
                .estado("PENDIENTE")
                .build();
    }

    public RecargaResponseDto toDto(RecargaEntity entity) {
        if (entity == null) {
            return null;
        }

        String usuarioNombre = null;
        if (entity.getUsuario() != null) {
            usuarioNombre = entity.getUsuario().getNombre();
        }

        String tipoPagoNombre = null;
        if (entity.getTipoPago() != null) {
            tipoPagoNombre = entity.getTipoPago().getNombre();
        }

        return RecargaResponseDto.builder()
                .idRecarga(entity.getIdRecarga())
                .idUsuario(entity.getIdUsuario())
                .usuarioNombre(usuarioNombre)
                .idTipoPago(entity.getIdTipoPago())
                .tipoPagoNombre(tipoPagoNombre)
                .monto(entity.getMonto())
                .moneda(entity.getMoneda())
                .codigoRecarga(entity.getCodigoRecarga())
                .mensajePago(entity.getMensajePago())
                .estado(entity.getEstado())
                .comprobanteUrl(entity.getComprobanteUrl())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaVerificacion(entity.getFechaVerificacion())
                .build();
    }
}
package com.SteamCommerce.instruccion_pago.mapper;

import com.SteamCommerce.instruccion_pago.dto.InstruccionPagoRequestDto;
import com.SteamCommerce.instruccion_pago.dto.InstruccionPagoResponseDto;
import com.SteamCommerce.instruccion_pago.entity.InstruccionPagoEntity;
import org.springframework.stereotype.Component;

@Component
public class InstruccionPagoMapper {

    public InstruccionPagoEntity toEntity(InstruccionPagoRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return InstruccionPagoEntity.builder()
                .idTipoPago(dto.getIdTipoPago())
                .titular(dto.getTitular())
                .numeroPago(dto.getNumeroPago())
                .cuentaBancaria(dto.getCuentaBancaria())
                .cci(dto.getCci())
                .logoUrl(dto.getLogoUrl())
                .montoMinimo(dto.getMontoMinimo())
                .montoMaximo(dto.getMontoMaximo())
                .instruccionesTexto(dto.getInstruccionesTexto())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
    }

    public InstruccionPagoResponseDto toDto(InstruccionPagoEntity entity) {
        if (entity == null) {
            return null;
        }

        return InstruccionPagoResponseDto.builder()
                .idInstruccion(entity.getIdInstruccion())
                .idTipoPago(entity.getIdTipoPago())
                .tipoPagoNombre(entity.getTipoPago() != null ? entity.getTipoPago().getNombre() : null)
                .titular(entity.getTitular())
                .numeroPago(entity.getNumeroPago())
                .cuentaBancaria(entity.getCuentaBancaria())
                .cci(entity.getCci())
                .logoUrl(entity.getLogoUrl())
                .montoMinimo(entity.getMontoMinimo())
                .montoMaximo(entity.getMontoMaximo())
                .instruccionesTexto(entity.getInstruccionesTexto())
                .activo(entity.getActivo())
                .build();
    }
}

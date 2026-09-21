package com.SteamCommerce.tipo_pago.mapper;

import org.springframework.stereotype.Component;
import com.SteamCommerce.tipo_pago.dto.TipoPagoRequestDto;
import com.SteamCommerce.tipo_pago.dto.TipoPagoResponseDto;
import com.SteamCommerce.tipo_pago.entity.TipoPagoEntity;

@Component
public class TipoPagoMapper {

    public TipoPagoEntity toEntity(TipoPagoRequestDto dto) {
        return TipoPagoEntity.builder()
                .nombre(dto.getNombre())
                .activo(dto.getActivo())
                .build();
    }

    public TipoPagoResponseDto toDto(TipoPagoEntity entity) {
        return TipoPagoResponseDto.builder()
                .nombre(entity.getNombre())
                .activo(entity.getActivo())
                .build();
    }

}

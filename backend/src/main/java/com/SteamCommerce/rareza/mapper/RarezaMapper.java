package com.SteamCommerce.rareza.mapper;

import com.SteamCommerce.rareza.dto.RarezaRequestDto;
import com.SteamCommerce.rareza.dto.RarezaResponseDto;
import com.SteamCommerce.rareza.entity.RarezaEntity;
import org.springframework.stereotype.Component;

@Component
public class RarezaMapper {

    public RarezaEntity toEntity(RarezaRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return RarezaEntity.builder()
                .nombre(dto.getNombre())
                .color(dto.getColor())
                .nombreInterno(dto.getNombreInterno())
                .build();
    }

    public RarezaResponseDto toResponseDto(RarezaEntity entity) {
        if (entity == null) {
            return null;
        }

        return RarezaResponseDto.builder()
                .idRareza(entity.getIdRareza())
                .nombre(entity.getNombre())
                .color(entity.getColor())
                .nombreInterno(entity.getNombreInterno())
                .build();
    }
}

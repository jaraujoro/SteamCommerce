package com.SteamCommerce.tipo_item.mapper;

import org.springframework.stereotype.Component;

import com.SteamCommerce.tipo_item.dto.TipoItemRequestDto;
import com.SteamCommerce.tipo_item.dto.TipoItemResponseDto;
import com.SteamCommerce.tipo_item.entity.TipoItemEntity;

@Component
public class TipoItemMapper {

    public TipoItemEntity toEntity(TipoItemRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return TipoItemEntity.builder()
                .nombre(dto.getNombre())
                .nombreInterno(dto.getNombreInterno())
                .build();
    }

    public TipoItemResponseDto toResponseDto(TipoItemEntity entity) {
        if (entity == null) {
            return null;
        }

        return TipoItemResponseDto.builder()
                .idTipoItem(entity.getIdTipoItem())
                .nombre(entity.getNombre())
                .nombreInterno(entity.getNombreInterno())
                .build();
    }
}

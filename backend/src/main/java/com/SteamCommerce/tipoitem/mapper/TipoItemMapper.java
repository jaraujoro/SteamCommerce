package com.SteamCommerce.tipoitem.mapper;

import com.SteamCommerce.tipoitem.dto.TipoItemRequestDto;
import com.SteamCommerce.tipoitem.dto.TipoItemResponseDto;
import com.SteamCommerce.tipoitem.entity.TipoItemEntity;
import org.springframework.stereotype.Component;

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

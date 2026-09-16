package com.SteamCommerce.heroe.mapper;

import com.SteamCommerce.heroe.dto.HeroeRequestDto;
import com.SteamCommerce.heroe.dto.HeroeResponseDto;
import com.SteamCommerce.heroe.entity.HeroeEntity;
import org.springframework.stereotype.Component;

@Component
public class HeroeMapper {

    public HeroeEntity toEntity(HeroeRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return HeroeEntity.builder()
                .nombre(dto.getNombre())
                .nombreInterno(dto.getNombreInterno())
                .build();
    }

    public HeroeResponseDto toResponseDto(HeroeEntity entity) {
        if (entity == null) {
            return null;
        }

        return HeroeResponseDto.builder()
                .idHeroe(entity.getIdHeroe())
                .nombre(entity.getNombre())
                .nombreInterno(entity.getNombreInterno())
                .build();
    }

    public HeroeEntity toEntity(String nombre, String nombreInterno) {
        return HeroeEntity.builder()
                .nombre(nombre)
                .nombreInterno(nombreInterno)
                .build();
    }
}

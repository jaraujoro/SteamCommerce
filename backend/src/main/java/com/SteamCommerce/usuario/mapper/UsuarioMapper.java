package com.SteamCommerce.usuario.mapper;

import com.SteamCommerce.usuario.dto.UsuarioRequestDto;
import com.SteamCommerce.usuario.dto.UsuarioResponseDto;
import com.SteamCommerce.usuario.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioEntity toEntity(UsuarioRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return UsuarioEntity.builder()
                .steamId(dto.getSteamId())
                .nombre(dto.getNombre())
                .avatar(dto.getAvatar())
                .tradeUrl(dto.getTradeUrl())
                .saldoSoles(dto.getSaldoSoles())
                .role(dto.getRole() != null ? dto.getRole() : "Cliente")
                .build();
    }

    public UsuarioResponseDto toResponseDto(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }
        return UsuarioResponseDto.builder()
                .idUsuario(entity.getIdUsuario())
                .steamId(entity.getSteamId())
                .nombre(entity.getNombre())
                .avatar(entity.getAvatar())
                .tradeUrl(entity.getTradeUrl())
                .saldoSoles(entity.getSaldoSoles())
                .role(entity.getRole())
                .build();
    }

    // Actualizar solo el tradeUrl
    public void updateTradeUrl(UsuarioEntity entity, String tradeUrl) {
        if (entity != null && tradeUrl != null) {
            entity.setTradeUrl(tradeUrl.trim());
        }
    }
}
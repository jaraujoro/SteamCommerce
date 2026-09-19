package com.SteamCommerce.usuario.mapper;

import com.SteamCommerce.usuario.dto.AuthResponse;
import com.SteamCommerce.usuario.dto.LoginResponse;
import com.SteamCommerce.usuario.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(UsuarioEntity usuario, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .steamId(usuario.getSteamId())
                .nombre(usuario.getNombre())
                .avatar(usuario.getAvatar())
                .build();
    }

    public LoginResponse toLoginResponse(String url) {
        return LoginResponse.builder()
                .loginUrl(url)
                .build();
    }

}
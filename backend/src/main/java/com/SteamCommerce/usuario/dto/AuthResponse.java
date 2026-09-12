package com.SteamCommerce.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String steamId;
    private String nombre;
    private String avatar;
    private String rol;
}
package com.SteamCommerce.usuario.service;

import java.util.UUID;

import com.SteamCommerce.usuario.dto.ActualizarTradeUrlDto;
import com.SteamCommerce.usuario.dto.UsuarioRequestDto;

public interface UsuarioService {

    void actualizarTradeUrl(UUID idUsuario, ActualizarTradeUrlDto dto);

    void crearUsuario(UsuarioRequestDto dto);
}

package com.SteamCommerce.usuario.service;

import com.SteamCommerce.usuario.dto.UsuarioRequestDto;
import com.SteamCommerce.usuario.dto.UsuarioResponseDto;

public interface UsuarioService {

    UsuarioResponseDto actualizarTradeUrl(UsuarioRequestDto dto);

    void crearUsuario(UsuarioRequestDto dto);
}

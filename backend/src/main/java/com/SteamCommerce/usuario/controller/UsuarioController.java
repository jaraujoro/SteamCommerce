package com.SteamCommerce.usuario.controller;

import com.SteamCommerce.common.ApiResponse;
import com.SteamCommerce.usuario.dto.ActualizarTradeUrlDto;
import com.SteamCommerce.usuario.dto.UsuarioRequestDto;
import com.SteamCommerce.usuario.entity.UsuarioEntity;
import com.SteamCommerce.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ApiResponse<Void> crearUsuario(@Valid @RequestBody UsuarioRequestDto dto) {
        usuarioService.crearUsuario(dto);
        return ApiResponse.success("Usuario creado exitosamente", HttpStatus.CREATED.value());
    }

    @PatchMapping("/trade-url")
    public ApiResponse<Void> actualizarTradeUrl(@Valid @RequestBody ActualizarTradeUrlDto dto,
            @AuthenticationPrincipal UsuarioEntity usuarioAutenticado) {

        usuarioService.actualizarTradeUrl(usuarioAutenticado.getIdUsuario(), dto);
        return ApiResponse.success("URL actualizada correctamente", HttpStatus.OK.value());
    }
}
package com.SteamCommerce.usuario.controller;

import com.SteamCommerce.common.ApiResponse;
import com.SteamCommerce.usuario.dto.UsuarioRequestDto;
import com.SteamCommerce.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/crear_usuario")
    public ResponseEntity<ApiResponse<Void>> crearUsuario(@Valid @RequestBody UsuarioRequestDto dto) {
        usuarioService.crearUsuario(dto);
        ApiResponse<Void> response = ApiResponse.success("Usuario creado exitosamente", HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/actualizar_trade_url")
    public ResponseEntity<ApiResponse<Void>> actualizarTradeUrl(@Valid @RequestBody UsuarioRequestDto dto) {
        usuarioService.actualizarTradeUrl(dto);
        ApiResponse<Void> response = ApiResponse.success("URL actualizada correctamente", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
package com.SteamCommerce.rareza.controller;

import com.SteamCommerce.common.ApiResponse;
import com.SteamCommerce.rareza.dto.RarezaRequestDto;
import com.SteamCommerce.rareza.dto.RarezaResponseDto;
import com.SteamCommerce.rareza.service.RarezaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/rareza")
@RequiredArgsConstructor
public class RarezaController {

    private final RarezaService rarezaService;

    @GetMapping("/listar_rareza")
    public ResponseEntity<ApiResponse<List<RarezaResponseDto>>> listarRareza() {
        List<RarezaResponseDto> rarezas = rarezaService.listarRarezas();
        ApiResponse<List<RarezaResponseDto>> response = ApiResponse.success(
                "Rarezas listadas exitosamente",
                HttpStatus.OK.value(),
                rarezas);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/crear_rareza")
    public ResponseEntity<ApiResponse<Void>> crearRareza(@Valid @RequestBody RarezaRequestDto dto) {
        rarezaService.crearRareza(dto);
        ApiResponse<Void> response = ApiResponse.success("Rareza creada exitosamente", HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

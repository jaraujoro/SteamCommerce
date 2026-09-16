package com.SteamCommerce.tipoitem.controller;

import com.SteamCommerce.common.ApiResponse;
import com.SteamCommerce.tipoitem.dto.TipoItemRequestDto;
import com.SteamCommerce.tipoitem.dto.TipoItemResponseDto;
import com.SteamCommerce.tipoitem.service.TipoItemService;
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
@RequestMapping("/tipo_item")
@RequiredArgsConstructor
public class TipoItemController {

    private final TipoItemService tipoItemService;

    @GetMapping("/listar_tipo_item")
    public ResponseEntity<ApiResponse<List<TipoItemResponseDto>>> listarTipoItem() {
        List<TipoItemResponseDto> tipos = tipoItemService.listarTipos();
        ApiResponse<List<TipoItemResponseDto>> response = ApiResponse.success(
                "Tipos de item listados exitosamente",
                HttpStatus.OK.value(),
                tipos);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/crear_tipo_item")
    public ResponseEntity<ApiResponse<Void>> crearTipoItem(@Valid @RequestBody TipoItemRequestDto dto) {
        tipoItemService.crearTipo(dto);
        ApiResponse<Void> response = ApiResponse.success("Tipo de item creado exitosamente",
                HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

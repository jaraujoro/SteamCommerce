package com.SteamCommerce.tipo_item.controller;

import com.SteamCommerce.common.ApiResponse;
import com.SteamCommerce.tipo_item.dto.TipoItemRequestDto;
import com.SteamCommerce.tipo_item.dto.TipoItemResponseDto;
import com.SteamCommerce.tipo_item.service.TipoItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/tipo-item")
@RequiredArgsConstructor
public class TipoItemController {

    private final TipoItemService tipoItemService;

    @GetMapping
    public ApiResponse<List<TipoItemResponseDto>> listarTipoItem() {
        return ApiResponse.success("Tipos de item listados exitosamente", HttpStatus.OK.value(),
                tipoItemService.listarTipos());
    }

    @PostMapping
    public ApiResponse<Void> crearTipoItem(@Valid @RequestBody TipoItemRequestDto dto) {
        tipoItemService.crearTipo(dto);
        return ApiResponse.success("Tipo de item creado exitosamente", HttpStatus.CREATED.value());
    }
}

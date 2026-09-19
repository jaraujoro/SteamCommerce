package com.SteamCommerce.item.controller;

import lombok.AllArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.SteamCommerce.common.ApiResponse;
import com.SteamCommerce.item.dto.ItemRequestDto;
import com.SteamCommerce.item.dto.ItemResponseDto;
import com.SteamCommerce.item.service.ItemService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/item")
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/listar_item")
    public ApiResponse<List<ItemResponseDto>> listarItem() {
        return ApiResponse.success("Items listados exitosamente", HttpStatus.OK.value(), itemService.listarItems());
    }

    @PostMapping("/crear_item")
    public ApiResponse<Void> crearItem(@Valid @RequestBody ItemRequestDto dto) {
        itemService.crearItem(dto);
        return ApiResponse.success("Item creado exitosamente", HttpStatus.CREATED.value());
    }

}

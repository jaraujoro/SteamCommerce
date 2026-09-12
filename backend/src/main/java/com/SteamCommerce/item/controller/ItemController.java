package com.SteamCommerce.item.controller;

import lombok.AllArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> listarItem() {
        List<ItemResponseDto> items = itemService.listarItems();
        ApiResponse<List<ItemResponseDto>> response = ApiResponse.success("Items listados exitosamente",
                HttpStatus.OK.value(), items);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/crear_item")
    public ResponseEntity<ApiResponse<Void>> crearItem(@Valid @RequestBody ItemRequestDto dto) {
        itemService.crearItem(dto);
        ApiResponse<Void> response = ApiResponse.success("Item creado exitosamente", HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}

package com.SteamCommerce.heroe.controller;

import com.SteamCommerce.common.ApiResponse;
import com.SteamCommerce.heroe.dto.HeroeRequestDto;
import com.SteamCommerce.heroe.dto.HeroeResponseDto;
import com.SteamCommerce.heroe.service.HeroeService;
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
@RequestMapping("/heroe")
@RequiredArgsConstructor
public class HeroeController {

    private final HeroeService heroeService;

    @GetMapping
    public ApiResponse<List<HeroeResponseDto>> listarHeroe() {
        return ApiResponse.success("Héroes listados exitosamente", HttpStatus.OK.value(), heroeService.listarHeroes());
    }

    @PostMapping
    public ApiResponse<Void> crearHeroe(@Valid @RequestBody HeroeRequestDto dto) {
        heroeService.crearHeroe(dto);
        return ApiResponse.success("Héroe creado exitosamente", HttpStatus.CREATED.value());
    }
}

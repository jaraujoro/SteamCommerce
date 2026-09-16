package com.SteamCommerce.heroe.service;

import com.SteamCommerce.heroe.dto.HeroeRequestDto;
import com.SteamCommerce.heroe.dto.HeroeResponseDto;
import java.util.List;

public interface HeroeService {

    List<HeroeResponseDto> listarHeroes();

    void crearHeroe(HeroeRequestDto dto);

    Long obtenerOCrear(String nombre, String nombreInterno);

    void precargarCache();
}

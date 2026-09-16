package com.SteamCommerce.rareza.service;

import com.SteamCommerce.rareza.dto.RarezaRequestDto;
import com.SteamCommerce.rareza.dto.RarezaResponseDto;
import java.util.List;

public interface RarezaService {

    List<RarezaResponseDto> listarRarezas();

    void crearRareza(RarezaRequestDto dto);

    Long obtenerOCrear(String nombre, String nombreInterno, String color);

    void precargarCache();
}

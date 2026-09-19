package com.SteamCommerce.tipo_item.service;

import java.util.List;

import com.SteamCommerce.tipo_item.dto.TipoItemRequestDto;
import com.SteamCommerce.tipo_item.dto.TipoItemResponseDto;

public interface TipoItemService {

    List<TipoItemResponseDto> listarTipos();

    void crearTipo(TipoItemRequestDto dto);

    Long obtenerOCrear(String nombre, String nombreInterno);

    void precargarCache();
}

package com.SteamCommerce.tipoitem.service;

import com.SteamCommerce.tipoitem.dto.TipoItemRequestDto;
import com.SteamCommerce.tipoitem.dto.TipoItemResponseDto;
import java.util.List;

public interface TipoItemService {

    List<TipoItemResponseDto> listarTipos();

    void crearTipo(TipoItemRequestDto dto);

    Long obtenerOCrear(String nombre, String nombreInterno);

    void precargarCache();
}

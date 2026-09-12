package com.SteamCommerce.item.service;

import com.SteamCommerce.item.dto.ItemRequestDto;
import com.SteamCommerce.item.dto.ItemResponseDto;
import com.SteamCommerce.item.dto.ResultadoSync;
import java.util.List;

public interface ItemService {

    List<ItemResponseDto> listarItems();

    void crearItem(ItemRequestDto dto);

    ResultadoSync crearItemsEnBatch(List<ItemRequestDto> items);
}

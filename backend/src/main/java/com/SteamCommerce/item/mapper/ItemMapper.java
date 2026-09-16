package com.SteamCommerce.item.mapper;

import com.SteamCommerce.item.dto.ItemRequestDto;
import com.SteamCommerce.item.dto.ItemResponseDto;
import com.SteamCommerce.item.entity.ItemEntity;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemEntity toEntity(ItemRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        return ItemEntity.builder()
                .assetId(requestDto.getAssetId())
                .marketHashName(requestDto.getMarketHashName())
                .iconUrl(requestDto.getIconUrl())
                .color(requestDto.getColor())
                .tradable(requestDto.getTradable())
                .marketable(requestDto.getMarketable())
                .marketTradableRestriction(requestDto.getMarketTradableRestriction())
                .tradeCooldownUntil(requestDto.getTradeCooldownUntil())
                .idTipoItem(requestDto.getIdTipoItem())
                .idRareza(requestDto.getIdRareza())
                .idHeroe(requestDto.getIdHeroe())
                .build();
    }

    public ItemResponseDto toResponseDto(ItemEntity entity) {
        if (entity == null) {
            return null;
        }

        return ItemResponseDto.builder()
                .publicId(entity.getPublicId())
                .marketHashName(entity.getMarketHashName())
                .iconUrl(entity.getIconUrl())
                .color(entity.getColor())
                .tradable(entity.getTradable())
                .marketable(entity.getMarketable())
                .marketTradableRestriction(entity.getMarketTradableRestriction())
                .tradeCooldownUntil(entity.getTradeCooldownUntil())
                .tipoItem(entity.getTipoItem() != null ? entity.getTipoItem().getNombre() : null)
                .rarity(entity.getRareza() != null ? entity.getRareza().getNombre() : null)
                .hero(entity.getHeroe() != null ? entity.getHeroe().getNombre() : null)
                .creadoEn(entity.getCreadoEn())
                .build();
    }
}
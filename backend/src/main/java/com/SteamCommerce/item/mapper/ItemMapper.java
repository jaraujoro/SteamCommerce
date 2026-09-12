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
                .appId(requestDto.getAppId())
                .contextId(requestDto.getContextId())
                .classId(requestDto.getClassId())
                .instanceId(requestDto.getInstanceId())
                .amount(requestDto.getAmount())
                .name(requestDto.getName())
                .marketName(requestDto.getMarketName())
                .marketHashName(requestDto.getMarketHashName())
                .iconUrl(requestDto.getIconUrl())
                .color(requestDto.getColor())
                .tradable(requestDto.getTradable())
                .marketable(requestDto.getMarketable())
                .commodity(requestDto.getCommodity())
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
                .assetId(entity.getAssetId())
                .appId(entity.getAppId())
                .contextId(entity.getContextId())
                .classId(entity.getClassId())
                .instanceId(entity.getInstanceId())
                .amount(entity.getAmount())
                .name(entity.getName())
                .marketName(entity.getMarketName())
                .marketHashName(entity.getMarketHashName())
                .iconUrl(entity.getIconUrl())
                .color(entity.getColor())
                .tradable(entity.getTradable())
                .marketable(entity.getMarketable())
                .commodity(entity.getCommodity())
                .marketTradableRestriction(entity.getMarketTradableRestriction())
                .tradeCooldownUntil(entity.getTradeCooldownUntil())
                .idTipoItem(entity.getIdTipoItem())
                .idRareza(entity.getIdRareza())
                .idHeroe(entity.getIdHeroe())
                .creadoEn(entity.getCreadoEn())
                .build();
    }
}
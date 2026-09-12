package com.SteamCommerce.steam.mapper;

import com.SteamCommerce.item.dto.ItemRequestDto;
import com.SteamCommerce.steam.dto.SteamAsset;
import com.SteamCommerce.steam.dto.SteamDescription;
import com.SteamCommerce.steam.dto.SteamTag;
import com.SteamCommerce.steam.util.TradeCooldownParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Slf4j
public class SteamItemMapper {

    private final Long ID_TIPO_ITEM_DEFAULT = 1L;
    private final Long ID_RAREZA_DEFAULT = 1L;
    private final Long ID_HEROE_DEFAULT = 1L;

    public ItemRequestDto toItemRequestDto(SteamAsset asset, SteamDescription desc) {

        if (asset == null || desc == null) {
            return null;
        }

        String rarityColor = null;
        if (desc.getTags() != null) {
            for (SteamTag tag : desc.getTags()) {
                if ("Rarity".equals(tag.getCategory())) {
                    rarityColor = tag.getColor();
                    break;
                }
            }
        }

        String finalColor = rarityColor != null ? rarityColor : desc.getColor();

        LocalDateTime cooldownDate = TradeCooldownParser.extractCooldownDate(desc.getDescriptions());

        ItemRequestDto dto = ItemRequestDto.builder()
                .assetId(asset.getAssetid())
                .appId(asset.getAppid())
                .contextId(asset.getContextid())
                .classId(asset.getClassid())
                .instanceId(asset.getInstanceid())
                .amount(asset.getAmount())
                .name(desc.getName())
                .marketName(desc.getMarket_name())
                .marketHashName(desc.getMarket_hash_name())
                .iconUrl(desc.getIcon_url())
                .color(finalColor)
                .tradable(desc.getTradable())
                .marketable(desc.getMarketable())
                .commodity(desc.getCommodity())
                .marketTradableRestriction(desc.getMarket_tradable_restriction())
                .tradeCooldownUntil(cooldownDate)
                .idTipoItem(ID_TIPO_ITEM_DEFAULT)
                .idRareza(ID_RAREZA_DEFAULT)
                .idHeroe(ID_HEROE_DEFAULT)
                .build();

        return dto;
    }
}
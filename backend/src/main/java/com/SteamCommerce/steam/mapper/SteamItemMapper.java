package com.SteamCommerce.steam.mapper;

import com.SteamCommerce.heroe.service.HeroeService;
import com.SteamCommerce.item.dto.ItemRequestDto;
import com.SteamCommerce.rareza.service.RarezaService;
import com.SteamCommerce.steam.dto.SteamAsset;
import com.SteamCommerce.steam.dto.SteamDescription;
import com.SteamCommerce.steam.dto.SteamTag;
import com.SteamCommerce.steam.util.TradeCooldownParser;
import com.SteamCommerce.tipo_item.service.TipoItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SteamItemMapper {

    private final HeroeService heroeService;
    private final RarezaService rarezaService;
    private final TipoItemService tipoItemService;

    public ItemRequestDto toItemRequestDto(SteamAsset asset, SteamDescription desc) {

        if (asset == null || desc == null) {
            return null;
        }

        String heroName = null;
        String heroInternal = null;

        String rarityName = null;
        String rarityInternal = null;
        String rarityColor = null;

        String typeName = null;
        String typeInternal = null;

        String slotName = null;
        String slotInternal = null;

        if (desc.getTags() != null) {
            for (SteamTag tag : desc.getTags()) {
                if (tag == null || tag.getCategory() == null)
                    continue;

                String category = tag.getCategory();

                if ("Hero".equalsIgnoreCase(category)) {
                    heroName = tag.getNombre();
                    heroInternal = tag.getInternalName();
                } else if ("Rarity".equalsIgnoreCase(category)) {
                    rarityName = tag.getNombre();
                    rarityInternal = tag.getInternalName();
                    if (tag.getColor() != null && !tag.getColor().isBlank()) {
                        rarityColor = tag.getColor();
                    }
                } else if ("Type".equalsIgnoreCase(category)) {
                    typeName = tag.getNombre();
                    typeInternal = tag.getInternalName();
                } else if ("Slot".equalsIgnoreCase(category)) {
                    slotName = tag.getNombre();
                    slotInternal = tag.getInternalName();
                }
            }
        }

        // Resolución de Héroe
        if (heroName == null || heroName.isBlank()) {
            heroName = "Sin héroe";
            heroInternal = "npc_dota_hero_none";
        }
        Long idHeroe = heroeService.obtenerOCrear(heroName, heroInternal);

        // Resolución de Rareza
        if (rarityName == null || rarityName.isBlank()) {
            rarityName = "Común";
            rarityInternal = "Rarity_Common";
        }
        Long idRareza = rarezaService.obtenerOCrear(rarityName, rarityInternal, rarityColor);

        // Resolución de Tipo de Item
        if (typeName == null || typeName.isBlank()) {
            if (slotName != null && !slotName.isBlank()) {
                typeName = slotName;
                typeInternal = slotInternal;
            } else {
                typeName = "Otro";
                typeInternal = "other";
            }
        }
        Long idTipoItem = tipoItemService.obtenerOCrear(typeName, typeInternal);

        String finalColor = rarityColor != null ? rarityColor : desc.getColor();

        LocalDateTime cooldownDate = TradeCooldownParser.extractCooldownDate(desc.getDescriptions());

        ItemRequestDto dto = ItemRequestDto.builder()
                .assetId(asset.getAssetid())
                .marketHashName(desc.getMarket_hash_name())
                .iconUrl(desc.getIcon_url())
                .color(finalColor)
                .tradable(desc.getTradable())
                .marketable(desc.getMarketable())
                .marketTradableRestriction(desc.getMarket_tradable_restriction())
                .tradeCooldownUntil(cooldownDate)
                .idTipoItem(idTipoItem)
                .idRareza(idRareza)
                .idHeroe(idHeroe)
                .build();

        return dto;
    }
}

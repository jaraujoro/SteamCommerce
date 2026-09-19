package com.SteamCommerce.steam.mapper;

import com.SteamCommerce.heroe.service.HeroeService;
import com.SteamCommerce.item.dto.ItemRequestDto;
import com.SteamCommerce.rareza.service.RarezaService;
import com.SteamCommerce.steam.dto.SteamAsset;
import com.SteamCommerce.steam.dto.SteamDescription;
import com.SteamCommerce.steam.dto.SteamTag;
import com.SteamCommerce.tipo_item.service.TipoItemService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SteamItemMapperTest {

    @Mock
    private HeroeService heroeService;

    @Mock
    private RarezaService rarezaService;

    @Mock
    private TipoItemService tipoItemService;

    @InjectMocks
    private SteamItemMapper steamItemMapper;

    private SteamAsset asset;
    private SteamDescription desc;

    @BeforeEach
    void setUp() {
        asset = new SteamAsset();
        asset.setAssetid("12345");
        asset.setAppid("570");
        asset.setContextid("2");
        asset.setClassid("999");
        asset.setInstanceid("0");
        asset.setAmount(1);

        desc = new SteamDescription();
        desc.setName("Dragonclaw Hook");
        desc.setMarket_name("Dragonclaw Hook");
        desc.setMarket_hash_name("Dragonclaw Hook");
        desc.setTradable(true);
        desc.setMarketable(true);
    }

    @Test
    void testToItemRequestDtoWithTags() {
        SteamTag heroTag = SteamTag.builder()
                .category("Hero")
                .name("Pudge")
                .internalName("npc_dota_hero_pudge")
                .build();

        SteamTag rarityTag = SteamTag.builder()
                .category("Rarity")
                .name("Immortal")
                .internalName("Rarity_Immortal")
                .color("e4ae39")
                .build();

        SteamTag typeTag = SteamTag.builder()
                .category("Type")
                .name("Wearable")
                .internalName("wearable")
                .build();

        desc.setTags(List.of(heroTag, rarityTag, typeTag));

        when(heroeService.obtenerOCrear("Pudge", "npc_dota_hero_pudge")).thenReturn(10L);
        when(rarezaService.obtenerOCrear("Immortal", "Rarity_Immortal", "e4ae39")).thenReturn(20L);
        when(tipoItemService.obtenerOCrear("Wearable", "wearable")).thenReturn(30L);

        ItemRequestDto dto = steamItemMapper.toItemRequestDto(asset, desc);

        assertNotNull(dto);
        assertEquals(10L, dto.getIdHeroe());
        assertEquals(20L, dto.getIdRareza());
        assertEquals(30L, dto.getIdTipoItem());
        assertEquals("e4ae39", dto.getColor());

        verify(heroeService).obtenerOCrear("Pudge", "npc_dota_hero_pudge");
        verify(rarezaService).obtenerOCrear("Immortal", "Rarity_Immortal", "e4ae39");
        verify(tipoItemService).obtenerOCrear("Wearable", "wearable");
    }

    @Test
    void testToItemRequestDtoWithoutTagsDefaults() {
        desc.setTags(null);

        when(heroeService.obtenerOCrear("Sin héroe", "npc_dota_hero_none")).thenReturn(1L);
        when(rarezaService.obtenerOCrear(eq("Común"), eq("Rarity_Common"), eq(null))).thenReturn(1L);
        when(tipoItemService.obtenerOCrear("Otro", "other")).thenReturn(1L);

        ItemRequestDto dto = steamItemMapper.toItemRequestDto(asset, desc);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdHeroe());
        assertEquals(1L, dto.getIdRareza());
        assertEquals(1L, dto.getIdTipoItem());
    }
}

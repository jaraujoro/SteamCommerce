package com.SteamCommerce.steam.service;

import com.SteamCommerce.heroe.service.HeroeService;
import com.SteamCommerce.item.dto.ItemRequestDto;
import com.SteamCommerce.item.dto.ResultadoSync;
import com.SteamCommerce.item.service.ItemService;
import com.SteamCommerce.rareza.service.RarezaService;
import com.SteamCommerce.steam.dto.SteamAsset;
import com.SteamCommerce.steam.dto.SteamDescription;
import com.SteamCommerce.steam.dto.SteamInventoryResponse;
import com.SteamCommerce.steam.mapper.SteamItemMapper;
import com.SteamCommerce.tipoitem.service.TipoItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventorySyncService {

    private final SteamInventoryService steamInventoryService;
    private final ItemService itemService;
    private final SteamItemMapper steamItemMapper;
    private final HeroeService heroeService;
    private final RarezaService rarezaService;
    private final TipoItemService tipoItemService;

    public void sincronizarInventario() {
        sincronizarInventario("76561198290695666", "570");
    }

    public void sincronizarInventario(String steamId, String appId) {

        long inicio = System.currentTimeMillis();

        heroeService.precargarCache();
        rarezaService.precargarCache();
        tipoItemService.precargarCache();

        List<SteamInventoryResponse> todasLasPaginas = steamInventoryService.obtenerInventarioCompleto(steamId, appId);

        if (todasLasPaginas.isEmpty()) {
            log.warn("No se pudo obtener el inventario de Steam");
            return;
        }

        // 1. Mapa de descripciones
        Map<String, SteamDescription> mapaDescripciones = new HashMap<>();
        for (SteamInventoryResponse pagina : todasLasPaginas) {
            if (pagina.getDescriptions() != null) {
                for (SteamDescription desc : pagina.getDescriptions()) {
                    mapaDescripciones.put(desc.getClassid(), desc);
                }
            }
        }

        // 2. Recorrer todo y armar la lista de DTOs
        List<ItemRequestDto> candidatos = new ArrayList<>();
        int sinDescripcion = 0;
        int noTradables = 0;
        int totalItems = 0;

        for (SteamInventoryResponse pagina : todasLasPaginas) {
            if (pagina.getAssets() == null)
                continue;

            for (SteamAsset asset : pagina.getAssets()) {
                totalItems++;

                SteamDescription desc = mapaDescripciones.get(asset.getClassid());
                if (desc == null) {
                    sinDescripcion++;
                    continue;
                }

                ItemRequestDto dto = steamItemMapper.toItemRequestDto(asset, desc);
                if (dto == null)
                    continue;

                boolean esTradable = Boolean.TRUE.equals(dto.getTradable());
                boolean tieneCooldown = dto.getTradeCooldownUntil() != null;

                if (!esTradable && !tieneCooldown) {
                    noTradables++;
                    continue;
                }
                candidatos.add(dto);
            }
        }

        log.info("Total items: {} | Candidatos: {} | No tradables: {} | Sin descripción: {}",
                totalItems, candidatos.size(), noTradables, sinDescripcion);

        // 3. Guardar en batch
        ResultadoSync resultado = itemService.crearItemsEnBatch(candidatos);

        long duracion = System.currentTimeMillis() - inicio;

        log.info("===== RESUMEN DE SINCRONIZACIÓN =====");
        log.info("Nuevos guardados: {}", resultado.guardados());
        log.info("Actualizados: {}", resultado.actualizados());
        log.info("Total procesados: {}", resultado.total());
        log.info("No tradables (omitidos): {}", noTradables);
        log.info("Sin descripción: {}", sinDescripcion);
        log.info("Tiempo total: {} ms", duracion);
        log.info("SINCRONIZACIÓN COMPLETADA");
    }
}
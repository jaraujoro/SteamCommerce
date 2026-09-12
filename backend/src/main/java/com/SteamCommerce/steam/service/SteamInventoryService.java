package com.SteamCommerce.steam.service;

import com.SteamCommerce.steam.dto.SteamInventoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SteamInventoryService {

    private final RestTemplate restTemplate;

    // Máximo de items que Steam permite por página
    private static final int ITEMS_POR_PAGINA = 500;

    // Delay entre páginas para no activar el rate limit de Steam
    private static final int DELAY_ENTRE_PAGINAS_MS = 1000;

    // Espera cuando Steam responde 429 (rate limit)
    private static final int ESPERA_RATE_LIMIT_MS = 5000;

    // Máximo de reintentos por página antes de abortar
    private static final int MAX_REINTENTOS_PAGINA = 1;

    // Tope de seguridad para no entrar en loop infinito
    private static final int MAX_PAGINAS = 100;

    // Headers para simular un navegador real
    private static final HttpHeaders HEADERS = new HttpHeaders();

    static {
        HEADERS.set("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        HEADERS.set("Accept-Language", "english");
        HEADERS.set("Accept", "application/json");
    }

    public SteamInventoryResponse obtenerInventario(String steamId, String appId) {
        return obtenerInventario(steamId, appId, null, false);
    }

    public SteamInventoryResponse obtenerInventario(String steamId, String appId, String startAssetId) {
        return obtenerInventario(steamId, appId, startAssetId, false);
    }

    private SteamInventoryResponse obtenerInventario(String steamId, String appId,
            String startAssetId, boolean reintento) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://steamcommunity.com/inventory/" + steamId + "/" + appId + "/2")
                .queryParam("l", "english")
                .queryParam("count", ITEMS_POR_PAGINA);

        if (startAssetId != null && !startAssetId.isBlank()) {
            builder.queryParam("start_assetid", startAssetId);
        }

        String url = builder.build().toUriString();

        log.debug("📡 Consultando Steam: {}", url);

        try {
            HttpEntity<String> entity = new HttpEntity<>(HEADERS);
            ResponseEntity<SteamInventoryResponse> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    SteamInventoryResponse.class);

            SteamInventoryResponse respuesta = responseEntity.getBody();

            if (respuesta != null && respuesta.isSuccess()) {
                int itemsEnPagina = respuesta.getAssets() != null ? respuesta.getAssets().size() : 0;
                log.debug("✅ Página obtenida: {} items | Total: {}",
                        itemsEnPagina, respuesta.getTotal_inventory_count());
                return respuesta;
            }
            return null;

        } catch (Exception e) {
            String mensaje = e.getMessage();

            // Rate limit (429): reintentar UNA sola vez
            if (mensaje != null && mensaje.contains("429")) {
                if (reintento) {
                    log.error("❌ Steam sigue devolviendo 429 tras el reintento. Abortando.");
                    return null;
                }
                log.warn("⚠️ Rate Limit de Steam (429) - Esperando {} ms antes de reintentar",
                        ESPERA_RATE_LIMIT_MS);
                dormir(ESPERA_RATE_LIMIT_MS);
                return obtenerInventario(steamId, appId, startAssetId, true);
            }

            log.error("❌ Error al consultar Steam: {}", mensaje);
            return null;
        }
    }

    public List<SteamInventoryResponse> obtenerInventarioCompleto(String steamId, String appId) {

        List<SteamInventoryResponse> todasLasPaginas = new ArrayList<>();
        String startAssetId = null;
        int pagina = 1;
        int totalItemsObtenidos = 0;
        int intentosFallidos = 0;
        Integer totalInventarioEsperado = null;

        log.info("🔄 Iniciando obtención de TODAS las páginas...");

        while (pagina <= MAX_PAGINAS) {

            log.info("📄 Obteniendo página {}...", pagina);

            SteamInventoryResponse respuesta = obtenerInventario(steamId, appId, startAssetId);

            if (respuesta == null || !respuesta.isSuccess()) {
                intentosFallidos++;
                if (intentosFallidos >= MAX_REINTENTOS_PAGINA) {
                    log.warn("⚠️ Demasiados intentos fallidos ({}), terminando", intentosFallidos);
                    break;
                }

                // Backoff exponencial: 2s, 4s, 8s...
                long espera = (long) Math.pow(2, intentosFallidos) * 1000;
                log.info("⏳ Esperando {} ms antes de reintentar...", espera);
                dormir(espera);
                continue;
            }

            // Reset del contador al recibir una respuesta válida
            intentosFallidos = 0;

            // Guardar el total esperado la primera vez
            if (totalInventarioEsperado == null) {
                totalInventarioEsperado = respuesta.getTotal_inventory_count();
                log.info("📊 Total de items en inventario según Steam: {}", totalInventarioEsperado);
            }

            // Sin assets → no hay más páginas
            if (respuesta.getAssets() == null || respuesta.getAssets().isEmpty()) {
                log.info("✅ No hay más items, última página: {}", pagina - 1);
                break;
            }

            todasLasPaginas.add(respuesta);

            int itemsEnPagina = respuesta.getAssets().size();
            totalItemsObtenidos += itemsEnPagina;

            log.info("✅ Página {}: {} items | Total acumulado: {}/{}",
                    pagina, itemsEnPagina, totalItemsObtenidos, totalInventarioEsperado);

            // Ya tenemos todo lo esperado
            if (totalInventarioEsperado != null && totalItemsObtenidos >= totalInventarioEsperado) {
                log.info("✅ Todos los items han sido obtenidos");
                break;
            }

            // Página incompleta → es la última
            if (itemsEnPagina < ITEMS_POR_PAGINA) {
                log.info("✅ Última página (menos de {} items)", ITEMS_POR_PAGINA);
                break;
            }

            // Preparar siguiente página
            startAssetId = respuesta.getAssets().get(itemsEnPagina - 1).getAssetid();
            log.debug("🔑 Next start_assetid: {}", startAssetId);

            dormir(DELAY_ENTRE_PAGINAS_MS);
            pagina++;
        }

        if (pagina > MAX_PAGINAS) {
            log.warn("⚠️ Se alcanzó el máximo de páginas ({}), deteniendo por seguridad", MAX_PAGINAS);
        }

        log.info("📊 ===== RESUMEN DE PÁGINAS =====");
        log.info("📄 Total de páginas obtenidas: {}", todasLasPaginas.size());
        log.info("📦 Total de items obtenidos: {}", totalItemsObtenidos);
        if (totalInventarioEsperado != null) {
            log.info("📦 Total esperado según Steam: {}", totalInventarioEsperado);
            if (totalItemsObtenidos < totalInventarioEsperado) {
                log.warn("⚠️ Faltan {} items por obtener",
                        totalInventarioEsperado - totalItemsObtenidos);
            }
        }

        return todasLasPaginas;
    }

    private void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("⚠️ Espera interrumpida");
        }
    }
}
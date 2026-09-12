package com.SteamCommerce.steam.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.SteamCommerce.steam.service.InventorySyncService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventoryScheduler {

    private final InventorySyncService inventorySyncService;

    /**
     * Se ejecuta 5 segundos después de iniciar y luego cada 6 horas
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 21600000) // 5s, luego 6h
    public void sincronizarProgramado() {
        inventorySyncService.sincronizarInventario();
    }
}
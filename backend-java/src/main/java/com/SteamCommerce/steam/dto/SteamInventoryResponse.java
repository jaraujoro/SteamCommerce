package com.SteamCommerce.steam.dto;

import lombok.Data;
import java.util.List;

@Data
public class SteamInventoryResponse {
    private boolean success;
    private List<SteamAsset> assets;
    private List<SteamDescription> descriptions;
    private int total_inventory_count;
}
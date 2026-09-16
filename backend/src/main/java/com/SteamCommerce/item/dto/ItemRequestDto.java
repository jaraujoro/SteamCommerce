package com.SteamCommerce.item.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {

    @JsonProperty("asset_id")
    private String assetId;

    @NotNull(message = "El market_hash_name es obligatorio")
    @JsonProperty("market_hash_name")
    private String marketHashName;

    @JsonProperty("icon_url")
    private String iconUrl;

    @JsonProperty("color")
    private String color;

    @JsonProperty("tradable")
    private Boolean tradable;

    @JsonProperty("marketable")
    private Boolean marketable;

    @JsonProperty("market_tradable_restriction")
    private Integer marketTradableRestriction;

    @JsonProperty("trade_cooldown_until")
    private LocalDateTime tradeCooldownUntil;

    @NotNull(message = "El id_tipo_item es obligatorio")
    @JsonProperty("id_tipo_item")
    private Long idTipoItem;

    @NotNull(message = "El id_rareza es obligatorio")
    @JsonProperty("id_rareza")
    private Long idRareza;

    @NotNull(message = "El id_heroe es obligatorio")
    @JsonProperty("id_heroe")
    private Long idHeroe;
}

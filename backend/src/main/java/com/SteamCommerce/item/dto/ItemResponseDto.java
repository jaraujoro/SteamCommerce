package com.SteamCommerce.item.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemResponseDto {
    private String publicId;

    private String marketHashName;

    private String iconUrl;

    private String color;

    private Boolean tradable;

    private Boolean marketable;

    private Integer marketTradableRestriction;

    private LocalDateTime tradeCooldownUntil;

    private String tipoItem;

    private String rarity;

    private String hero;

    private LocalDateTime creadoEn;
}

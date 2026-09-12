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

    private String assetId;

    private String appId;

    private String contextId;

    private String classId;

    private String instanceId;

    private Integer amount;

    private String name;

    private String marketName;

    private String marketHashName;

    private String iconUrl;

    private String color;

    private Boolean tradable;

    private Boolean marketable;

    private Boolean commodity;

    private Integer marketTradableRestriction;

    private LocalDateTime tradeCooldownUntil;

    private Long idTipoItem;

    private Long idRareza;

    private Long idHeroe;

    private LocalDateTime creadoEn;
}

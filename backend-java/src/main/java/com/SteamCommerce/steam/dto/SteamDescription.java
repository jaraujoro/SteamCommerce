package com.SteamCommerce.steam.dto;

import java.util.List;
import lombok.Data;

@Data
public class SteamDescription {
    private String appid;
    private String classid;
    private String instanceid;
    private String name;
    private String market_name;
    private String market_hash_name;
    private String icon_url;
    private String color;
    private Boolean tradable;
    private Boolean marketable;
    private Boolean commodity;
    private Integer market_tradable_restriction;
    private List<SteamTag> tags;
    private List<SteamDescriptionItem> descriptions;
}
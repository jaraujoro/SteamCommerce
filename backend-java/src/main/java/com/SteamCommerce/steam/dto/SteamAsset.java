package com.SteamCommerce.steam.dto;

import lombok.Data;

@Data
public class SteamAsset {
    private String appid;
    private String contextid;
    private String assetid;
    private String classid;
    private String instanceid;
    private Integer amount;
}
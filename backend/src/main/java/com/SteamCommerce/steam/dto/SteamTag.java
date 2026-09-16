package com.SteamCommerce.steam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SteamTag {

    private String category;

    @JsonProperty("internal_name")
    private String internalName;

    @JsonProperty("localized_category_name")
    private String localizedCategoryName;

    private String name;

    @JsonProperty("localized_tag_name")
    private String localizedTagName;

    private String color;

    public String getNombre() {
        if (name != null && !name.isBlank()) {
            return name.trim();
        }
        if (localizedTagName != null && !localizedTagName.isBlank()) {
            return localizedTagName.trim();
        }
        return internalName != null ? internalName.trim() : null;
    }
}
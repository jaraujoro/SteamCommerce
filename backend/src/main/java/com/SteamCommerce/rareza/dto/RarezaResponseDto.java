package com.SteamCommerce.rareza.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RarezaResponseDto {

    @JsonProperty("id_rareza")
    private Long idRareza;

    private String nombre;

    private String color;

    @JsonProperty("nombre_interno")
    private String nombreInterno;
}

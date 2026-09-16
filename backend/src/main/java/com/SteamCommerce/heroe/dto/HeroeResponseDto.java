package com.SteamCommerce.heroe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeroeResponseDto {

    @JsonProperty("id_heroe")
    private Long idHeroe;

    private String nombre;

    @JsonProperty("nombre_interno")
    private String nombreInterno;
}

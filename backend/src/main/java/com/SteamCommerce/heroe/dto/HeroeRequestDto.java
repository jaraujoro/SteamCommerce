package com.SteamCommerce.heroe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeroeRequestDto {

    @NotBlank(message = "El nombre del héroe es obligatorio")
    private String nombre;

    @JsonProperty("nombre_interno")
    private String nombreInterno;
}

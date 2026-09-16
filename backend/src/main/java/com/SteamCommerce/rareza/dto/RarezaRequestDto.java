package com.SteamCommerce.rareza.dto;

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
public class RarezaRequestDto {

    @NotBlank(message = "El nombre de la rareza es obligatorio")
    private String nombre;

    private String color;

    @JsonProperty("nombre_interno")
    private String nombreInterno;
}

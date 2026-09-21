package com.SteamCommerce.recarga.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecargaEstadoDto {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}
package com.SteamCommerce.tipo_pago.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipoPagoRequestDto {

    @NotNull(message = "El nombre es obligatorio")
    private String nombre;
    @NotNull(message = "El estado es obligatorio")
    private Boolean activo;

}

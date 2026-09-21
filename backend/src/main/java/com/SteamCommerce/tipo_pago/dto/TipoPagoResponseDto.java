package com.SteamCommerce.tipo_pago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipoPagoResponseDto {

    private String nombre;
    private Boolean activo;
}

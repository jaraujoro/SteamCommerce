package com.SteamCommerce.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ActualizarTradeUrlDto {
    @NotBlank(message = "El trade URL es obligatorio")
    String steamTradeUrl;
}

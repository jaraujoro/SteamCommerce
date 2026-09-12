package com.SteamCommerce.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioRequestDto {

    @NotNull(message = "El steam_id es obligatorio")
    @JsonProperty("steam_id")
    private String steamId;

    @NotNull(message = "El nombre es obligatorio")
    private String nombre;

    private String avatar;

    @JsonProperty("trade_url")
    private String tradeUrl;

    @JsonProperty("saldo_soles")
    private BigDecimal saldoSoles;

    private String role;
}
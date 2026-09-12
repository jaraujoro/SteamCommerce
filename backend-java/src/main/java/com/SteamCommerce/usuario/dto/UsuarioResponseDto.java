package com.SteamCommerce.usuario.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioResponseDto {

    @JsonProperty("id_usuario")
    private UUID idUsuario;

    @JsonProperty("steam_id")
    private String steamId;

    private String nombre;
    private String avatar;

    @JsonProperty("trade_url")
    private String tradeUrl;

    @JsonProperty("saldo_soles")
    private BigDecimal saldoSoles;

    private String role;
}
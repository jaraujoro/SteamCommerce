package com.SteamCommerce.instruccion_pago.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstruccionPagoRequestDto {

    @NotNull(message = "El id_tipo_pago es obligatorio")
    @JsonProperty("id_tipo_pago")
    private Long idTipoPago;

    private String titular;

    @JsonProperty("numero_pago")
    private String numeroPago;

    @JsonProperty("cuenta_bancaria")
    private String cuentaBancaria;

    private String cci;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("monto_minimo")
    private BigDecimal montoMinimo;

    @JsonProperty("monto_maximo")
    private BigDecimal montoMaximo;

    @JsonProperty("instrucciones_texto")
    private String instruccionesTexto;

    private Boolean activo;
}

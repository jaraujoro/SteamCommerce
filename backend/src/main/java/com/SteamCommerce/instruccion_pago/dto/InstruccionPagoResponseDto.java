package com.SteamCommerce.instruccion_pago.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstruccionPagoResponseDto {

    @JsonProperty("id_instruccion")
    private Long idInstruccion;

    @JsonProperty("id_tipo_pago")
    private Long idTipoPago;

    @JsonProperty("tipo_pago_nombre")
    private String tipoPagoNombre;

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

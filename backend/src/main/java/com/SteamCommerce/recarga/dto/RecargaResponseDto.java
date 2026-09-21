package com.SteamCommerce.recarga.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecargaResponseDto {

    @JsonProperty("id_recarga")
    private Long idRecarga;

    @JsonProperty("id_usuario")
    private UUID idUsuario;

    @JsonProperty("usuario_nombre")
    private String usuarioNombre;

    @JsonProperty("id_tipo_pago")
    private Long idTipoPago;

    @JsonProperty("tipo_pago_nombre")
    private String tipoPagoNombre;

    private BigDecimal monto;

    private String moneda;

    @JsonProperty("codigo_recarga")
    private String codigoRecarga;

    @JsonProperty("mensaje_pago")
    private String mensajePago;

    private String estado;

    @JsonProperty("comprobante_url")
    private String comprobanteUrl;

    @JsonProperty("fecha_creacion")
    private LocalDateTime fechaCreacion;

    @JsonProperty("fecha_verificacion")
    private LocalDateTime fechaVerificacion;
}
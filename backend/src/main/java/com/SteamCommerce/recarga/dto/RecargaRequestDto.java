package com.SteamCommerce.recarga.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class RecargaRequestDto {

    @JsonProperty("id_usuario")
    private UUID idUsuario;

    @NotNull(message = "El id_tipo_pago es obligatorio")
    @JsonProperty("id_tipo_pago")
    private Long idTipoPago;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    private String moneda;
}
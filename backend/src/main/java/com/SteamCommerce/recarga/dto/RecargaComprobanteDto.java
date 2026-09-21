package com.SteamCommerce.recarga.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecargaComprobanteDto {

    @NotBlank(message = "La URL del comprobante es obligatoria")
    @JsonProperty("comprobante_url")
    private String comprobanteUrl;
}
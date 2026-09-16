package com.SteamCommerce.tipoitem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipoItemResponseDto {

    @JsonProperty("id_tipo_item")
    private Long idTipoItem;

    private String nombre;

    @JsonProperty("nombre_interno")
    private String nombreInterno;
}

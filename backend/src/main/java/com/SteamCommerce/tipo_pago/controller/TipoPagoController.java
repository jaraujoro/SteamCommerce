package com.SteamCommerce.tipo_pago.controller;

import com.SteamCommerce.common.ApiResponse;
import com.SteamCommerce.tipo_pago.dto.TipoPagoRequestDto;
import com.SteamCommerce.tipo_pago.dto.TipoPagoResponseDto;
import com.SteamCommerce.tipo_pago.service.TipoPagoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tipo-pago")
@RequiredArgsConstructor
public class TipoPagoController {

    private final TipoPagoService tipoPagoService;

    @PostMapping
    public ApiResponse<Void> crearTipoPago(@Valid @RequestBody TipoPagoRequestDto dto) {
        tipoPagoService.crearTipoPago(dto);
        return ApiResponse.success("Tipo de pago creado exitosamente", HttpStatus.CREATED.value());
    }

    @PutMapping("/{idTipoPago}")
    public ApiResponse<Void> actualizarTipoPago(@PathVariable Long idTipoPago,
            @Valid @RequestBody TipoPagoRequestDto dto) {
        tipoPagoService.actualizarTipoPago(dto, idTipoPago);
        return ApiResponse.success("Tipo de pago actualizado correctamente", HttpStatus.OK.value());
    }

    @DeleteMapping("/{idTipoPago}")
    public ApiResponse<Void> eliminarTipoPago(@PathVariable Long idTipoPago) {
        tipoPagoService.eliminarTipoPago(idTipoPago);
        return ApiResponse.success("Tipo de pago eliminado correctamente", HttpStatus.OK.value());
    }

    @GetMapping
    public ApiResponse<List<TipoPagoResponseDto>> listarTiposPago() {
        return ApiResponse.success("Tipos de pago obtenidos correctamente", HttpStatus.OK.value(),
                tipoPagoService.listarTiposPago());
    }

}

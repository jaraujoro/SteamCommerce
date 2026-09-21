package com.SteamCommerce.instruccion_pago.controller;

import com.SteamCommerce.common.ApiResponse;
import com.SteamCommerce.instruccion_pago.dto.InstruccionPagoRequestDto;
import com.SteamCommerce.instruccion_pago.dto.InstruccionPagoResponseDto;
import com.SteamCommerce.instruccion_pago.service.InstruccionPagoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/instruccion-pago")
@RequiredArgsConstructor
public class InstruccionPagoController {

    private final InstruccionPagoService instruccionPagoService;

    @PostMapping
    public ApiResponse<InstruccionPagoResponseDto> crearInstruccion(@Valid @RequestBody InstruccionPagoRequestDto dto) {
        InstruccionPagoResponseDto response = instruccionPagoService.crearInstruccion(dto);
        return ApiResponse.success("Instrucción de pago creada exitosamente", HttpStatus.CREATED.value(), response);
    }

    @PutMapping("/{idInstruccion}")
    public ApiResponse<InstruccionPagoResponseDto> actualizarInstruccion(
            @PathVariable Long idInstruccion,
            @Valid @RequestBody InstruccionPagoRequestDto dto) {
        InstruccionPagoResponseDto response = instruccionPagoService.actualizarInstruccion(dto, idInstruccion);
        return ApiResponse.success("Instrucción de pago actualizada correctamente", HttpStatus.OK.value(), response);
    }

    @DeleteMapping("/{idInstruccion}")
    public ApiResponse<Void> eliminarInstruccion(@PathVariable Long idInstruccion) {
        instruccionPagoService.eliminarInstruccion(idInstruccion);
        return ApiResponse.success("Instrucción de pago eliminada correctamente", HttpStatus.OK.value());
    }

    @GetMapping
    public ApiResponse<List<InstruccionPagoResponseDto>> listarInstrucciones() {
        return ApiResponse.success("Instrucciones de pago obtenidas correctamente", HttpStatus.OK.value(),
                instruccionPagoService.listarInstrucciones());
    }

    @GetMapping("/{idInstruccion}")
    public ApiResponse<InstruccionPagoResponseDto> obtenerInstruccionPorId(@PathVariable Long idInstruccion) {
        return ApiResponse.success("Instrucción de pago obtenida correctamente", HttpStatus.OK.value(),
                instruccionPagoService.obtenerInstruccionPorId(idInstruccion));
    }

    @GetMapping("/tipo-pago/{idTipoPago}")
    public ApiResponse<List<InstruccionPagoResponseDto>> listarPorTipoPago(@PathVariable Long idTipoPago) {
        return ApiResponse.success("Instrucciones de pago por tipo obtenidas correctamente", HttpStatus.OK.value(),
                instruccionPagoService.listarInstruccionesPorTipoPago(idTipoPago));
    }
}

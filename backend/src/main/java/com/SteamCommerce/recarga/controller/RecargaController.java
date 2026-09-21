package com.SteamCommerce.recarga.controller;

import com.SteamCommerce.common.ApiResponse;
import com.SteamCommerce.recarga.dto.RecargaComprobanteDto;
import com.SteamCommerce.recarga.dto.RecargaEstadoDto;
import com.SteamCommerce.recarga.dto.RecargaRequestDto;
import com.SteamCommerce.recarga.dto.RecargaResponseDto;
import com.SteamCommerce.recarga.service.RecargaService;
import com.SteamCommerce.usuario.entity.UsuarioEntity;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recarga")
@RequiredArgsConstructor
public class RecargaController {

    private final RecargaService recargaService;

    @PostMapping
    public ApiResponse<RecargaResponseDto> crearRecarga(
            @Valid @RequestBody RecargaRequestDto dto,
            @AuthenticationPrincipal UsuarioEntity usuarioAutenticado) {
        RecargaResponseDto response = recargaService.crearRecarga(dto, usuarioAutenticado.getIdUsuario());
        return ApiResponse.success("Recarga creada exitosamente", HttpStatus.CREATED.value(), response);
    }

    @GetMapping
    public ApiResponse<List<RecargaResponseDto>> listarRecargas(
            @RequestParam(required = false) String estado) {
        return ApiResponse.success("Recargas listadas correctamente", HttpStatus.OK.value(),
                recargaService.listarRecargas(estado));
    }

    @GetMapping("/mis-recargas")
    public ApiResponse<List<RecargaResponseDto>> listarMisRecargas(
            @AuthenticationPrincipal UsuarioEntity usuarioAutenticado) {
        if (usuarioAutenticado == null) {
            return ApiResponse.error("Usuario no autenticado", HttpStatus.UNAUTHORIZED.value());
        }
        return ApiResponse.success("Mis recargas listadas correctamente", HttpStatus.OK.value(),
                recargaService.listarMisRecargas(usuarioAutenticado.getIdUsuario()));
    }

    @GetMapping("/{idRecarga}")
    public ApiResponse<RecargaResponseDto> obtenerRecargaPorId(@PathVariable Long idRecarga) {
        return ApiResponse.success("Recarga obtenida correctamente", HttpStatus.OK.value(),
                recargaService.obtenerRecargaPorId(idRecarga));
    }

    @GetMapping("/codigo/{codigoRecarga}")
    public ApiResponse<RecargaResponseDto> obtenerRecargaPorCodigo(@PathVariable String codigoRecarga) {
        return ApiResponse.success("Recarga obtenida por código correctamente", HttpStatus.OK.value(),
                recargaService.obtenerRecargaPorCodigo(codigoRecarga));
    }

    @PatchMapping("/{idRecarga}/estado")
    public ApiResponse<RecargaResponseDto> actualizarEstado(
            @PathVariable Long idRecarga,
            @Valid @RequestBody RecargaEstadoDto dto) {
        RecargaResponseDto response = recargaService.actualizarEstado(idRecarga, dto);
        return ApiResponse.success("Estado de recarga actualizado correctamente", HttpStatus.OK.value(), response);
    }

    @PatchMapping("/{idRecarga}/comprobante")
    public ApiResponse<RecargaResponseDto> subirComprobante(
            @PathVariable Long idRecarga,
            @Valid @RequestBody RecargaComprobanteDto dto) {
        RecargaResponseDto response = recargaService.subirComprobante(idRecarga, dto);
        return ApiResponse.success("Comprobante registrado correctamente", HttpStatus.OK.value(), response);
    }

    @DeleteMapping("/{idRecarga}")
    public ApiResponse<Void> eliminarRecarga(@PathVariable Long idRecarga) {
        recargaService.eliminarRecarga(idRecarga);
        return ApiResponse.success("Recarga eliminada correctamente", HttpStatus.OK.value());
    }
}

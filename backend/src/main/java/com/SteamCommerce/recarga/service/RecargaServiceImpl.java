package com.SteamCommerce.recarga.service;

import com.SteamCommerce.config.exception.BadRequestException;
import com.SteamCommerce.config.exception.ResourceNotFoundException;
import com.SteamCommerce.recarga.dto.RecargaComprobanteDto;
import com.SteamCommerce.recarga.dto.RecargaEstadoDto;
import com.SteamCommerce.recarga.dto.RecargaRequestDto;
import com.SteamCommerce.recarga.dto.RecargaResponseDto;
import com.SteamCommerce.recarga.entity.RecargaEntity;
import com.SteamCommerce.recarga.mapper.RecargaMapper;
import com.SteamCommerce.recarga.repository.RecargaRepository;
import com.SteamCommerce.tipo_pago.entity.TipoPagoEntity;
import com.SteamCommerce.tipo_pago.repository.TipoPagoRepository;
import com.SteamCommerce.usuario.entity.UsuarioEntity;
import com.SteamCommerce.usuario.repository.UsuarioRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecargaServiceImpl implements RecargaService {

    // Estados válidos del ciclo de vida de una recarga
    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "PENDIENTE",
            "EN_REVISION",
            "VERIFICADO",
            "RECHAZADO",
            "CANCELADO",
            "REEMBOLSADO");

    // Generador de códigos
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_INTENTOS_CODIGO = 50;
    private static final int MAX_REINTENTOS_SAVE = 5;
    private static final int RANGO_CODIGO = 10_000; // SC-0000 a SC-9999

    private final RecargaRepository recargaRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecargaMapper recargaMapper;

    // ─────────────────────────────────────────────────────────────
    // CREAR RECARGA
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public RecargaResponseDto crearRecarga(RecargaRequestDto dto, UUID idUsuario) {
        if (dto == null) {
            throw new BadRequestException("Los datos de la recarga no pueden ser nulos");
        }
        if (idUsuario == null) {
            throw new BadRequestException("El usuario es obligatorio para registrar la recarga");
        }

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con id: " + idUsuario));

        TipoPagoEntity tipoPago = tipoPagoRepository.findById(dto.getIdTipoPago())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de pago no encontrado con id: " + dto.getIdTipoPago()));

        if (Boolean.FALSE.equals(tipoPago.getActivo())) {
            throw new BadRequestException("El tipo de pago seleccionado no se encuentra activo");
        }

        // BUSCAR si ya existe una recarga PENDIENTE con el MISMO monto y tipo de
        // pago
        Optional<RecargaEntity> existente = recargaRepository
                .findFirstByIdUsuarioAndEstadoAndIdTipoPagoAndMontoOrderByFechaCreacionDesc(
                        idUsuario,
                        "PENDIENTE",
                        dto.getIdTipoPago(),
                        dto.getMonto());

        if (existente.isPresent()) {
            // Si existe, devolverla con el MISMO código (no se genera uno nuevo)
            RecargaEntity recarga = existente.get();
            recarga.setUsuario(usuario);
            recarga.setTipoPago(tipoPago);
            return recargaMapper.toDto(recarga);
        }

        // Si NO existe, crear una nueva con código nuevo
        for (int intento = 0; intento < MAX_REINTENTOS_SAVE; intento++) {
            String codigoRecarga = generarCodigoRecargaUnico();

            RecargaEntity entity = recargaMapper.toEntity(dto, idUsuario, codigoRecarga);
            entity.setUsuario(usuario);
            entity.setTipoPago(tipoPago);

            try {
                RecargaEntity guardada = recargaRepository.saveAndFlush(entity);
                return recargaMapper.toDto(guardada);
            } catch (DataIntegrityViolationException e) {
                // Colisión de código único → reintentar con otro
            }
        }

        throw new BadRequestException("No se pudo generar un código único para la recarga");
    }

    // ─────────────────────────────────────────────────────────────
    // ACTUALIZAR ESTADO (admin)
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public RecargaResponseDto actualizarEstado(Long idRecarga, RecargaEstadoDto dto) {
        if (idRecarga == null || dto == null || dto.getEstado() == null || dto.getEstado().isBlank()) {
            throw new BadRequestException("El id de recarga y el nuevo estado son obligatorios");
        }

        String nuevoEstado = dto.getEstado().trim().toUpperCase();
        if (!ESTADOS_VALIDOS.contains(nuevoEstado)) {
            throw new BadRequestException("Estado no válido. Valores permitidos: " + ESTADOS_VALIDOS);
        }

        RecargaEntity entity = recargaRepository.findWithRelacionesByIdRecarga(idRecarga)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recarga no encontrada con id: " + idRecarga));

        entity.setEstado(nuevoEstado);
        if ("VERIFICADO".equals(nuevoEstado) || "RECHAZADO".equals(nuevoEstado)) {
            entity.setFechaVerificacion(LocalDateTime.now());
        }

        RecargaEntity actualizada = recargaRepository.save(entity);
        return recargaMapper.toDto(actualizada);
    }

    // ─────────────────────────────────────────────────────────────
    // SUBIR COMPROBANTE (usuario)
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public RecargaResponseDto subirComprobante(Long idRecarga, RecargaComprobanteDto dto) {
        if (idRecarga == null || dto == null
                || dto.getComprobanteUrl() == null
                || dto.getComprobanteUrl().isBlank()) {
            throw new BadRequestException("El comprobante_url es obligatorio");
        }

        RecargaEntity entity = recargaRepository.findWithRelacionesByIdRecarga(idRecarga)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recarga no encontrada con id: " + idRecarga));

        entity.setComprobanteUrl(dto.getComprobanteUrl().trim());
        entity.setEstado("EN_REVISION");
        entity.setMensajePago("Comprobante subido, pendiente de verificación");

        RecargaEntity actualizada = recargaRepository.save(entity);
        return recargaMapper.toDto(actualizada);
    }

    // ─────────────────────────────────────────────────────────────
    // OBTENER POR ID
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public RecargaResponseDto obtenerRecargaPorId(Long idRecarga) {
        RecargaEntity entity = recargaRepository.findWithRelacionesByIdRecarga(idRecarga)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recarga no encontrada con id: " + idRecarga));
        return recargaMapper.toDto(entity);
    }

    // ─────────────────────────────────────────────────────────────
    // OBTENER POR CÓDIGO
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public RecargaResponseDto obtenerRecargaPorCodigo(String codigoRecarga) {
        if (codigoRecarga == null || codigoRecarga.isBlank()) {
            throw new BadRequestException("El código de recarga es obligatorio");
        }
        RecargaEntity entity = recargaRepository.findByCodigoRecarga(codigoRecarga.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recarga no encontrada con código: " + codigoRecarga));
        return recargaMapper.toDto(entity);
    }

    // ─────────────────────────────────────────────────────────────
    // LISTAR RECARGAS (admin)
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<RecargaResponseDto> listarRecargas(String estado) {
        List<RecargaEntity> recargas;
        if (estado != null && !estado.isBlank()) {
            recargas = recargaRepository.findByEstadoIgnoreCaseOrderByFechaCreacionDesc(estado.trim());
        } else {
            recargas = recargaRepository.findAllByOrderByFechaCreacionDesc();
        }
        return recargas.stream().map(recargaMapper::toDto).toList();
    }

    // ─────────────────────────────────────────────────────────────
    // LISTAR MIS RECARGAS (usuario autenticado)
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<RecargaResponseDto> listarMisRecargas(UUID idUsuario) {
        if (idUsuario == null) {
            throw new BadRequestException("El id del usuario es obligatorio");
        }
        return recargaRepository.findByIdUsuarioOrderByFechaCreacionDesc(idUsuario).stream()
                .map(recargaMapper::toDto)
                .toList();
    }

    // ─────────────────────────────────────────────────────────────
    // ELIMINAR
    // ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void eliminarRecarga(Long idRecarga) {
        RecargaEntity entity = recargaRepository.findById(idRecarga)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Recarga no encontrada con id: " + idRecarga));
        recargaRepository.delete(entity);
    }

    // ─────────────────────────────────────────────────────────────
    // GENERADOR DE CÓDIGO ÚNICO
    // ─────────────────────────────────────────────────────────────
    private String generarCodigoRecargaUnico() {
        for (int i = 0; i < MAX_INTENTOS_CODIGO; i++) {
            int numero = RANDOM.nextInt(RANGO_CODIGO);
            String codigo = String.format("SC-%04d", numero);
            if (!recargaRepository.existsByCodigoRecarga(codigo)) {
                return codigo;
            }
        }
        throw new BadRequestException(
                "No se pudo generar un código único. Es posible que el límite de recargas se haya alcanzado.");
    }
}
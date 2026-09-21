package com.SteamCommerce.instruccion_pago.service;

import com.SteamCommerce.config.exception.BadRequestException;
import com.SteamCommerce.config.exception.ResourceNotFoundException;
import com.SteamCommerce.instruccion_pago.dto.InstruccionPagoRequestDto;
import com.SteamCommerce.instruccion_pago.dto.InstruccionPagoResponseDto;
import com.SteamCommerce.instruccion_pago.entity.InstruccionPagoEntity;
import com.SteamCommerce.instruccion_pago.mapper.InstruccionPagoMapper;
import com.SteamCommerce.instruccion_pago.repository.InstruccionPagoRepository;
import com.SteamCommerce.tipo_pago.entity.TipoPagoEntity;
import com.SteamCommerce.tipo_pago.repository.TipoPagoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstruccionPagoServiceImpl implements InstruccionPagoService {

    private final InstruccionPagoRepository instruccionPagoRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final InstruccionPagoMapper instruccionPagoMapper;

    @Override
    @Transactional
    public InstruccionPagoResponseDto crearInstruccion(InstruccionPagoRequestDto dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos de la instrucción no pueden ser nulos");
        }

        TipoPagoEntity tipoPago = tipoPagoRepository.findById(dto.getIdTipoPago())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de pago no encontrado con id: " + dto.getIdTipoPago()));

        validarMontos(dto);

        InstruccionPagoEntity entity = instruccionPagoMapper.toEntity(dto);
        InstruccionPagoEntity guardada = instruccionPagoRepository.save(entity);
        guardada.setTipoPago(tipoPago);

        return instruccionPagoMapper.toDto(guardada);
    }

    @Override
    @Transactional
    public InstruccionPagoResponseDto actualizarInstruccion(InstruccionPagoRequestDto dto, Long idInstruccion) {
        if (dto == null || idInstruccion == null) {
            throw new BadRequestException("Los datos de la petición no pueden ser nulos");
        }

        InstruccionPagoEntity entity = instruccionPagoRepository.findById(idInstruccion)
                .orElseThrow(() -> new ResourceNotFoundException("Instrucción de pago no encontrada con id: " + idInstruccion));

        TipoPagoEntity tipoPago = null;
        if (dto.getIdTipoPago() != null) {
            tipoPago = tipoPagoRepository.findById(dto.getIdTipoPago())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de pago no encontrado con id: " + dto.getIdTipoPago()));
            entity.setIdTipoPago(dto.getIdTipoPago());
        }

        validarMontos(dto);

        entity.setTitular(dto.getTitular());
        entity.setNumeroPago(dto.getNumeroPago());
        entity.setCuentaBancaria(dto.getCuentaBancaria());
        entity.setCci(dto.getCci());
        entity.setLogoUrl(dto.getLogoUrl());
        entity.setMontoMinimo(dto.getMontoMinimo());
        entity.setMontoMaximo(dto.getMontoMaximo());
        entity.setInstruccionesTexto(dto.getInstruccionesTexto());
        if (dto.getActivo() != null) {
            entity.setActivo(dto.getActivo());
        }

        InstruccionPagoEntity actualizada = instruccionPagoRepository.save(entity);
        if (tipoPago != null) {
            actualizada.setTipoPago(tipoPago);
        }

        return instruccionPagoMapper.toDto(actualizada);
    }

    @Override
    @Transactional
    public void eliminarInstruccion(Long idInstruccion) {
        InstruccionPagoEntity entity = instruccionPagoRepository.findById(idInstruccion)
                .orElseThrow(() -> new ResourceNotFoundException("Instrucción de pago no encontrada con id: " + idInstruccion));
        instruccionPagoRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public InstruccionPagoResponseDto obtenerInstruccionPorId(Long idInstruccion) {
        InstruccionPagoEntity entity = instruccionPagoRepository.findById(idInstruccion)
                .orElseThrow(() -> new ResourceNotFoundException("Instrucción de pago no encontrada con id: " + idInstruccion));
        return instruccionPagoMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstruccionPagoResponseDto> listarInstrucciones() {
        return instruccionPagoRepository.findAll().stream()
                .map(instruccionPagoMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstruccionPagoResponseDto> listarInstruccionesPorTipoPago(Long idTipoPago) {
        if (!tipoPagoRepository.existsById(idTipoPago)) {
            throw new ResourceNotFoundException("Tipo de pago no encontrado con id: " + idTipoPago);
        }
        return instruccionPagoRepository.findByIdTipoPagoAndActivoTrue(idTipoPago).stream()
                .map(instruccionPagoMapper::toDto)
                .toList();
    }

    private void validarMontos(InstruccionPagoRequestDto dto) {
        if (dto.getMontoMinimo() != null && dto.getMontoMaximo() != null) {
            if (dto.getMontoMinimo().compareTo(dto.getMontoMaximo()) > 0) {
                throw new BadRequestException("El monto mínimo no puede ser mayor que el monto máximo");
            }
        }
    }
}

package com.SteamCommerce.tipo_pago.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.SteamCommerce.config.exception.BadRequestException;
import com.SteamCommerce.tipo_pago.dto.TipoPagoRequestDto;
import com.SteamCommerce.tipo_pago.dto.TipoPagoResponseDto;
import com.SteamCommerce.tipo_pago.entity.TipoPagoEntity;
import com.SteamCommerce.tipo_pago.mapper.TipoPagoMapper;
import com.SteamCommerce.tipo_pago.repository.TipoPagoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoPagoServiceImpl implements TipoPagoService {

    private final TipoPagoRepository tipoPagoRepository;
    private final TipoPagoMapper tipoPagoMapper;

    @Override
    public void crearTipoPago(TipoPagoRequestDto dto) {
        if (dto == null || dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del tipo de pago es obligatorio");
        }

        if (tipoPagoRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new BadRequestException("El tipo de pago ya existe");
        }

        if (dto.getActivo() == null) {
            dto.setActivo(true);
        }

        TipoPagoEntity entity = tipoPagoMapper.toEntity(dto);

        tipoPagoRepository.save(entity);
    }

    @Override
    public void actualizarTipoPago(TipoPagoRequestDto dto, Long idTipoPago) {
        if (dto == null || idTipoPago == null) {
            throw new BadRequestException("El objeto no puede ser nulo");
        }

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del tipo de pago es obligatorio");
        }

        TipoPagoEntity entity = tipoPagoRepository.findById(idTipoPago)
                .orElseThrow(() -> new BadRequestException("Tipo de pago no encontrado"));

        if (tipoPagoRepository.existsByNombreIgnoreCaseAndIdTipoPagoNot(
                dto.getNombre(), idTipoPago)) {

            throw new BadRequestException("El tipo de pago ya existe");
        }

        if (dto.getActivo() == null) {
            dto.setActivo(true);
        }

        entity.setNombre(dto.getNombre());
        entity.setActivo(dto.getActivo());
        tipoPagoRepository.save(entity);
    }

    @Override
    public void eliminarTipoPago(Long idTipoPago) {
        TipoPagoEntity entity = tipoPagoRepository.findById(idTipoPago)
                .orElseThrow(() -> new BadRequestException("Tipo de pago no encontrado"));
        tipoPagoRepository.delete(entity);
    }

    @Override
    public List<TipoPagoResponseDto> listarTiposPago() {
        return tipoPagoRepository.findAll().stream().map(tipoPagoMapper::toDto).toList();
    }

}

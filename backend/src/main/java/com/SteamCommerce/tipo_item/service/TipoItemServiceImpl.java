package com.SteamCommerce.tipo_item.service;

import com.SteamCommerce.config.exception.BadRequestException;
import com.SteamCommerce.tipo_item.dto.TipoItemRequestDto;
import com.SteamCommerce.tipo_item.dto.TipoItemResponseDto;
import com.SteamCommerce.tipo_item.entity.TipoItemEntity;
import com.SteamCommerce.tipo_item.mapper.TipoItemMapper;
import com.SteamCommerce.tipo_item.repository.TipoItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TipoItemServiceImpl implements TipoItemService {

    private final TipoItemRepository tipoItemRepository;
    private final TipoItemMapper tipoItemMapper;
    private final Map<String, Long> cacheTipos = new ConcurrentHashMap<>();

    @Override
    @Transactional(readOnly = true)
    public List<TipoItemResponseDto> listarTipos() {
        return tipoItemRepository.findAll()
                .stream()
                .map(tipoItemMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void crearTipo(TipoItemRequestDto dto) {
        if (dto == null || dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del tipo de item es obligatorio");
        }

        String nombreNormalizado = dto.getNombre().trim();
        if (tipoItemRepository.existsByNombre(nombreNormalizado)) {
            throw new BadRequestException("El tipo de item '" + nombreNormalizado + "' ya existe");
        }

        tipoItemRepository.save(tipoItemMapper.toEntity(dto));
    }

    @Override
    public void precargarCache() {
        tipoItemRepository.findAll().forEach(t -> cacheTipos.put(t.getNombre().toLowerCase(), t.getIdTipoItem()));
        log.info("Caché de tipos precargada: {} entradas", cacheTipos.size());
    }

    @Override
    @Transactional
    public Long obtenerOCrear(String nombre, String nombreInterno) {
        String nombreFinal = (nombre != null && !nombre.isBlank()) ? nombre.trim() : "Otro";
        String claveCache = nombreFinal.toLowerCase();

        // 1) Caché
        Long idEnCache = cacheTipos.get(claveCache);
        if (idEnCache != null) {
            return idEnCache;
        }

        // 2) Base de datos
        Optional<TipoItemEntity> existente = tipoItemRepository.findByNombreIgnoreCase(nombreFinal);
        if (existente.isPresent()) {
            TipoItemEntity t = existente.get();
            cacheTipos.put(claveCache, t.getIdTipoItem());
            return t.getIdTipoItem();
        }

        // 3) Crear
        TipoItemEntity nuevo = TipoItemEntity.builder()
                .nombre(nombreFinal)
                .nombreInterno(nombreInterno != null ? nombreInterno.trim() : null)
                .build();

        TipoItemEntity guardado = tipoItemRepository.save(nuevo);
        cacheTipos.put(claveCache, guardado.getIdTipoItem());

        log.info("Nuevo tipo de item registrado: {} (ID: {})", guardado.getNombre(), guardado.getIdTipoItem());
        return guardado.getIdTipoItem();
    }
}

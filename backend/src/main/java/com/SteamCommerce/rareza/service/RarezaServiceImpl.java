package com.SteamCommerce.rareza.service;

import com.SteamCommerce.config.exception.BadRequestException;
import com.SteamCommerce.rareza.dto.RarezaRequestDto;
import com.SteamCommerce.rareza.dto.RarezaResponseDto;
import com.SteamCommerce.rareza.entity.RarezaEntity;
import com.SteamCommerce.rareza.mapper.RarezaMapper;
import com.SteamCommerce.rareza.repository.RarezaRepository;
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
public class RarezaServiceImpl implements RarezaService {

    private final RarezaRepository rarezaRepository;
    private final RarezaMapper rarezaMapper;
    private final Map<String, Long> cacheRarezas = new ConcurrentHashMap<>();

    @Override
    @Transactional(readOnly = true)
    public List<RarezaResponseDto> listarRarezas() {
        return rarezaRepository.findAll().stream().map(rarezaMapper::toResponseDto).toList();
    }

    @Override
    @Transactional
    public void crearRareza(RarezaRequestDto dto) {
        if (dto == null || dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre de la rareza es obligatorio");
        }

        String nombreNormalizado = dto.getNombre().trim();
        if (rarezaRepository.existsByNombre(nombreNormalizado)) {
            throw new BadRequestException("La rareza '" + nombreNormalizado + "' ya existe");
        }

        rarezaRepository.save(rarezaMapper.toEntity(dto));
    }

    @Override
    public void precargarCache() {
        rarezaRepository.findAll().forEach(r -> cacheRarezas.put(r.getNombre().toLowerCase(), r.getIdRareza()));
        log.info("Caché de rarezas precargado: {} entradas", cacheRarezas.size());
    }

    @Override
    @Transactional
    public Long obtenerOCrear(String nombre, String nombreInterno, String color) {
        String nombreFinal = (nombre != null && !nombre.isBlank()) ? nombre.trim() : "Común";
        String claveCache = nombreFinal.toLowerCase();

        // 1) Caché
        Long idEnCache = cacheRarezas.get(claveCache);
        if (idEnCache != null) {
            return idEnCache;
        }

        // 2) Base de datos
        Optional<RarezaEntity> existente = rarezaRepository.findByNombreIgnoreCase(nombreFinal);
        if (existente.isPresent()) {
            RarezaEntity r = existente.get();
            cacheRarezas.put(claveCache, r.getIdRareza());
            return r.getIdRareza();
        }

        // 3) Crear
        RarezaEntity nueva = RarezaEntity.builder()
                .nombre(nombreFinal)
                .color(color != null ? color.trim() : null)
                .nombreInterno(nombreInterno != null ? nombreInterno.trim() : null)
                .build();

        RarezaEntity guardada = rarezaRepository.save(nueva);
        cacheRarezas.put(claveCache, guardada.getIdRareza());

        log.info("Nueva rareza registrada: {} (ID: {})", guardada.getNombre(), guardada.getIdRareza());
        return guardada.getIdRareza();
    }

}

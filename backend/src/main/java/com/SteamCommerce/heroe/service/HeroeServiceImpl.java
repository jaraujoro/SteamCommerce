package com.SteamCommerce.heroe.service;

import com.SteamCommerce.config.exception.BadRequestException;
import com.SteamCommerce.heroe.dto.HeroeRequestDto;
import com.SteamCommerce.heroe.dto.HeroeResponseDto;
import com.SteamCommerce.heroe.entity.HeroeEntity;
import com.SteamCommerce.heroe.mapper.HeroeMapper;
import com.SteamCommerce.heroe.repository.HeroeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeroeServiceImpl implements HeroeService {

    private final HeroeRepository heroeRepository;
    private final HeroeMapper heroeMapper;
    private final Map<String, Long> cacheHeroes = new ConcurrentHashMap<>();

    @Override
    @Transactional(readOnly = true)
    public List<HeroeResponseDto> listarHeroes() {
        return heroeRepository.findAll().stream().map(heroeMapper::toResponseDto).toList();
    }

    @Override
    @Transactional
    public void crearHeroe(HeroeRequestDto dto) {
        if (dto == null || dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del héroe es obligatorio");
        }

        String nombreNormalizado = dto.getNombre().trim();
        if (heroeRepository.existsByNombre(nombreNormalizado)) {
            throw new BadRequestException("El héroe '" + nombreNormalizado + "' ya existe");
        }

        heroeRepository.save(heroeMapper.toEntity(dto));
    }

    @Override
    public void precargarCache() {
        heroeRepository.findAll().forEach(h -> cacheHeroes.put(h.getNombre().toLowerCase(), h.getIdHeroe()));
        log.info("Caché de héroes precargado: {} entradas", cacheHeroes.size());
    }

    @Override
    @Transactional
    public Long obtenerOCrear(String nombre, String nombreInterno) {
        String nombreFinal = (nombre != null && !nombre.isBlank())
                ? nombre.trim()
                : "Sin héroe";
        String claveCache = nombreFinal.toLowerCase();

        // 1) Caché
        Long idEnCache = cacheHeroes.get(claveCache);
        if (idEnCache != null) {
            return idEnCache;
        }

        // 2) BD
        Optional<HeroeEntity> existente = heroeRepository.findByNombreIgnoreCase(nombreFinal);
        if (existente.isPresent()) {
            HeroeEntity h = existente.get();
            cacheHeroes.put(claveCache, h.getIdHeroe());
            return h.getIdHeroe();
        }

        // 3) Crear
        try {
            HeroeEntity guardado = heroeRepository.save(
                    heroeMapper.toEntity(nombreFinal, nombreInterno));
            cacheHeroes.put(claveCache, guardado.getIdHeroe());
            log.info("Nuevo héroe registrado: {} (ID: {})",
                    guardado.getNombre(), guardado.getIdHeroe());
            return guardado.getIdHeroe();

        } catch (DataIntegrityViolationException e) {
            // Otro hilo lo insertó primero → lo buscamos
            return heroeRepository.findByNombreIgnoreCase(nombreFinal)
                    .map(h -> {
                        cacheHeroes.put(claveCache, h.getIdHeroe());
                        return h.getIdHeroe();
                    })
                    .orElseThrow(() -> new IllegalStateException(
                            "Héroe no encontrado tras concurrencia: " + nombreFinal, e));
        }
    }
}
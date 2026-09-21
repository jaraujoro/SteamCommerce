package com.SteamCommerce.usuario.service;

import com.SteamCommerce.config.exception.BadRequestException;
import com.SteamCommerce.config.exception.ResourceNotFoundException;
import com.SteamCommerce.usuario.dto.ActualizarTradeUrlDto;
import com.SteamCommerce.usuario.dto.UsuarioRequestDto;
import com.SteamCommerce.usuario.entity.UsuarioEntity;
import com.SteamCommerce.usuario.mapper.UsuarioMapper;
import com.SteamCommerce.usuario.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional
    public void crearUsuario(UsuarioRequestDto dto) {
        // Validación: Steam ID es obligatorio
        if (dto.getSteamId() == null || dto.getSteamId().trim().isEmpty()) {
            throw new BadRequestException("El steam_id es obligatorio");
        }

        // Validación: Verificar si el steam_id ya existe
        if (usuarioRepository.findBySteamId(dto.getSteamId()).isPresent()) {
            throw new BadRequestException("El steam_id ya está registrado: " + dto.getSteamId());
        }

        // Validación: Nombre es obligatorio
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre es obligatorio");
        }

        // Validación: Trade URL es obligatorio
        if (dto.getTradeUrl() == null || dto.getTradeUrl().trim().isEmpty()) {
            throw new BadRequestException("El trade_url es obligatorio");
        }

        // Validación: URL debe ser de Steam
        String tradeUrl = dto.getTradeUrl().trim();
        if (!tradeUrl.contains("steamcommunity.com/tradeoffer/new/")) {
            throw new BadRequestException("La URL de intercambio no es válida. Debe ser de Steam Community.");
        }

        // Crear y guardar usuario
        UsuarioEntity usuario = usuarioMapper.toEntity(dto);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void actualizarTradeUrl(UUID idUsuario, ActualizarTradeUrlDto dto) {
        // Validación: Steam ID es obligatorio
        if (idUsuario == null) {
            throw new BadRequestException("El objeto no puede ser nulo");
        }

        // Validación: Trade URL es obligatorio
        if (dto.getSteamTradeUrl() == null || dto.getSteamTradeUrl().trim().isEmpty()) {
            throw new BadRequestException("La url es obligatorio");
        }

        // Validación: URL debe ser de Steam
        String tradeUrlLimpia = dto.getSteamTradeUrl().trim();
        if (!tradeUrlLimpia.contains("steamcommunity.com/tradeoffer/new/")) {
            throw new BadRequestException("La URL de intercambio no es válida. Debe ser de Steam Community.");
        }

        // Buscar usuario por steamId
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + idUsuario));

        // Actualizar solo el tradeUrl
        usuario.setTradeUrl(tradeUrlLimpia);
        usuarioRepository.save(usuario);
    }
}
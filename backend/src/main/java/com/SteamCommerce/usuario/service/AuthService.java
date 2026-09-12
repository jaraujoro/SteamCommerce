package com.SteamCommerce.usuario.service;

import com.SteamCommerce.config.security.JwtService;
import com.SteamCommerce.usuario.dto.AuthResponse;
import com.SteamCommerce.usuario.dto.LoginResponse;
import com.SteamCommerce.usuario.entity.UsuarioEntity;
import com.SteamCommerce.usuario.mapper.AuthMapper;
import com.SteamCommerce.usuario.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;
    private final AuthMapper authMapper;

    @Value("${steam.api-key}")
    private String steamApiKey;

    @Value("${frontend.url}")
    private String frontUrl;

    @Value("${backend.url}")
    private String backUrl;

    public LoginResponse generateSteamLoginUrl() {
        String url = "https://steamcommunity.com/openid/login?" +
                "openid.ns=http://specs.openid.net/auth/2.0&" +
                "openid.mode=checkid_setup&" +
                "openid.return_to=" + backUrl + "/usuario/callback&" +
                "openid.realm=" + backUrl + "&" +
                "openid.identity=http://specs.openid.net/auth/2.0/identifier_select&" +
                "openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select";

        return authMapper.toLoginResponse(url);
    }

    public UsuarioEntity findBySteamId(String steamId) {
        return usuarioRepository.findBySteamId(steamId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public String processSteamLogin(HttpServletRequest request) {
        String claimedId = request.getParameter("openid.claimed_id");
        String cleanSteamId = extractSteamId(claimedId);

        if (claimedId == null || claimedId.isEmpty()) {
            throw new RuntimeException("No se pudo obtener openid.claimed_id");
        }

        Map<String, Object> playerInfo = getSteamPlayerInfo(cleanSteamId);
        String nombre = (String) playerInfo.getOrDefault("personaname", "Usuario Steam");
        String avatar = (String) playerInfo.getOrDefault("avatar", "");

        UsuarioEntity usuario = usuarioRepository.findBySteamId(cleanSteamId)
                .orElseGet(() -> {
                    UsuarioEntity nuevo = new UsuarioEntity();
                    nuevo.setSteamId(cleanSteamId);
                    nuevo.setRole("USER");
                    return nuevo;
                });

        usuario.setNombre(nombre);
        usuario.setAvatar(avatar);
        usuario = usuarioRepository.save(usuario);

        // Generar tokens
        String accessToken = jwtService.generateToken(cleanSteamId);
        String refreshToken = jwtService.generateRefreshToken(cleanSteamId);

        return String.format("%s?access=%s&refresh=%s&steam_id=%s&nombre=%s&avatar=%s",
                frontUrl,
                accessToken,
                refreshToken,
                cleanSteamId,
                nombre,
                avatar);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Token de refresco inválido");
        }

        String steamId = jwtService.extractUsername(refreshToken);
        UsuarioEntity usuario = usuarioRepository.findBySteamId(steamId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String newAccessToken = jwtService.generateToken(steamId);
        return authMapper.toAuthResponse(usuario, newAccessToken, refreshToken);
    }

    private String extractSteamId(String input) {
        if (input == null)
            return null;
        if (input.contains("steamcommunity")) {
            String[] parts = input.split("/");
            return parts[parts.length - 1];
        }
        return input;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getSteamPlayerInfo(String steamId) {
        String url = String.format(
                "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%s",
                steamApiKey, steamId);

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("response")) {
                Map<String, Object> responseData = (Map<String, Object>) response.get("response");
                var players = (java.util.List<Map<String, Object>>) responseData.get("players");
                if (players != null && !players.isEmpty()) {
                    return players.get(0);
                }
            }
            throw new RuntimeException("No se pudo obtener información del jugador");
        } catch (Exception e) {
            throw new RuntimeException("Error al llamar a la API de Steam: " + e.getMessage());
        }
    }
}
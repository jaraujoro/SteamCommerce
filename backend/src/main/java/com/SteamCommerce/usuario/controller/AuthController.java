package com.SteamCommerce.usuario.controller;

import com.SteamCommerce.usuario.dto.AuthResponse;
import com.SteamCommerce.usuario.dto.LoginResponse;
import com.SteamCommerce.usuario.dto.RefreshTokenRequest;
import com.SteamCommerce.usuario.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public LoginResponse login() {
        return authService.generateSteamLoginUrl();
    }

    @GetMapping("/callback")
    public RedirectView steamCallback(HttpServletRequest request) {
        String redirectUrl = authService.processSteamLogin(request);
        return new RedirectView(redirectUrl);
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }
}
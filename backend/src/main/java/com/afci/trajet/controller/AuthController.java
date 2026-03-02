package com.afci.trajet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.afci.trajet.dto.AuthRequest;
import com.afci.trajet.dto.AuthResponse;
import com.afci.trajet.service.AuthService;

/**
 * Contrôleur REST dédié à l'authentification (login, refresh, etc.).
 *
 * Pour l'instant, on expose :
 *   POST /api/auth/login
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin // tu peux préciser les origines ici si besoin
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint de connexion :
     * - Reçoit email / password
     * - Retourne un JWT + infos de l'utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

package com.afci.trajet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur de test pour vérifier que la sécurité / les rôles
 * fonctionnent correctement.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminTestController {

    /**
     * Accessible uniquement si l'utilisateur a le rôle ADMIN
     * (donc l'autorité ROLE_ADMIN dans Spring Security).
     */
    @GetMapping("/ping")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> pingAdmin() {
        return ResponseEntity.ok("Accès ADMIN OK - Trajet Formateur 🚀");
    }
}

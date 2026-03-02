// src/main/java/com/afci/trajet/controller/formateur/MonCompteFormateurController.java
package com.afci.trajet.controller.formateur;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.afci.trajet.dto.formateur.moncompte.ChangePasswordRequest;
import com.afci.trajet.dto.formateur.moncompte.FormateurProfileResponse;
import com.afci.trajet.dto.formateur.moncompte.FormateurProfileUpdateRequest;
import com.afci.trajet.service.formateur.moncompte.FormateurMonCompteService;

/**
 * Controller REST pour le "Mon compte" du FORMATEUR.
 *
 * Routes (MVP) :
 *  - GET  /api/formateur/mon-compte?idUser=...
 *  - PUT  /api/formateur/mon-compte?idUser=...
 *  - PUT  /api/formateur/mon-compte/password?idUser=...
 *
 * ⚠️ Pour l'instant, l'idUser est passé en query param.
 *    Plus tard, on utilisera l'id du JWT (SecurityContext).
 */
@RestController
@RequestMapping("/api/formateur/mon-compte")
@PreAuthorize("hasRole('FORMATEUR')")
public class MonCompteFormateurController {

    private final FormateurMonCompteService monCompteService;

    public MonCompteFormateurController(FormateurMonCompteService monCompteService) {
        this.monCompteService = monCompteService;
    }

    /**
     * Lecture du profil complet formateur.
     */
    @GetMapping
    public ResponseEntity<FormateurProfileResponse> getMonCompte(
            @RequestParam("idUser") Integer idUser
    ) {
        FormateurProfileResponse profil = monCompteService.getProfilByUserId(idUser);
        return ResponseEntity.ok(profil);
    }

    /**
     * Mise à jour du profil (téléphone, adresse, zone, etc.).
     */
    @PutMapping
    public ResponseEntity<FormateurProfileResponse> updateMonCompte(
            @RequestParam("idUser") Integer idUser,
            @RequestBody FormateurProfileUpdateRequest request
    ) {
        FormateurProfileResponse updated = monCompteService.updateProfilByUserId(idUser, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Changement de mot de passe.
     */
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @RequestParam("idUser") Integer idUser,
            @RequestBody ChangePasswordRequest request
    ) {
        monCompteService.changePassword(idUser, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package com.afci.trajet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.afci.trajet.dto.ecole.MonEtablissementResponse;
import com.afci.trajet.dto.ecole.MonEtablissementUpdateRequest;
import com.afci.trajet.dto.ecole.MonMotDePasseUpdateRequest;
import com.afci.trajet.dto.ecole.MonProfilUpdateRequest;
import com.afci.trajet.dto.ecole.ResponsableAccessibiliteDto;
import com.afci.trajet.dto.ecole.ResponsableAccessibiliteUpsertRequest;
import com.afci.trajet.service.EcoleMonCompteService;

/**
 * Contrôleur REST pour l'espace ECOLE "Mon compte / Mon établissement".
 *
 * Toutes les routes sont préfixées par /api/ecole et nécessitent
 * le rôle ROLE_ECOLE.
 *
 * On se base sur l'utilisateur actuellement authentifié (Spring Security),
 * via SecurityContextHolder.
 */
@RestController
@RequestMapping("/api/ecole")
@PreAuthorize("hasRole('ECOLE')")
public class EcoleMonCompteController {

    private final EcoleMonCompteService ecoleMonCompteService;

    public EcoleMonCompteController(EcoleMonCompteService ecoleMonCompteService) {
        this.ecoleMonCompteService = ecoleMonCompteService;
    }

    // ---------------------------------------------------------------------
    // Méthode utilitaire : récupérer l'email de l'utilisateur connecté
    // ---------------------------------------------------------------------

    /**
     * Récupère l'email (username) de l'utilisateur actuellement authentifié.
     * Dans ta config de sécurité, le "username" est l'email.
     */
    private String getConnectedEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Aucun utilisateur authentifié.");
        }
        return auth.getName();
    }

    // ---------------------------------------------------------------------
    // 1️⃣ GET /api/ecole/mon-etablissement
    // ---------------------------------------------------------------------

    /**
     * Récupère les informations complètes "Mon établissement"
     * pour l'utilisateur ECOLE connecté :
     *  - bloc profil (user ECOLE),
     *  - bloc établissement (Ecole),
     *  - bloc responsable accessibilité (optionnel).
     */
    @GetMapping("/mon-etablissement")
    public ResponseEntity<MonEtablissementResponse> getMonEtablissement() {
        String email = getConnectedEmail();
        MonEtablissementResponse dto = ecoleMonCompteService.getMonEtablissement(email);
        return ResponseEntity.ok(dto);
    }

    // ---------------------------------------------------------------------
    // 2️⃣ PUT /api/ecole/mon-profil
    // ---------------------------------------------------------------------

    /**
     * Mise à jour du profil de l'utilisateur ECOLE :
     *  - prénom
     *  - nom
     *  - téléphone
     *
     * Retourne le DTO global "MonEtablissementResponse" mis à jour
     * pour que le front rafraîchisse tout en une fois.
     */
    @PutMapping("/mon-profil")
    public ResponseEntity<MonEtablissementResponse> updateMonProfil(
            @RequestBody MonProfilUpdateRequest request) {

        String email = getConnectedEmail();
        MonEtablissementResponse dto = ecoleMonCompteService.updateMonProfil(email, request);
        return ResponseEntity.ok(dto);
    }

    // ---------------------------------------------------------------------
    // 3️⃣ PUT /api/ecole/mon-etablissement
    // ---------------------------------------------------------------------

    /**
     * Mise à jour des informations d'établissement
     * (nom, adresse, ville, code postal, pays, niveauAccessibilite, infosAcces).
     *
     * Re-géocode l'adresse pour mettre à jour les coordonnées GPS
     * si possible. En cas d'échec du geocoding, les anciennes coordonnées
     * sont conservées (fallback).
     */
    @PutMapping("/mon-etablissement")
    public ResponseEntity<MonEtablissementResponse> updateMonEtablissement(
            @RequestBody MonEtablissementUpdateRequest request) {

        String email = getConnectedEmail();
        MonEtablissementResponse dto = ecoleMonCompteService.updateMonEtablissement(email, request);
        return ResponseEntity.ok(dto);
    }

    // ---------------------------------------------------------------------
    // 4️⃣ PUT /api/ecole/mon-mot-de-passe
    // ---------------------------------------------------------------------

    /**
     * Changement de mot de passe pour l'utilisateur ECOLE.
     *
     * Exige :
     *  - ancienMotDePasse
     *  - nouveauMotDePasse
     *  - confirmationMotDePasse
     *
     * En cas de problème (ancien mot de passe incorrect, confirmation
     * qui ne correspond pas, longueur insuffisante, etc.), le service
     * lève une IllegalArgumentException qui sera mappée en 400
     * par ton handler global (ou autre stratégie de gestion d'erreur).
     */
    @PutMapping("/mon-mot-de-passe")
    public ResponseEntity<Void> changeMonMotDePasse(
            @RequestBody MonMotDePasseUpdateRequest request) {

        String email = getConnectedEmail();
        ecoleMonCompteService.changeMonMotDePasse(email, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // ---------------------------------------------------------------------
    // 5️⃣ Responsable accessibilité (bloc dédié)
    // ---------------------------------------------------------------------

    /**
     * GET /api/ecole/mon-responsable-accessibilite
     *
     * Permet à l'école de récupérer son Responsable accessibilité.
     *
     * - Si un responsable existe → 200 + DTO.
     * - Si aucun responsable n'est encore défini → 204 No Content
     *   (le front pourra afficher "Aucun responsable défini" dans ce cas).
     */
    @GetMapping("/mon-responsable-accessibilite")
    public ResponseEntity<ResponsableAccessibiliteDto> getMonResponsableAccessibilite() {
        String email = getConnectedEmail();
        ResponsableAccessibiliteDto dto = ecoleMonCompteService.getMonResponsableAccessibilite(email);

        if (dto == null) {
            // Pas encore de responsable enregistré pour cette école
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    /**
     * PUT /api/ecole/mon-responsable-accessibilite
     *
     * Règle métier : une école a au plus 1 responsable.
     *  - Si un responsable existe déjà pour cette école → mise à jour.
     *  - Sinon → création.
     *
     * Retourne le DTO du responsable accessibilité après upsert pour
     * que le front puisse rafraîchir l'affichage.
     */
    @PutMapping("/mon-responsable-accessibilite")
    public ResponseEntity<ResponsableAccessibiliteDto> upsertMonResponsableAccessibilite(
            @RequestBody ResponsableAccessibiliteUpsertRequest request) {

        String email = getConnectedEmail();
        ResponsableAccessibiliteDto dto =
                ecoleMonCompteService.upsertMonResponsableAccessibilite(email, request);

        return ResponseEntity.ok(dto);
    }
}

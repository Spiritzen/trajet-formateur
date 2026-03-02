package com.afci.trajet.controller.om;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.afci.trajet.dto.common.PageResponse;
import com.afci.trajet.dto.om.OrdreMissionAssignFormateurRequest;
import com.afci.trajet.dto.om.OrdreMissionCreateRequest;
import com.afci.trajet.dto.om.OrdreMissionResponse;
import com.afci.trajet.dto.om.OrdreMissionUpdateRequest;
import com.afci.trajet.dto.om.ecole.OmFormateurCandidatResponse;
import com.afci.trajet.dto.om.ecole.OmAffectationFormateurRequest;
import com.afci.trajet.service.om.OrdreMissionService;

/**
 * Contrôleur REST pour la gestion des Ordres de Mission (OM) côté ECOLE.
 *
 * Toutes les routes sont préfixées par /api/ecole/om et
 * nécessitent le rôle ROLE_ECOLE.
 */
@RestController
@RequestMapping("/api/ecole/om")
@PreAuthorize("hasRole('ECOLE')")
public class OrdreMissionController {

    private final OrdreMissionService ordreMissionService;

    public OrdreMissionController(OrdreMissionService ordreMissionService) {
        this.ordreMissionService = ordreMissionService;
    }

    // ---------------------------------------------------------------------
    // Utilitaire : récupérer l'email de l'utilisateur connecté
    // ---------------------------------------------------------------------

    private String getConnectedEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Aucun utilisateur authentifié.");
        }
        return auth.getName(); // username = email
    }

    // ---------------------------------------------------------------------
    // 1️⃣ POST /api/ecole/om  → création OM BROUILLON
    // ---------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<OrdreMissionResponse> createOrdreMission(
            @RequestBody OrdreMissionCreateRequest request
    ) {
        String email = getConnectedEmail();
        OrdreMissionResponse created =
                ordreMissionService.createOrdreMissionPourEcole(email, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // ---------------------------------------------------------------------
    // 2️⃣ PUT /api/ecole/om/{id}  → update OM BROUILLON
    // ---------------------------------------------------------------------

    @PutMapping("/{idOrdreMission}")
    public ResponseEntity<OrdreMissionResponse> updateOrdreMissionBrouillon(
            @PathVariable Integer idOrdreMission,
            @RequestBody OrdreMissionUpdateRequest request
    ) {
        String email = getConnectedEmail();
        OrdreMissionResponse updated =
                ordreMissionService.updateOrdreMissionBrouillonPourEcole(email, idOrdreMission, request);

        return ResponseEntity.ok(updated);
    }

    // ---------------------------------------------------------------------
    // 3️⃣ PUT /api/ecole/om/{id}/assign-formateur
    //     (ancienne version avec email contexte)
    // ---------------------------------------------------------------------

    @PutMapping("/{idOrdreMission}/assign-formateur")
    public ResponseEntity<OrdreMissionResponse> assignFormateur(
            @PathVariable Integer idOrdreMission,
            @RequestBody OrdreMissionAssignFormateurRequest request
    ) {
        String email = getConnectedEmail();
        OrdreMissionResponse updated =
                ordreMissionService.assignerFormateurPourOrdreMission(email, idOrdreMission, request);

        return ResponseEntity.ok(updated);
    }

    // ---------------------------------------------------------------------
    // 4️⃣ GET /api/ecole/om  → liste paginée des OM de l'école
    // ---------------------------------------------------------------------

    /**
     * Exemples :
     *   GET /api/ecole/om?page=0&size=10
     *   GET /api/ecole/om?statut=BROUILLON&page=1&size=5
     */
    @GetMapping
    public ResponseEntity<PageResponse<OrdreMissionResponse>> listOrdresMission(
            @RequestParam(name = "statut", required = false) String statut,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        // Sécurisation basique des bornes
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        String email = getConnectedEmail();
        PageResponse<OrdreMissionResponse> result =
                ordreMissionService.listerOrdresMissionPourEcole(email, statut, page, size);

        return ResponseEntity.ok(result);
    }

    // ---------------------------------------------------------------------
    // 5️⃣ GET /api/ecole/om/{id}  → détail d'un OM
    // ---------------------------------------------------------------------

    @GetMapping("/{idOrdreMission}")
    public ResponseEntity<OrdreMissionResponse> getOrdreMissionDetail(
            @PathVariable Integer idOrdreMission
    ) {
        String email = getConnectedEmail();
        OrdreMissionResponse dto =
                ordreMissionService.getOrdreMissionDetailPourEcole(email, idOrdreMission);

        return ResponseEntity.ok(dto);
    }

    // ---------------------------------------------------------------------
    // 6️⃣ GET /api/ecole/om/{idOm}/candidats-formateurs
    //     → liste des formateurs triés du plus proche au plus loin
    // ---------------------------------------------------------------------

    @GetMapping("/{idOm}/candidats-formateurs")
    public ResponseEntity<List<OmFormateurCandidatResponse>> getCandidats(
            @PathVariable("idOm") Integer idOm) {

        List<OmFormateurCandidatResponse> candidats =
                ordreMissionService.findCandidatsPourOrdre(idOm);

        return ResponseEntity.ok(candidats);
    }

    // ---------------------------------------------------------------------
    // 7️⃣ PUT /api/ecole/om/{idOm}/affectation
    //     → affecter un formateur à l'OM (BROUILLON -> PROPOSE)
    // ---------------------------------------------------------------------

    @PutMapping("/{idOm}/affectation")
    public ResponseEntity<OrdreMissionResponse> affecterFormateur(
            @PathVariable("idOm") Integer idOm,
            @RequestBody OmAffectationFormateurRequest request) {

        OrdreMissionResponse updated =
                ordreMissionService.affecterFormateur(idOm, request.getIdFormateur());

        return ResponseEntity.ok(updated);
    }
}

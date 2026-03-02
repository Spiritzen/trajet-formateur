package com.afci.trajet.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.afci.trajet.dto.common.PageResponse;
import com.afci.trajet.dto.formateur.admin.AdminFormateurCreateRequest;
import com.afci.trajet.dto.formateur.admin.AdminFormateurDetailResponse;
import com.afci.trajet.dto.formateur.admin.AdminFormateurListItemResponse;
import com.afci.trajet.dto.formateur.admin.AdminFormateurUpdateRequest;
import com.afci.trajet.service.formateur.AdminFormateurService;

/**
 * Contrôleur REST pour la gestion des FORMATEURS côté ADMIN.
 *
 * Toutes les routes sont préfixées par /api/admin/formateurs
 * et nécessitent le rôle ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/formateurs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFormateurController {

    private final AdminFormateurService service;

    public AdminFormateurController(AdminFormateurService service) {
        this.service = service;
    }

    // =========================================================
    // 1️⃣ LISTE PAGINÉE + RECHERCHE
    // =========================================================

    /**
     * Liste paginée des formateurs.
     *
     * Exemples :
     *  - GET /api/admin/formateurs
     *  - GET /api/admin/formateurs?page=1&size=20
     *  - GET /api/admin/formateurs?search=dupont&page=0&size=5
     */
    @GetMapping
    public ResponseEntity<PageResponse<AdminFormateurListItemResponse>> list(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        PageResponse<AdminFormateurListItemResponse> result =
                service.listFormateurs(search, page, size);

        return ResponseEntity.ok(result);
    }

    // =========================================================
    // 2️⃣ DÉTAIL
    // =========================================================

    @GetMapping("/{idFormateur}")
    public ResponseEntity<AdminFormateurDetailResponse> detail(
            @PathVariable Integer idFormateur
    ) {
        return ResponseEntity.ok(service.getFormateurDetail(idFormateur));
    }

    // =========================================================
    // 3️⃣ CRÉATION
    // =========================================================

    @PostMapping
    public ResponseEntity<AdminFormateurDetailResponse> create(
            @RequestBody AdminFormateurCreateRequest request
    ) {
        AdminFormateurDetailResponse created = service.createFormateur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // =========================================================
    // 4️⃣ MISE À JOUR
    // =========================================================

    @PutMapping("/{idFormateur}")
    public ResponseEntity<AdminFormateurDetailResponse> update(
            @PathVariable Integer idFormateur,
            @RequestBody AdminFormateurUpdateRequest request
    ) {
        return ResponseEntity.ok(service.updateFormateur(idFormateur, request));
    }

    // =========================================================
    // 5️⃣ SOFT DELETE
    // =========================================================

    @DeleteMapping("/{idFormateur}")
    public ResponseEntity<Void> softDelete(
            @PathVariable Integer idFormateur
    ) {
        service.softDeleteFormateur(idFormateur);
        return ResponseEntity.noContent().build();
    }
}

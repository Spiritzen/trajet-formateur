package com.afci.trajet.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.afci.trajet.dto.ecole.EcoleCreateRequest;
import com.afci.trajet.dto.ecole.EcoleDetailAdminResponse;
import com.afci.trajet.dto.ecole.EcoleResponse;
import com.afci.trajet.dto.ecole.EcoleUpdateAdminRequest;
import com.afci.trajet.dto.ecole.GetAllEcolesResponse;
import com.afci.trajet.entity.Ecole;
import com.afci.trajet.entity.Utilisateur;
import com.afci.trajet.repository.EcoleRepository;
import com.afci.trajet.repository.UtilisateurRepository;
import com.afci.trajet.service.EcoleService;

/**
 * Contrôleur regroupant les actions ADMIN liées aux ÉCOLES.
 *
 * Toutes les routes sont protégées par @PreAuthorize("hasRole('ADMIN')")
 * → seul un utilisateur avec ROLE_ADMIN peut y accéder.
 */
@RestController
@RequestMapping("/api/admin/ecoles")
public class AdminEcoleController {

    private final EcoleService ecoleService;
    private final EcoleRepository ecoleRepository;
    private final UtilisateurRepository utilisateurRepository;

    public AdminEcoleController(EcoleService ecoleService,
                                EcoleRepository ecoleRepository,
                                UtilisateurRepository utilisateurRepository) {
        this.ecoleService = ecoleService;
        this.ecoleRepository = ecoleRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // -------------------------------------------------------------------------
    // 1️⃣ POST : création école + compte ECOLE
    // -------------------------------------------------------------------------

    /**
     * Création d'une nouvelle école + compte utilisateur ECOLE (référent).
     *
     * Endpoint : POST /api/admin/ecoles
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public EcoleResponse createEcole(@RequestBody EcoleCreateRequest request) {
        return ecoleService.createEcole(request);
    }

    // -------------------------------------------------------------------------
    // 2️⃣ GET paginé : liste des écoles
    // -------------------------------------------------------------------------

    /**
     * Liste paginée des écoles, avec pour chaque entrée :
     *  - infos de base de l'établissement
     *  - nom + email du référent (utilisateur ECOLE)
     *
     * Endpoint : GET /api/admin/ecoles?page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public GetAllEcolesResponse listEcoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("nomEcole").ascending());
        Page<Ecole> result = ecoleRepository.findAll(pageable);

        List<EcoleResponse> items = result.getContent()
                .stream()
                .map(ecole -> {

                    Utilisateur ref = utilisateurRepository
                            .findById(ecole.getIdUser())
                            .orElse(null);

                    String nomReferent = (ref != null)
                            ? ref.getPrenom() + " " + ref.getNom()
                            : "(inconnu)";

                    String emailReferent = (ref != null)
                            ? ref.getEmail()
                            : "(inconnu)";

                    return new EcoleResponse(
                            ecole.getIdEcole(),
                            ecole.getNomEcole(),
                            ecole.getVille(),
                            ecole.getCodePostal(),
                            nomReferent,
                            emailReferent
                    );
                })
                .collect(Collectors.toList());

        return new GetAllEcolesResponse(
                items,
                result.getTotalElements(),
                result.getTotalPages(),
                page,
                size
        );
    }

    // -------------------------------------------------------------------------
    // 3️⃣ PUT : mise à jour d'une école EXISTANTE + référent
    // -------------------------------------------------------------------------

    /**
     * Mise à jour des informations d’une école + du compte référent.
     *
     * Endpoint :
     *  PUT /api/admin/ecoles/{idEcole}
     *
     * Body JSON attendu (EcoleUpdateAdminRequest) :
     * {
     *   "nomEcole": "Lycée Jean Jaurès",
     *   "adresseL1": "12 rue des Écoles",
     *   "adresseL2": null,
     *   "codePostal": "80000",
     *   "ville": "Amiens",
     *   "paysCode": "FR",
     *   "niveauAccessibilite": "MOYENNE",
     *   "infosAcces": "Parking gratuit devant le bâtiment",
     *
     *   "emailReferent": "contact.lycee-jj@lycee.test",
     *   "prenomReferent": "Claire",
     *   "nomReferent": "Durand",
     *   "telephoneReferent": "03 22 00 00 00"
     * }
     *
     * Règles métier :
     *  - l'ADMIN peut modifier :
     *      • les infos établissement
     *      • les infos référent (prenom, nom, email, téléphone)
     *  - si l'email du référent change, on vérifie qu'il n'est pas déjà utilisé
     *    par un autre utilisateur (contrôle d'unicité).
     *  - les coordonnées GPS lat/lon sont recalculées côté service
     *    via l'API Adresse (adresse complète puis fallback CP + ville).
     */
    @PutMapping("/{idEcole}")
    @PreAuthorize("hasRole('ADMIN')")
    public EcoleResponse updateEcoleAdmin(
            @PathVariable("idEcole") Integer idEcole,
            @RequestBody EcoleUpdateAdminRequest request
    ) {
        return ecoleService.updateEcole(idEcole, request);
    }

    // -------------------------------------------------------------------------
    // 4️⃣ DELETE : suppression d'une école
    // -------------------------------------------------------------------------

    /**
     * Suppression d'une école.
     *
     * Endpoint : DELETE /api/admin/ecoles/{idEcole}
     *
     * Pour le moment, il s’agit d’une suppression physique.
     * Plus tard, on pourra remplacer cela par une désactivation soft.
     */
    @DeleteMapping("/{idEcole}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteEcole(@PathVariable("idEcole") Integer idEcole) {
        ecoleService.deleteEcole(idEcole);
    }

    // -------------------------------------------------------------------------
    // 5️⃣ GET /search : recherche ville / codePostal (sans pagination)
    // -------------------------------------------------------------------------

    /**
     * Recherche d'écoles par ville et/ou code postal.
     *
     * Endpoint :
     *  GET /api/admin/ecoles/search?ville=Amiens&codePostal=80000
     *
     * Si aucun critère n'est fourni → renvoie les 50 premières via listEcoles(0, 50).
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public List<EcoleResponse> searchEcoles(
            @RequestParam(required = false) String ville,
            @RequestParam(required = false, name = "codePostal") String cp
    ) {
        // ⚠ si aucun critère => on renvoie TOUT (limité à 50 via listEcoles)
        if ((ville == null || ville.isBlank()) && (cp == null || cp.isBlank())) {
            return listEcoles(0, 50).getItems();
        }

        List<Ecole> results;

        if (ville != null && !ville.isBlank() && cp != null && !cp.isBlank()) {
            // Ville + CP
            results = ecoleRepository
                    .findByVilleIgnoreCaseContainingAndCodePostalStartingWith(
                            ville.trim(), cp.trim()
                    );

        } else if (ville != null && !ville.isBlank()) {
            // Ville seule
            results = ecoleRepository.findByVilleIgnoreCaseContaining(ville.trim());

        } else {
            // CP seul
            results = ecoleRepository.findByCodePostalStartingWith(cp.trim());
        }

        // On enrichit la réponse avec les infos du référent
        return results.stream()
                .map(e -> {

                    Utilisateur ref = utilisateurRepository
                            .findById(e.getIdUser())
                            .orElse(null);

                    String nomReferent = (ref != null)
                            ? ref.getPrenom() + " " + ref.getNom()
                            : "(inconnu)";

                    String emailReferent = (ref != null)
                            ? ref.getEmail()
                            : "(inconnu)";

                    return new EcoleResponse(
                            e.getIdEcole(),
                            e.getNomEcole(),
                            e.getVille(),
                            e.getCodePostal(),
                            nomReferent,
                            emailReferent
                    );
                })
                .toList();
    }

    // -------------------------------------------------------------------------
    // 6️⃣ GET détail : pour la modale "Voir"
    // -------------------------------------------------------------------------

    /**
     * Récupère le détail complet d'une école pour l'ADMIN.
     *
     * Endpoint : GET /api/admin/ecoles/{idEcole}
     * Consommé par la modale "Voir" côté React.
     */
    @GetMapping("/{idEcole}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EcoleDetailAdminResponse> getEcoleDetail(@PathVariable Integer idEcole) {
        EcoleDetailAdminResponse dto = ecoleService.getEcoleDetailForAdmin(idEcole);
        return ResponseEntity.ok(dto);
    }
}

// src/main/java/com/afci/trajet/service/formateur/AdminFormateurServiceImpl.java
package com.afci.trajet.service.formateur;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.afci.trajet.dto.common.PageResponse;
import com.afci.trajet.dto.formateur.admin.AdminFormateurCreateRequest;
import com.afci.trajet.dto.formateur.admin.AdminFormateurDetailResponse;
import com.afci.trajet.dto.formateur.admin.AdminFormateurListItemResponse;
import com.afci.trajet.dto.formateur.admin.AdminFormateurUpdateRequest;
import com.afci.trajet.entity.Formateur;
import com.afci.trajet.entity.Role;
import com.afci.trajet.entity.Utilisateur;
import com.afci.trajet.entity.UtilisateurRole;
import com.afci.trajet.entity.UtilisateurRoleId;
import com.afci.trajet.repository.FormateurRepository;
import com.afci.trajet.repository.RoleRepository;
import com.afci.trajet.repository.UtilisateurRepository;
import com.afci.trajet.repository.UtilisateurRoleRepository;
import com.afci.trajet.service.GeocodingService;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Service métier pour la gestion des FORMATEURS côté ADMIN.
 *
 * Rôle :
 *  - Créer un formateur (Utilisateur + rôle FORMATEUR + Formateur)
 *  - Lister les formateurs (paginé)
 *  - Consulter le détail
 *  - Mettre à jour
 *  - Soft delete (via Utilisateur.actif + deleted_at)
 */
@Service
@Transactional
public class AdminFormateurServiceImpl implements AdminFormateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final UtilisateurRoleRepository utilisateurRoleRepository;
    private final FormateurRepository formateurRepository;
    private final GeocodingService geocodingService;
    private final PasswordEncoder passwordEncoder;

    public AdminFormateurServiceImpl(UtilisateurRepository utilisateurRepository,
                                     RoleRepository roleRepository,
                                     UtilisateurRoleRepository utilisateurRoleRepository,
                                     FormateurRepository formateurRepository,
                                     GeocodingService geocodingService,
                                     PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.utilisateurRoleRepository = utilisateurRoleRepository;
        this.formateurRepository = formateurRepository;
        this.geocodingService = geocodingService;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // 1️⃣ LISTE PAGINÉE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminFormateurListItemResponse> listFormateurs(
            String search,
            int page,
            int size
    ) {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        Pageable pageable = PageRequest.of(page, size);

        String normalizedSearch = StringUtils.hasText(search)
                ? search.trim()
                : null;

        // 🔥 On s'appuie DIRECTEMENT sur la requête JPQL
        //     qui renvoie AdminFormateurListItemResponse
        Page<AdminFormateurListItemResponse> pageResult =
                formateurRepository.searchFormateursForAdmin(normalizedSearch, pageable);

        PageResponse<AdminFormateurListItemResponse> response = new PageResponse<>();
        response.setItems(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSize(pageResult.getSize());
        response.setTotalElements(pageResult.getTotalElements());
        response.setTotalPages(pageResult.getTotalPages());

        return response;
    }

    // =========================================================
    // 2️⃣ DÉTAIL
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public AdminFormateurDetailResponse getFormateurDetail(Integer idFormateur) {

        Formateur formateur = formateurRepository.findById(idFormateur)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formateur introuvable (id=" + idFormateur + ")"));

        Utilisateur user = utilisateurRepository.findById(formateur.getIdUser())
                .orElseThrow(() -> new IllegalStateException(
                        "Utilisateur introuvable pour ce formateur (id_user=" + formateur.getIdUser() + ")"));

        return toDetailDto(user, formateur);
    }

    // =========================================================
    // 3️⃣ CRÉATION
    // =========================================================

    @Override
    public AdminFormateurDetailResponse createFormateur(AdminFormateurCreateRequest request) {

        // -----------------------------
        // 1) VALIDATIONS
        // -----------------------------
        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("L'email est obligatoire pour créer un formateur.");
        }

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        Optional<Utilisateur> existingUser = utilisateurRepository.findByEmailIgnoreCase(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException(
                    "Un utilisateur existe déjà avec cet email : " + email);
        }

        Short zoneKm = request.getZoneKm();
        if (zoneKm == null || zoneKm < 1 || zoneKm > 500) {
            throw new IllegalArgumentException(
                    "La zone kilométrique doit être comprise entre 1 et 500 km.");
        }

        // -----------------------------
        // 2) CRÉATION UTILISATEUR
        // -----------------------------
        OffsetDateTime now = OffsetDateTime.now();

        Utilisateur user = new Utilisateur();
        user.setEmail(email);
        user.setPrenom(request.getPrenom());
        user.setNom(request.getNom());
        user.setTelephone(request.getTelephone());
        user.setActif(true);
        user.setEmailVerified(false);
        user.setTelephoneVerified(false);

        user.setAdresseL1(request.getAdresseL1());
        user.setAdresseL2(request.getAdresseL2());
        user.setCodePostal(request.getCodePostal());
        user.setVille(request.getVille());
        user.setPaysCode(
                StringUtils.hasText(request.getPaysCode())
                        ? request.getPaysCode().trim().toUpperCase(Locale.ROOT)
                        : "FR"
        );

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        String tempPassword = generateTemporaryPassword();
        user.setPasswordHash(passwordEncoder.encode(tempPassword));
        user.setPasswordUpdatedAt(now);

        user.setFailedLoginAttempts((short) 0);
        user.setLockedUntil(null);
        user.setDeletedAt(null);

        utilisateurRepository.save(user);

        // -----------------------------
        // 3) RÔLE FORMATEUR
        // -----------------------------
        Role roleFormateur = roleRepository.findByCode("FORMATEUR")
                .orElseThrow(() -> new IllegalStateException(
                        "Le rôle FORMATEUR n'existe pas en base."));

        UtilisateurRoleId urId = new UtilisateurRoleId(user.getIdUser(), roleFormateur.getIdRole());
        UtilisateurRole ur = new UtilisateurRole();
        ur.setId(urId);
        ur.setCreatedAt(now.toInstant());
        utilisateurRoleRepository.save(ur);

        // -----------------------------
        // 4) FORMATEUR
        // -----------------------------
        Formateur formateur = new Formateur();
        formateur.setIdUser(user.getIdUser());
        formateur.setZoneKm(zoneKm);
        formateur.setVehiculePerso(
                request.getVehiculePerso() != null ? request.getVehiculePerso() : Boolean.FALSE
        );
        formateur.setPermis(
                request.getPermis() != null ? request.getPermis() : Boolean.FALSE
        );
        formateur.setCommentaire(request.getCommentaire());
        formateur.setMobilitePrefJson(request.getMobilitePrefJson());
        formateur.setDisponibiliteJson(request.getDisponibiliteJson());
        formateur.setCreatedAt(now);
        formateur.setUpdatedAt(now);

        // -----------------------------
        // 5) GÉOCODAGE (lat / lon sur UTILISATEUR)
        // -----------------------------
        String adresseComplete = geocodingService.buildAdresseComplete(
                user.getAdresseL1(),
                user.getAdresseL2(),
                user.getCodePostal(),
                user.getVille(),
                user.getPaysCode()
        );

        geocodingService.geocodeAdresse(adresseComplete)
                .ifPresent(coords -> {
                    user.setLat(coords.lat());
                    user.setLon(coords.lon());
                });

        utilisateurRepository.save(user);
        formateurRepository.save(formateur);

        return toDetailDto(user, formateur);
    }

    // =========================================================
    // 4️⃣ MISE À JOUR
    // =========================================================

    @Override
    public AdminFormateurDetailResponse updateFormateur(
            Integer idFormateur,
            AdminFormateurUpdateRequest request
    ) {
        Formateur formateur = formateurRepository.findById(idFormateur)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formateur introuvable (id=" + idFormateur + ")"));

        Utilisateur user = utilisateurRepository.findById(formateur.getIdUser())
                .orElseThrow(() -> new IllegalStateException(
                        "Utilisateur introuvable pour ce formateur (id_user=" + formateur.getIdUser() + ")"));

        if (StringUtils.hasText(request.getEmail())) {
            String newEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
            if (!newEmail.equalsIgnoreCase(user.getEmail())) {
                Optional<Utilisateur> existing = utilisateurRepository.findByEmailIgnoreCase(newEmail);
                if (existing.isPresent()) {
                    throw new IllegalArgumentException(
                            "Un utilisateur existe déjà avec cet email : " + newEmail);
                }
                user.setEmail(newEmail);
            }
        }

        if (request.getZoneKm() != null) {
            Short zoneKm = request.getZoneKm();
            if (zoneKm < 1 || zoneKm > 500) {
                throw new IllegalArgumentException(
                        "La zone kilométrique doit être comprise entre 1 et 500 km.");
            }
            formateur.setZoneKm(zoneKm);
        }

        if (StringUtils.hasText(request.getPrenom())) {
            user.setPrenom(request.getPrenom().trim());
        }
        if (StringUtils.hasText(request.getNom())) {
            user.setNom(request.getNom().trim());
        }
        if (StringUtils.hasText(request.getTelephone())) {
            user.setTelephone(request.getTelephone().trim());
        }

        String oldAdresse = geocodingService.buildAdresseComplete(
                user.getAdresseL1(),
                user.getAdresseL2(),
                user.getCodePostal(),
                user.getVille(),
                user.getPaysCode()
        );

        if (request.getAdresseL1() != null) user.setAdresseL1(request.getAdresseL1());
        if (request.getAdresseL2() != null) user.setAdresseL2(request.getAdresseL2());
        if (request.getCodePostal() != null) user.setCodePostal(request.getCodePostal());
        if (request.getVille() != null) user.setVille(request.getVille());
        if (request.getPaysCode() != null) {
            user.setPaysCode(request.getPaysCode().trim().toUpperCase(Locale.ROOT));
        }

        if (request.getVehiculePerso() != null) {
            formateur.setVehiculePerso(request.getVehiculePerso());
        }
        if (request.getPermis() != null) {
            formateur.setPermis(request.getPermis());
        }
        if (request.getCommentaire() != null) {
            formateur.setCommentaire(request.getCommentaire());
        }
        if (request.getMobilitePrefJson() != null) {
            formateur.setMobilitePrefJson(request.getMobilitePrefJson());
        }
        if (request.getDisponibiliteJson() != null) {
            formateur.setDisponibiliteJson(request.getDisponibiliteJson());
        }

        OffsetDateTime now = OffsetDateTime.now();
        user.setUpdatedAt(now);
        formateur.setUpdatedAt(now);

        String newAdresse = geocodingService.buildAdresseComplete(
                user.getAdresseL1(),
                user.getAdresseL2(),
                user.getCodePostal(),
                user.getVille(),
                user.getPaysCode()
        );

        if (!newAdresse.equals(oldAdresse)) {
            geocodingService.geocodeAdresse(newAdresse)
                    .ifPresent(coords -> {
                        user.setLat(coords.lat());
                        user.setLon(coords.lon());
                    });
        }

        utilisateurRepository.save(user);
        formateurRepository.save(formateur);

        return toDetailDto(user, formateur);
    }

    // =========================================================
    // 5️⃣ SOFT DELETE
    // =========================================================

    @Override
    public void softDeleteFormateur(Integer idFormateur) {
        Formateur formateur = formateurRepository.findById(idFormateur)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formateur introuvable (id=" + idFormateur + ")"));

        Utilisateur user = utilisateurRepository.findById(formateur.getIdUser())
                .orElseThrow(() -> new IllegalStateException(
                        "Utilisateur introuvable pour ce formateur (id_user=" + formateur.getIdUser() + ")"));

        OffsetDateTime now = OffsetDateTime.now();

        user.setActif(false);
        user.setDeletedAt(now);
        user.setUpdatedAt(now);

        utilisateurRepository.save(user);
    }

    // =========================================================
    // Helpers
    // =========================================================

    private String generateTemporaryPassword() {
        return "Formateur123!";
    }

    private AdminFormateurDetailResponse toDetailDto(Utilisateur user, Formateur f) {
        AdminFormateurDetailResponse dto = new AdminFormateurDetailResponse();

        dto.setIdUser(user.getIdUser());
        dto.setEmail(user.getEmail());
        dto.setPrenom(user.getPrenom());
        dto.setNom(user.getNom());
        dto.setTelephone(user.getTelephone());
        dto.setAdresseL1(user.getAdresseL1());
        dto.setAdresseL2(user.getAdresseL2());
        dto.setCodePostal(user.getCodePostal());
        dto.setVille(user.getVille());
        dto.setPaysCode(user.getPaysCode());
        dto.setLat(user.getLat());
        dto.setLon(user.getLon());
        dto.setActif(user.isActif());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setDeletedAt(user.getDeletedAt());

        dto.setIdFormateur(f.getIdFormateur());
        dto.setZoneKm(f.getZoneKm());
        dto.setVehiculePerso(f.isVehiculePerso());
        dto.setPermis(f.isPermis());
        dto.setCommentaire(f.getCommentaire());
        dto.setMobilitePrefJson(f.getMobilitePrefJson());
        dto.setDisponibiliteJson(f.getDisponibiliteJson());
        dto.setCreatedAtFormateur(f.getCreatedAt());
        dto.setUpdatedAtFormateur(f.getUpdatedAt());

        return dto;
    }
}

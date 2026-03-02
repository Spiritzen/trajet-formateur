// src/main/java/com/afci/trajet/service/formateur/moncompte/FormateurMonCompteServiceImpl.java
package com.afci.trajet.service.formateur.moncompte;

import java.time.OffsetDateTime;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.afci.trajet.dto.formateur.moncompte.ChangePasswordRequest;
import com.afci.trajet.dto.formateur.moncompte.FormateurProfileResponse;
import com.afci.trajet.dto.formateur.moncompte.FormateurProfileUpdateRequest;
import com.afci.trajet.entity.Formateur;
import com.afci.trajet.entity.Utilisateur;
import com.afci.trajet.repository.FormateurRepository;
import com.afci.trajet.repository.UtilisateurRepository;
import com.afci.trajet.service.GeocodingService;

/**
 * Implémentation du service "Mon compte" pour le rôle FORMATEUR.
 */
@Service
@Transactional
public class FormateurMonCompteServiceImpl implements FormateurMonCompteService {

    private final UtilisateurRepository utilisateurRepository;
    private final FormateurRepository formateurRepository;
    private final GeocodingService geocodingService;
    private final PasswordEncoder passwordEncoder;

    public FormateurMonCompteServiceImpl(UtilisateurRepository utilisateurRepository,
                                         FormateurRepository formateurRepository,
                                         GeocodingService geocodingService,
                                         PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.formateurRepository = formateurRepository;
        this.geocodingService = geocodingService;
        this.passwordEncoder = passwordEncoder;
    }

    // --------------------------------------------------------
    // 1) LECTURE DU PROFIL
    // --------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public FormateurProfileResponse getProfilByUserId(Integer idUser) {

        // 1. Récupération du compte utilisateur
        Utilisateur user = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable (id_user=" + idUser + ")"));

        // 2. Récupération de la fiche formateur associée
        Formateur formateur = formateurRepository.findByIdUser(idUser)
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun formateur associé à cet utilisateur (id_user=" + idUser + ")"));

        // 3. Mapping vers DTO
        return toProfileResponse(user, formateur);
    }

    // --------------------------------------------------------
    // 2) MISE À JOUR DU PROFIL
    // --------------------------------------------------------

    @Override
    public FormateurProfileResponse updateProfilByUserId(Integer idUser, FormateurProfileUpdateRequest request) {

        Utilisateur user = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable (id_user=" + idUser + ")"));

        Formateur formateur = formateurRepository.findByIdUser(idUser)
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun formateur associé à cet utilisateur (id_user=" + idUser + ")"));

        // -----------------------------
        // 1) Mise à jour des infos utilisateur
        // -----------------------------

        if (StringUtils.hasText(request.getTelephone())) {
            user.setTelephone(request.getTelephone().trim());
        }

        // Adresse avant modification (pour savoir si on doit regéocoder)
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

        // -----------------------------
        // 2) Mise à jour des infos formateur
        // -----------------------------

        if (request.getZoneKm() != null) {
            Short zoneKm = request.getZoneKm();
            if (zoneKm < 0 || zoneKm > 500) {
                throw new IllegalArgumentException(
                        "La zone kilométrique doit être comprise entre 0 et 500 km.");
            }
            formateur.setZoneKm(zoneKm);
        }

        if (request.getVehiculePerso() != null) {
            formateur.setVehiculePerso(request.getVehiculePerso());
        }
        if (request.getPermis() != null) {
            formateur.setPermis(request.getPermis());
        }
        if (request.getMobilitePrefJson() != null) {
            formateur.setMobilitePrefJson(request.getMobilitePrefJson());
        }
        if (request.getDisponibiliteJson() != null) {
            formateur.setDisponibiliteJson(request.getDisponibiliteJson());
        }
        if (request.getCommentaire() != null) {
            formateur.setCommentaire(request.getCommentaire());
        }

        // -----------------------------
        // 3) Horodatage + géocoding si adresse modifiée
        // -----------------------------

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

        return toProfileResponse(user, formateur);
    }

    // --------------------------------------------------------
    // 3) CHANGEMENT DE MOT DE PASSE
    // --------------------------------------------------------

    @Override
    public void changePassword(Integer idUser, ChangePasswordRequest request) {

        Utilisateur user = utilisateurRepository.findById(idUser)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable (id_user=" + idUser + ")"));

        if (!StringUtils.hasText(request.getCurrentPassword()) ||
            !StringUtils.hasText(request.getNewPassword()) ||
            !StringUtils.hasText(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException(
                    "Les champs mot de passe courant / nouveau / confirmation sont obligatoires.");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("La confirmation du nouveau mot de passe ne correspond pas.");
        }

        // Vérification de l'ancien mot de passe
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect.");
        }

        // Mise à jour du password_hash
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordUpdatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        utilisateurRepository.save(user);
    }

    // --------------------------------------------------------
    // Helper : mapping entités -> DTO
    // --------------------------------------------------------

    private FormateurProfileResponse toProfileResponse(Utilisateur user, Formateur formateur) {
        Double lat = (user.getLat() != null) ? user.getLat().doubleValue() : null;
        Double lon = (user.getLon() != null) ? user.getLon().doubleValue() : null;

        FormateurProfileResponse dto = new FormateurProfileResponse();
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
        dto.setCreatedAtUser(user.getCreatedAt());
        dto.setUpdatedAtUser(user.getUpdatedAt());

        dto.setIdFormateur(formateur.getIdFormateur());
        dto.setZoneKm(formateur.getZoneKm());
        dto.setVehiculePerso(formateur.isVehiculePerso());
        dto.setPermis(formateur.isPermis());
        dto.setMobilitePrefJson(formateur.getMobilitePrefJson());
        dto.setDisponibiliteJson(formateur.getDisponibiliteJson());
        dto.setCommentaire(formateur.getCommentaire());
        dto.setCreatedAtFormateur(formateur.getCreatedAt());
        dto.setUpdatedAtFormateur(formateur.getUpdatedAt());

        return dto;
    }
}

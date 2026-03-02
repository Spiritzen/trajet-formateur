package com.afci.trajet.service;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afci.trajet.dto.ecole.MonEtablissementResponse;
import com.afci.trajet.dto.ecole.MonEtablissementUpdateRequest;
import com.afci.trajet.dto.ecole.MonMotDePasseUpdateRequest;
import com.afci.trajet.dto.ecole.MonProfilUpdateRequest;
import com.afci.trajet.dto.ecole.ResponsableAccessibiliteDto;
import com.afci.trajet.dto.ecole.ResponsableAccessibiliteUpsertRequest;
import com.afci.trajet.entity.Ecole;
import com.afci.trajet.entity.ResponsableAccessibilite;
import com.afci.trajet.entity.Utilisateur;
import com.afci.trajet.repository.EcoleRepository;
import com.afci.trajet.repository.ResponsableAccessibiliteRepository;
import com.afci.trajet.repository.UtilisateurRepository;

/**
 * Service métier pour l'espace "Mon compte / Mon établissement" côté ECOLE.
 *
 * Toutes les méthodes partent de l'email du compte connecté :
 *  - on récupère d'abord l'utilisateur,
 *  - puis l'école associée via la FK id_user,
 *  - puis on construit un DTO global.
 *
 * Ce service gère :
 *  - la lecture globale (profil + établissement + responsable accessibilité),
 *  - la mise à jour du profil utilisateur ECOLE,
 *  - la mise à jour de l'établissement (avec re-géocodage),
 *  - le changement de mot de passe,
 *  - la lecture / création / mise à jour du Responsable accessibilité.
 */
@Service
public class EcoleMonCompteService {

    private final UtilisateurRepository utilisateurRepository;
    private final EcoleRepository ecoleRepository;
    private final GeocodingService geocodingService;
    private final PasswordEncoder passwordEncoder;
    private final ResponsableAccessibiliteRepository responsableRepository;

    public EcoleMonCompteService(UtilisateurRepository utilisateurRepository,
                                 EcoleRepository ecoleRepository,
                                 GeocodingService geocodingService,
                                 PasswordEncoder passwordEncoder,
                                 ResponsableAccessibiliteRepository responsableRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.ecoleRepository = ecoleRepository;
        this.geocodingService = geocodingService;
        this.passwordEncoder = passwordEncoder;
        this.responsableRepository = responsableRepository;
    }

    // ---------------------------------------------------------------------
    // Méthodes utilitaires internes
    // ---------------------------------------------------------------------

    /**
     * Récupère l'utilisateur ECOLE par email (ignore case) ou lève une exception.
     */
    private Utilisateur getUserByEmailOrThrow(String emailConnected) {
        return utilisateurRepository.findByEmailIgnoreCase(emailConnected)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur ECOLE introuvable pour l'email : " + emailConnected));
    }

    /**
     * Récupère l'école associée à un utilisateur donné (via id_user) ou lève une exception.
     */
    private Ecole getEcoleByUserOrThrow(Utilisateur user) {
        return ecoleRepository.findByIdUser(user.getIdUser())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucune école associée à ce compte. Contactez un administrateur."));
    }

    // ---------------------------------------------------------------------
    // 1️⃣ Lecture globale : profil + établissement + responsable accessibilité
    // ---------------------------------------------------------------------

    /**
     * Récupère le "mon établissement" complet pour l'utilisateur ECOLE connecté.
     *
     * @param emailConnected email de l'utilisateur connecté (principal Spring Security)
     * @return DTO regroupant profil + établissement + responsable accessibilité (optionnel)
     */
    @Transactional(readOnly = true)
    public MonEtablissementResponse getMonEtablissement(String emailConnected) {

        // 1) Récupérer le compte utilisateur par email (ignore case)
        Utilisateur user = getUserByEmailOrThrow(emailConnected);

        // 2) Récupérer l'école associée via la FK id_user
        Ecole ecole = getEcoleByUserOrThrow(user);

        // 3) Récupérer éventuellement le Responsable accessibilité (0 ou 1 par école)
        Optional<ResponsableAccessibilite> optResp =
                responsableRepository.findByIdEcole(ecole.getIdEcole());

        ResponsableAccessibiliteDto respDto = optResp
                .map(this::mapToResponsableDto)
                .orElse(null);

        // 4) Construire le DTO de réponse global (profil + établissement + responsable)
        return buildMonEtablissementResponse(user, ecole, respDto);
    }

    // ---------------------------------------------------------------------
    // 2️⃣ Mise à jour du PROFIL (compte utilisateur ECOLE)
    // ---------------------------------------------------------------------

    /**
     * Met à jour les informations de profil du compte ECOLE :
     *  - prénom
     *  - nom
     *  - téléphone
     *
     * L'email (identifiant de connexion) n'est pas modifié ici.
     */
    @Transactional
    public MonEtablissementResponse updateMonProfil(String emailConnected,
                                                    MonProfilUpdateRequest request) {

        // 1) Récupérer l'utilisateur
        Utilisateur user = getUserByEmailOrThrow(emailConnected);

        // 2) Mettre à jour les champs autorisés
        user.setPrenom(request.getPrenom());
        user.setNom(request.getNom());
        user.setTelephone(request.getTelephone());

        // 3) Sauvegarder
        utilisateurRepository.save(user);

        // 4) Récupérer l'école liée
        Ecole ecole = getEcoleByUserOrThrow(user);

        // 5) Récupérer le responsable accessibilité pour reconstruire le DTO complet
        ResponsableAccessibiliteDto respDto = responsableRepository
                .findByIdEcole(ecole.getIdEcole())
                .map(this::mapToResponsableDto)
                .orElse(null);

        return buildMonEtablissementResponse(user, ecole, respDto);
    }

    // ---------------------------------------------------------------------
    // 3️⃣ Mise à jour de l'ÉTABLISSEMENT (adresse, nom, accessibilité...)
    // ---------------------------------------------------------------------

    /**
     * Met à jour les informations d'établissement pour l'utilisateur ECOLE connecté.
     *
     * ⚠ lat / lon ne sont pas directement modifiés par le front :
     * on reconstruit une adresse complète et on appelle l'API Adresse
     * via GeocodingService. Si l'API renvoie quelque chose, on met à jour
     * les coordonnées, sinon on conserve les anciennes (fallback).
     */
    @Transactional
    public MonEtablissementResponse updateMonEtablissement(String emailConnected,
                                                           MonEtablissementUpdateRequest request) {

        // 1) Récupérer l'utilisateur
        Utilisateur user = getUserByEmailOrThrow(emailConnected);

        // 2) Récupérer l'école associée
        Ecole ecole = getEcoleByUserOrThrow(user);

        // 3) Mettre à jour les champs établissement
        ecole.setNomEcole(request.getNomEcole());
        ecole.setAdresseL1(request.getAdresseL1());
        ecole.setAdresseL2(request.getAdresseL2());
        ecole.setCodePostal(request.getCodePostal());
        ecole.setVille(request.getVille());
        ecole.setPaysCode(request.getPaysCode());
        ecole.setNiveauAccessibilite(request.getNiveauAccessibilite());
        ecole.setInfosAcces(request.getInfosAcces());

        // 4) Re-géocodage de l'adresse (API Adresse data.gouv)
        String adresseComplete = geocodingService.buildAdresseComplete(
                request.getAdresseL1(),
                request.getAdresseL2(),
                request.getCodePostal(),
                request.getVille(),
                request.getPaysCode()
        );

        geocodingService.geocodeAdresse(adresseComplete)
                .ifPresent(coords -> {
                    // Si l'API renvoie quelque chose, on met lat/lon à jour
                    ecole.setLat(coords.lat());
                    ecole.setLon(coords.lon());
                });
        // Si l'API ne renvoie rien, on conserve les anciennes coordonnées :
        // pas de "else", c'est le fallback naturel.

        // 5) Sauvegarder
        ecoleRepository.save(ecole);

        // 6) Récupérer le responsable accessibilité pour reconstruire le DTO complet
        ResponsableAccessibiliteDto respDto = responsableRepository
                .findByIdEcole(ecole.getIdEcole())
                .map(this::mapToResponsableDto)
                .orElse(null);

        // 7) Construire la réponse globale
        return buildMonEtablissementResponse(user, ecole, respDto);
    }

    // ---------------------------------------------------------------------
    // 4️⃣ Changement de MOT DE PASSE
    // ---------------------------------------------------------------------

    /**
     * Changement de mot de passe pour l'utilisateur ECOLE connecté.
     *
     * Règles :
     *  - on vérifie que l'ancien mot de passe est correct,
     *  - on vérifie que nouveauMotDePasse == confirmationMotDePasse,
     *  - on impose une longueur minimale (ex: 8 caractères),
     *  - on encode le nouveau mot de passe avec PasswordEncoder.
     */
    @Transactional
    public void changeMonMotDePasse(String emailConnected,
                                    MonMotDePasseUpdateRequest request) {

        // 1) Récupérer l'utilisateur
        Utilisateur user = getUserByEmailOrThrow(emailConnected);

        // 2) Vérifier l'ancien mot de passe
        if (request.getAncienMotDePasse() == null ||
            !passwordEncoder.matches(request.getAncienMotDePasse(), user.getPasswordHash())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect.");
        }

        // 3) Vérifier la cohérence nouveau / confirmation
        if (request.getNouveauMotDePasse() == null ||
            request.getConfirmationMotDePasse() == null ||
            !request.getNouveauMotDePasse().equals(request.getConfirmationMotDePasse())) {
            throw new IllegalArgumentException("La confirmation du nouveau mot de passe ne correspond pas.");
        }

        // 4) Vérifier quelques règles simples (ex: longueur minimale)
        if (request.getNouveauMotDePasse().length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caractères.");
        }

        // 5) Encoder et sauvegarder
        user.setPasswordHash(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateurRepository.save(user);
    }

    // ---------------------------------------------------------------------
    // 5️⃣ Responsable accessibilité : lecture + upsert
    // ---------------------------------------------------------------------

    /**
     * GET /api/ecole/mon-responsable-accessibilite
     *
     * Permet à l'école de récupérer son Responsable accessibilité.
     * Peut retourner null si aucun responsable n'a encore été défini.
     */
    @Transactional(readOnly = true)
    public ResponsableAccessibiliteDto getMonResponsableAccessibilite(String emailConnected) {

        Utilisateur user = getUserByEmailOrThrow(emailConnected);
        Ecole ecole = getEcoleByUserOrThrow(user);

        return responsableRepository.findByIdEcole(ecole.getIdEcole())
                .map(this::mapToResponsableDto)
                .orElse(null);
    }

    /**
     * PUT /api/ecole/mon-responsable-accessibilite
     *
     * Règle métier : une école a au plus 1 responsable.
     *  - Si un responsable existe déjà pour cette école → mise à jour.
     *  - Sinon → création.
     */
    @Transactional
    public ResponsableAccessibiliteDto upsertMonResponsableAccessibilite(
            String emailConnected,
            ResponsableAccessibiliteUpsertRequest request) {

        Utilisateur user = getUserByEmailOrThrow(emailConnected);
        Ecole ecole = getEcoleByUserOrThrow(user);

        // On regarde s'il existe déjà un responsable pour cette école
        ResponsableAccessibilite responsable = responsableRepository
                .findByIdEcole(ecole.getIdEcole())
                .orElseGet(() -> {
                    ResponsableAccessibilite r = new ResponsableAccessibilite();
                    r.setIdEcole(ecole.getIdEcole());
                    r.setCreatedAt(OffsetDateTime.now());
                    return r;
                });

        // Mise à jour des champs métier
        responsable.setNom(request.getNom());
        responsable.setPrenom(request.getPrenom());
        responsable.setFonction(request.getFonction());
        responsable.setTelephone(request.getTelephone());
        responsable.setEmail(request.getEmail());
        responsable.setPlageHoraire(request.getPlageHoraire());
        responsable.setUpdatedAt(OffsetDateTime.now());

        ResponsableAccessibilite saved = responsableRepository.save(responsable);

        return mapToResponsableDto(saved);
    }

    // ---------------------------------------------------------------------
    // Méthodes utilitaires : mapping & construction DTO global
    // ---------------------------------------------------------------------

    /**
     * Mapping entité ResponsableAccessibilite -> DTO exposé à l'API.
     */
    private ResponsableAccessibiliteDto mapToResponsableDto(ResponsableAccessibilite entity) {
        if (entity == null) {
            return null;
        }
        return new ResponsableAccessibiliteDto(
                entity.getIdResponsable(),
                entity.getNom(),
                entity.getPrenom(),
                entity.getFonction(),
                entity.getTelephone(),
                entity.getEmail(),
                entity.getPlageHoraire()
        );
    }

    /**
     * Construit le DTO global MonEtablissementResponse à partir :
     *  - du compte utilisateur ECOLE,
     *  - de l'établissement,
     *  - et éventuellement du Responsable accessibilité.
     *
     * On passe par les setters pour rester compatible avec l'évolution
     * de MonEtablissementResponse (ajouts de champs, etc.).
     */
    private MonEtablissementResponse buildMonEtablissementResponse(Utilisateur user,
                                                                   Ecole ecole,
                                                                   ResponsableAccessibiliteDto respDto) {
        MonEtablissementResponse dto = new MonEtablissementResponse();

        // --- Bloc profil utilisateur ---
        dto.setIdUser(user.getIdUser());
        dto.setEmail(user.getEmail());
        dto.setPrenom(user.getPrenom());
        dto.setNom(user.getNom());
        dto.setTelephone(user.getTelephone());

        // --- Bloc établissement ---
        dto.setIdEcole(ecole.getIdEcole());
        dto.setNomEcole(ecole.getNomEcole());
        dto.setAdresseL1(ecole.getAdresseL1());
        dto.setAdresseL2(ecole.getAdresseL2());
        dto.setCodePostal(ecole.getCodePostal());
        dto.setVille(ecole.getVille());
        dto.setPaysCode(ecole.getPaysCode());
        dto.setNiveauAccessibilite(ecole.getNiveauAccessibilite());
        dto.setInfosAcces(ecole.getInfosAcces());
        dto.setLat(ecole.getLat());
        dto.setLon(ecole.getLon());

        // --- Bloc responsable accessibilité (peut être null) ---
        dto.setResponsableAccessibilite(respDto);

        return dto;
    }
}

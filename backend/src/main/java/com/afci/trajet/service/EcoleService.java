package com.afci.trajet.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.afci.trajet.dto.ecole.EcoleCreateRequest;
import com.afci.trajet.dto.ecole.EcoleDetailAdminResponse;
import com.afci.trajet.dto.ecole.EcoleResponse;
import com.afci.trajet.dto.ecole.EcoleUpdateAdminRequest;
import com.afci.trajet.entity.Ecole;
import com.afci.trajet.entity.Role;
import com.afci.trajet.entity.Utilisateur;
import com.afci.trajet.repository.EcoleRepository;
import com.afci.trajet.repository.RoleRepository;
import com.afci.trajet.repository.UtilisateurRepository;

/**
 * Service métier dédié à la gestion des ÉCOLES.
 *
 * Rôles principaux :
 * - créer une école + compte ECOLE associé ;
 * - mettre à jour les informations d’une école (+ référent côté admin) ;
 * - supprimer (pour l’instant : suppression physique) une école.
 *
 * L’annotation @Transactional permet d’assurer la cohérence :
 * soit tout réussit, soit rien n’est écrit en base en cas d’erreur.
 */
@Service
public class EcoleService {

    private final UtilisateurRepository utilisateurRepository;
    private final EcoleRepository ecoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeocodingService geocodingService;

    public EcoleService(UtilisateurRepository utilisateurRepository,
                        EcoleRepository ecoleRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder,
                        GeocodingService geocodingService) {
        this.utilisateurRepository = utilisateurRepository;
        this.ecoleRepository = ecoleRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.geocodingService = geocodingService;
    }

    // ---------------------------------------------------------------------
    // 1️⃣ CRÉATION ÉCOLE + UTILISATEUR ECOLE
    // ---------------------------------------------------------------------

    /**
     * Création d'une école + utilisateur référent (rôle ECOLE).
     *
     * Étapes :
     * - vérifier que l’email n’est pas déjà utilisé ;
     * - récupérer le rôle ROLE_ECOLE ;
     * - créer l'utilisateur (table utilisateur) ;
     * - lier le rôle dans utilisateur_role ;
     * - créer la ligne dans la table ecole ;
     * - géocoder l’adresse pour remplir lat / lon ;
     * - renvoyer un DTO EcoleResponse prêt pour l’API.
     */
    @Transactional
    public EcoleResponse createEcole(EcoleCreateRequest request) {

        // 1) Validation email unique
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur existe déjà avec cet email.");
        }

        // 2) Récupérer le rôle ECOLE
        Role roleEcole = roleRepository.findByCode("ECOLE")
                .orElseThrow(() -> new IllegalStateException(
                        "Rôle ECOLE introuvable. Vérifiez l'initialisation de la table 'role'."));

        // 3) Créer l'utilisateur référent
        Utilisateur u = new Utilisateur();
        u.setEmail(request.getEmail());
        u.setPrenom(request.getPrenom());
        u.setNom(request.getNom());
        u.setTelephone(request.getTelephone());

        // 🔐 Initialisation du mot de passe SEULEMENT à la création
        // (ex: "Ecole123!")
        u.setPasswordHash(passwordEncoder.encode(request.getMotDePasse()));

        utilisateurRepository.save(u);

        // 4) Lier le rôle ECOLE dans la table utilisateur_role
        utilisateurRepository.assignRoleToUser(u.getIdUser(), roleEcole.getIdRole());

        // 5) Créer l’entrée dans la table ECOLE
        Ecole e = new Ecole();
        e.setNomEcole(request.getNomEcole());
        e.setAdresseL1(request.getAdresseL1());
        e.setAdresseL2(request.getAdresseL2());
        e.setCodePostal(request.getCodePostal());
        e.setVille(request.getVille());
        e.setPaysCode(request.getPaysCode());
        e.setNiveauAccessibilite(request.getNiveauAccessibilite());
        e.setInfosAcces(request.getInfosAcces());

        // FK vers le compte utilisateur référent
        e.setIdUser(u.getIdUser());

        // 6) Géocodage via API Adresse (avec fallback interne dans le service)
        String adresseComplete = geocodingService.buildAdresseComplete(
                request.getAdresseL1(),
                request.getAdresseL2(),
                request.getCodePostal(),
                request.getVille(),
                request.getPaysCode()
        );

        geocodingService.geocodeAdresse(adresseComplete)
                .ifPresent(coords -> {
                    e.setLat(coords.lat());
                    e.setLon(coords.lon());
                });

        // 7) Sauvegarde de l'école
        ecoleRepository.save(e);

        // 8) Construire le DTO de réponse
        String nomCompletReferent = u.getPrenom() + " " + u.getNom();

        return new EcoleResponse(
                e.getIdEcole(),
                e.getNomEcole(),
                e.getVille(),
                e.getCodePostal(),
                nomCompletReferent,
                u.getEmail()
        );
    }

    // ---------------------------------------------------------------------
    // 2️⃣ MISE À JOUR ÉCOLE (ADMIN) + INFOS RÉFÉRENT (SANS MOT DE PASSE)
    // ---------------------------------------------------------------------

    /**
     * Mise à jour d'une école existante côté ADMIN.
     *
     * ⚠ Ici :
     *  - on met à jour les données de la table ECOLE
     *  - on met à jour les infos de l'utilisateur référent (email, prénom, nom, téléphone)
     *  - on recalcule lat/lon à partir de la nouvelle adresse
     *  - ON NE TOUCHE PAS AU MOT DE PASSE (passwordHash reste inchangé)
     */
    @Transactional
    public EcoleResponse updateEcole(Integer idEcole, EcoleUpdateAdminRequest request) {

        // 1) Récupérer l'école existante
        Ecole ecole = ecoleRepository.findById(idEcole)
                .orElseThrow(() -> new IllegalArgumentException("École introuvable pour id=" + idEcole));

        // 2) Mettre à jour les champs de l'école
        ecole.setNomEcole(request.getNomEcole());
        ecole.setAdresseL1(request.getAdresseL1());
        ecole.setAdresseL2(request.getAdresseL2());
        ecole.setCodePostal(request.getCodePostal());
        ecole.setVille(request.getVille());
        ecole.setPaysCode(request.getPaysCode());
        ecole.setNiveauAccessibilite(request.getNiveauAccessibilite());
        ecole.setInfosAcces(request.getInfosAcces());

        // 3) Mettre à jour le référent SI présent
        if (ecole.getIdUser() != null) {
            Optional<Utilisateur> optRef = utilisateurRepository.findById(ecole.getIdUser());
            if (optRef.isPresent()) {
                Utilisateur ref = optRef.get();

                // ✏️ Mise à jour des infos de contact UNIQUEMENT
                ref.setEmail(request.getEmailReferent());
                ref.setPrenom(request.getPrenomReferent());
                ref.setNom(request.getNomReferent());
                ref.setTelephone(request.getTelephoneReferent());

                // ❌ IMPORTANT : on NE TOUCHE PAS à ref.getPasswordHash()
                // → le mot de passe reste celui déjà présent en BDD,
                //   même si l'école l'a changé via un autre flux.

                utilisateurRepository.save(ref);
            }
        }

        // 4) Re-géocodage systématique sur la nouvelle adresse
        String adresseComplete = geocodingService.buildAdresseComplete(
                request.getAdresseL1(),
                request.getAdresseL2(),
                request.getCodePostal(),
                request.getVille(),
                request.getPaysCode()
        );

        geocodingService.geocodeAdresse(adresseComplete)
                .ifPresent(coords -> {
                    ecole.setLat(coords.lat());
                    ecole.setLon(coords.lon());
                });

        // 5) Sauvegarder l'école
        ecoleRepository.save(ecole);

        // 6) Récupérer le référent (pour compléter le DTO)
        Optional<Utilisateur> optRef = (ecole.getIdUser() != null)
                ? utilisateurRepository.findById(ecole.getIdUser())
                : Optional.empty();

        Utilisateur ref = optRef.orElse(null);

        String nomCompletReferent = (ref != null)
                ? ref.getPrenom() + " " + ref.getNom()
                : "(inconnu)";

        String emailReferent = (ref != null)
                ? ref.getEmail()
                : "(inconnu)";

        // 7) Retourner le DTO de réponse mis à jour
        return new EcoleResponse(
                ecole.getIdEcole(),
                ecole.getNomEcole(),
                ecole.getVille(),
                ecole.getCodePostal(),
                nomCompletReferent,
                emailReferent
        );
    }

    // ---------------------------------------------------------------------
    // 3️⃣ SUPPRESSION ÉCOLE (ADMIN) - SIMPLE, SÉCURISÉE
    // ---------------------------------------------------------------------

    /**
     * Suppression d'une école.
     *
     * Pour l'instant, on effectue une suppression physique en base (DELETE).
     *
     * ⚠️ Si des données liées (ordres de mission, trajets, signatures, etc.)
     * empêchent la suppression (contrainte FK), on remonte une erreur HTTP 409
     * avec un message métier clair. Aucune donnée incohérente n'est écrite.
     *
     * ⚠️ On ne supprime pas l'utilisateur référent ici.
     */
    @Transactional
    public void deleteEcole(Integer idEcole) {

        // 1) Vérifier que l'école existe
        if (!ecoleRepository.existsById(idEcole)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Impossible de supprimer : école introuvable pour id=" + idEcole
            );
        }

        try {
            // 2) Tentative de suppression physique
            ecoleRepository.deleteById(idEcole);

        } catch (DataIntegrityViolationException ex) {
            // 3) Cas où la BDD bloque à cause de FK (OM, trajets, etc.)
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Impossible de supprimer cette école : des ordres de mission, trajets ou autres données y sont encore rattachés."
            );
        }
    }

    // ---------------------------------------------------------------------
    // 4️⃣ LECTURE DÉTAILLÉE ÉCOLE POUR ADMIN
    // ---------------------------------------------------------------------

    @Transactional(readOnly = true)
    public EcoleDetailAdminResponse getEcoleDetailForAdmin(Integer idEcole) {

        Ecole ecole = ecoleRepository.findById(idEcole)
                .orElseThrow(() ->
                        new IllegalArgumentException("École introuvable pour id=" + idEcole));

        Optional<Utilisateur> optRef = (ecole.getIdUser() != null)
                ? utilisateurRepository.findById(ecole.getIdUser())
                : Optional.empty();

        Utilisateur ref = optRef.orElse(null);

        Integer idUserReferent = (ref != null) ? ref.getIdUser() : null;
        String emailRef = (ref != null) ? ref.getEmail() : null;
        String prenomRef = (ref != null) ? ref.getPrenom() : null;
        String nomRef = (ref != null) ? ref.getNom() : null;
        String telRef = (ref != null) ? ref.getTelephone() : null;

        return new EcoleDetailAdminResponse(
                ecole.getIdEcole(),
                idUserReferent,
                ecole.getNomEcole(),
                ecole.getAdresseL1(),
                ecole.getAdresseL2(),
                ecole.getCodePostal(),
                ecole.getVille(),
                ecole.getPaysCode(),
                ecole.getLat(),
                ecole.getLon(),
                ecole.getNiveauAccessibilite(),
                ecole.getInfosAcces(),
                emailRef,
                prenomRef,
                nomRef,
                telRef
        );
    }
}

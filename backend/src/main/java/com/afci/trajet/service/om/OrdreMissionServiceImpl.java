package com.afci.trajet.service.om;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Comparator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afci.trajet.dto.common.PageResponse;
import com.afci.trajet.dto.om.OrdreMissionAssignFormateurRequest;
import com.afci.trajet.dto.om.OrdreMissionCreateRequest;
import com.afci.trajet.dto.om.OrdreMissionResponse;
import com.afci.trajet.dto.om.OrdreMissionUpdateRequest;
import com.afci.trajet.dto.om.ecole.OmFormateurCandidatResponse;
import com.afci.trajet.entity.Ecole;
import com.afci.trajet.entity.Formateur;
import com.afci.trajet.entity.OrdreMission;
import com.afci.trajet.entity.Utilisateur;
import com.afci.trajet.mapper.OrdreMissionMapper;
import com.afci.trajet.repository.EcoleRepository;
import com.afci.trajet.repository.FormateurRepository;
import com.afci.trajet.repository.OrdreMissionRepository;
import com.afci.trajet.repository.UtilisateurRepository;
import com.afci.trajet.util.GeoUtils;

/**
 * Implémentation du service métier pour la gestion des Ordres de Mission.
 *
 * Scénario côté ECOLE :
 *  - l'école connectée crée un OM en BROUILLON (sans formateur) ;
 *  - elle peut modifier cet OM tant qu'il est en BROUILLON ;
 *  - elle peut ensuite affecter un formateur, ce qui fait passer le statut en PROPOSE ;
 *  - elle peut lister ses OM, filtrer par statut, consulter le détail, etc.
 */
@Service
@Transactional
public class OrdreMissionServiceImpl implements OrdreMissionService {

    private final OrdreMissionRepository ordreMissionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EcoleRepository ecoleRepository;
    private final FormateurRepository formateurRepository;

    public OrdreMissionServiceImpl(OrdreMissionRepository ordreMissionRepository,
                                   UtilisateurRepository utilisateurRepository,
                                   EcoleRepository ecoleRepository,
                                   FormateurRepository formateurRepository) {
        this.ordreMissionRepository = ordreMissionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.ecoleRepository = ecoleRepository;
        this.formateurRepository = formateurRepository;
    }

    // ---------------------------------------------------------------------
    // 1️⃣ Création d'un OM en BROUILLON par une ECOLE
    // ---------------------------------------------------------------------

    @Override
    public OrdreMissionResponse createOrdreMissionPourEcole(String emailConnected,
                                                            OrdreMissionCreateRequest request) {

        // 1) Sécurisation / contexte : retrouver l'utilisateur par email
        Utilisateur user = utilisateurRepository.findByEmailIgnoreCase(emailConnected)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable pour l'email : " + emailConnected));

        // 2) Retrouver l'école associée à cet utilisateur (FK id_user)
        Ecole ecole = ecoleRepository.findByIdUser(user.getIdUser())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucune école associée à ce compte. Contactez un administrateur."));

        // 3) Validation simple des dates
        validateDates(request.getDateDebut(), request.getDateFin());

        // 4) Mapper le DTO de création vers une entité OM
        OrdreMission om = OrdreMissionMapper.toEntityFromCreate(request);

        // 5) Compléter les champs contextuels côté service
        om.setIdEcole(ecole.getIdEcole());
        om.setIdUserCreateur(user.getIdUser());

        // Au moment de la création côté ECOLE :
        //  - on n'a PAS encore de formateur affecté
        om.setIdFormateur(null);

        // Statut initial : BROUILLON (automate métier)
        om.setStatut("BROUILLON");

        // Générer un code ordre fonctionnel (ex : OM-2025-0001)
        String code = generateCodeOrdre(ecole);
        om.setCodeOrdre(code);

        // Timestamps
        OffsetDateTime now = OffsetDateTime.now();
        om.setCreatedAt(now);
        om.setUpdatedAt(now);

        // 6) Sauvegarder
        ordreMissionRepository.save(om);

        // 7) Retourner le DTO de réponse
        return OrdreMissionMapper.toResponse(om);
    }

    // ---------------------------------------------------------------------
    // 2️⃣ Mise à jour d'un OM BROUILLON par l'ECOLE
    // ---------------------------------------------------------------------

    @Override
    public OrdreMissionResponse updateOrdreMissionBrouillonPourEcole(String emailConnected,
                                                                     Integer idOrdreMission,
                                                                     OrdreMissionUpdateRequest request) {

        // 1) Contexte utilisateur + école
        Utilisateur user = utilisateurRepository.findByEmailIgnoreCase(emailConnected)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable pour l'email : " + emailConnected));

        Ecole ecole = ecoleRepository.findByIdUser(user.getIdUser())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucune école associée à ce compte. Contactez un administrateur."));

        // 2) Retrouver l'OM et vérifier qu'il appartient bien à cette école
        OrdreMission om = ordreMissionRepository.findById(idOrdreMission)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ordre de mission introuvable (id=" + idOrdreMission + ")."));

        if (!om.getIdEcole().equals(ecole.getIdEcole())) {
            throw new IllegalArgumentException(
                    "Cet ordre de mission n'appartient pas à votre établissement.");
        }

        // 3) Vérifier que l'OM est encore en BROUILLON
        if (!"BROUILLON".equalsIgnoreCase(om.getStatut())) {
            throw new IllegalStateException(
                    "Seuls les ordres de mission en statut BROUILLON peuvent être modifiés.");
        }

        // 4) Validation des dates
        validateDates(request.getDateDebut(), request.getDateFin());

        // 5) Appliquer la mise à jour à l'entité
        OrdreMissionMapper.updateEntityFromDto(request, om);
        om.setUpdatedAt(OffsetDateTime.now());

        // 6) Sauvegarder
        ordreMissionRepository.save(om);

        // 7) Retourner le DTO
        return OrdreMissionMapper.toResponse(om);
    }

    // ---------------------------------------------------------------------
    // 3️⃣ Liste paginée des OM de l'école (optionnellement filtrés par statut)
    // ---------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrdreMissionResponse> listerOrdresMissionPourEcole(String emailConnected,
                                                                           String statut,
                                                                           int page,
                                                                           int size) {

        // 1) Contexte utilisateur + école
        Utilisateur user = utilisateurRepository.findByEmailIgnoreCase(emailConnected)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable pour l'email : " + emailConnected));

        Ecole ecole = ecoleRepository.findByIdUser(user.getIdUser())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucune école associée à ce compte. Contactez un administrateur."));

        Integer idEcole = ecole.getIdEcole();

        // 2) Préparation du pageable avec tri par createdAt desc
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 3) Chargement de la page d'OM (avec ou sans filtre statut)
        Page<OrdreMission> pageOm;
        if (statut != null && !statut.isBlank()) {
            String statutFiltre = statut.trim().toUpperCase(Locale.ROOT);
            pageOm = ordreMissionRepository.findByIdEcoleAndStatut(idEcole, statutFiltre, pageable);
        } else {
            pageOm = ordreMissionRepository.findByIdEcole(idEcole, pageable);
        }

        // 4) Mapping entités -> DTO
        List<OrdreMissionResponse> items =
                OrdreMissionMapper.toResponseList(pageOm.getContent());

        // 5) Construction du DTO de pagination
        return new PageResponse<>(
                items,
                pageOm.getNumber(),
                pageOm.getSize(),
                pageOm.getTotalElements(),
                pageOm.getTotalPages(),
                pageOm.isFirst(),
                pageOm.isLast()
        );
    }

    // ---------------------------------------------------------------------
    // 4️⃣ Détail d'un OM (côté ECOLE)
    // ---------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public OrdreMissionResponse getOrdreMissionDetailPourEcole(String emailConnected,
                                                               Integer idOrdreMission) {

        // 1) Contexte utilisateur + école
        Utilisateur user = utilisateurRepository.findByEmailIgnoreCase(emailConnected)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable pour l'email : " + emailConnected));

        Ecole ecole = ecoleRepository.findByIdUser(user.getIdUser())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucune école associée à ce compte. Contactez un administrateur."));

        // 2) Charger l'OM
        OrdreMission om = ordreMissionRepository.findById(idOrdreMission)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ordre de mission introuvable (id=" + idOrdreMission + ")."));

        // 3) Vérifier l'appartenance à l'école
        if (!om.getIdEcole().equals(ecole.getIdEcole())) {
            throw new IllegalArgumentException(
                    "Cet ordre de mission n'appartient pas à votre établissement.");
        }

        return OrdreMissionMapper.toResponse(om);
    }

    // ---------------------------------------------------------------------
    // 5️⃣ Affectation d'un formateur par l'ECOLE (BROUILLON -> PROPOSE)
    //     (version avec contexte email)
    // ---------------------------------------------------------------------

    @Override
    public OrdreMissionResponse assignerFormateurPourOrdreMission(String emailConnected,
                                                                  Integer idOrdreMission,
                                                                  OrdreMissionAssignFormateurRequest request) {

        // 1) Contexte utilisateur + école
        Utilisateur user = utilisateurRepository.findByEmailIgnoreCase(emailConnected)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Utilisateur introuvable pour l'email : " + emailConnected));

        Ecole ecole = ecoleRepository.findByIdUser(user.getIdUser())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucune école associée à ce compte. Contactez un administrateur."));

        // 2) Charger l'OM
        OrdreMission om = ordreMissionRepository.findById(idOrdreMission)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ordre de mission introuvable (id=" + idOrdreMission + ")."));

        // 3) Contrôler l'appartenance à l'école
        if (!om.getIdEcole().equals(ecole.getIdEcole())) {
            throw new IllegalArgumentException(
                    "Cet ordre de mission n'appartient pas à votre établissement.");
        }

        // 4) Vérifier le statut (seul un BROUILLON peut être proposé)
        if (!"BROUILLON".equalsIgnoreCase(om.getStatut())) {
            throw new IllegalStateException(
                    "Seuls les ordres de mission en BROUILLON peuvent être affectés à un formateur.");
        }

        // 5) Vérifier que le formateur existe
        Integer idFormateur = request.getIdFormateur();
        if (idFormateur == null) {
            throw new IllegalArgumentException("L'id du formateur est obligatoire.");
        }

        Formateur formateur = formateurRepository.findById(idFormateur)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formateur introuvable (id=" + idFormateur + ")."));

        // 6) Appliquer l'affectation
        om.setIdFormateur(formateur.getIdFormateur());
        om.setStatut("PROPOSE");
        om.setUpdatedAt(OffsetDateTime.now());

        ordreMissionRepository.save(om);

        return OrdreMissionMapper.toResponse(om);
    }

    // ---------------------------------------------------------------------
    // 6️⃣ Candidats formateurs pour un OM (triés par distance)
    // ---------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<OmFormateurCandidatResponse> findCandidatsPourOrdre(Integer idOrdreMission) {

        OrdreMission om = ordreMissionRepository.findById(idOrdreMission)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ordre de mission introuvable (id=" + idOrdreMission + ")"));

        // 📍 Point de référence pour la distance :
        // 👉 Dans ton modèle actuel, c'est TOUJOURS l'école :
        //    - Ecole.lat / Ecole.lon (BigDecimal)
        Ecole ecole = ecoleRepository.findById(om.getIdEcole())
                .orElseThrow(() -> new IllegalStateException(
                        "École introuvable pour l'OM (id_ecole=" + om.getIdEcole() + ")"));

        if (ecole.getLat() == null || ecole.getLon() == null) {
            throw new IllegalStateException(
                    "Les coordonnées GPS de l'établissement ne sont pas renseignées.");
        }

        BigDecimal omLat = ecole.getLat();
        BigDecimal omLon = ecole.getLon();

        // 🔍 On récupère tous les formateurs (tu pourras plus tard optimiser
        // avec une méthode custom du repository : findAllActifsAvecCoordonnees(), etc.)
        List<Formateur> formateurs = formateurRepository.findAll();

        List<OmFormateurCandidatResponse> candidats = formateurs.stream()
                .map(f -> {
                    Utilisateur u = utilisateurRepository.findById(f.getIdUser())
                            .orElse(null);

                    if (u == null || !u.isActif()
                            || u.getLat() == null || u.getLon() == null) {
                        return null;
                    }

                    double distance = GeoUtils.distanceKm(
                            omLat, omLon,
                            u.getLat(), u.getLon()
                    );

                    OmFormateurCandidatResponse dto = new OmFormateurCandidatResponse();
                    dto.setIdFormateur(f.getIdFormateur());
                    dto.setIdUser(u.getIdUser());
                    dto.setPrenom(u.getPrenom());
                    dto.setNom(u.getNom());
                    dto.setVille(u.getVille());
                    dto.setZoneKm(f.getZoneKm());
                    dto.setVehiculePerso(f.isVehiculePerso());
                    dto.setPermis(f.isPermis());
                    dto.setCommentaire(f.getCommentaire());
                    dto.setDistanceKm(distance);

                    return dto;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(OmFormateurCandidatResponse::getDistanceKm))
                .toList();

        return candidats;
    }

    // ---------------------------------------------------------------------
    // 7️⃣ Affectation directe d'un formateur (sans email contexte)
    // ---------------------------------------------------------------------

    @Override
    public OrdreMissionResponse affecterFormateur(Integer idOrdreMission, Integer idFormateur) {

        OrdreMission om = ordreMissionRepository.findById(idOrdreMission)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Ordre de mission introuvable (id=" + idOrdreMission + ")"));

        if (!"BROUILLON".equalsIgnoreCase(om.getStatut())) {
            throw new IllegalStateException(
                    "Seuls les ordres de mission en statut BROUILLON peuvent être affectés.");
        }

        Formateur formateur = formateurRepository.findById(idFormateur)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formateur introuvable (id=" + idFormateur + ")"));

        Utilisateur u = utilisateurRepository.findById(formateur.getIdUser())
                .orElseThrow(() -> new IllegalStateException(
                        "Utilisateur introuvable pour ce formateur (id_user=" + formateur.getIdUser() + ")"));

        if (!u.isActif()) {
            throw new IllegalStateException("Ce formateur est désactivé.");
        }

        // ping-pong Ecole → Formateur : PROPOSE
        om.setIdFormateur(formateur.getIdFormateur());
        om.setStatut("PROPOSE");
        om.setUpdatedAt(OffsetDateTime.now());

        ordreMissionRepository.save(om);

        return OrdreMissionMapper.toResponse(om);
    }

    // ---------------------------------------------------------------------
    // Helpers internes
    // ---------------------------------------------------------------------

    private void validateDates(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires.");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début.");
        }
    }

    private String generateCodeOrdre(Ecole ecole) {
        int year = LocalDate.now().getYear();
        long count = ordreMissionRepository.count() + 1; // simpliste

        return String.format("OM-%d-%04d", year, count);
    }
}

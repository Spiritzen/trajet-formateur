package com.afci.trajet.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.afci.trajet.entity.OrdreMission;

/**
 * Repository JPA pour la table ORDRE_MISSION.
 *
 * On organise les méthodes par contexte d'usage :
 *
 *  - COMMUN :
 *      * findByCodeOrdre(...)
 *
 *  - Côté ECOLE :
 *      * findByIdEcoleOrderByCreatedAtDesc(...)
 *      * findByIdEcoleAndStatutOrderByCreatedAtDesc(...)
 *      * findByIdOrdreMissionAndIdEcole(...)
 *
 *  - Côté FORMATEUR :
 *      * findByIdFormateurOrderByCreatedAtDesc(...)
 *      * findByIdFormateurAndStatutInOrderByCreatedAtDesc(...)
 *
 *  - Planning / Admin :
 *      * findByDateDebutBetween(...)
 *      * findByStatutOrderByCreatedAtDesc(...)
 *      * findTop20ByOrderByCreatedAtDesc()
 */
public interface OrdreMissionRepository extends JpaRepository<OrdreMission, Integer> {

    // ---------------------------------------------------------------------
    // COMMUN
    // ---------------------------------------------------------------------

    /**
     * Retrouve un ordre de mission via son code fonctionnel (OM-2025-0001, ...).
     */
    Optional<OrdreMission> findByCodeOrdre(String codeOrdre);

    // ---------------------------------------------------------------------
    // CÔTÉ ECOLE
    // ---------------------------------------------------------------------

    /**
     * Liste tous les ordres de mission d'une école (tous statuts confondus),
     * du plus récent au plus ancien.
     *
     * Utilisé par l'onglet "OM" côté ECOLE pour afficher le tableau principal.
     */
    List<OrdreMission> findByIdEcoleOrderByCreatedAtDesc(Integer idEcole);

    /**
     * Liste les ordres de mission d'une école filtrés par statut
     * (BROUILLON, PROPOSE, EN_ATTENTE_VALIDATION, VALIDE, SIGNE, CLOTURE, REJETE),
     * du plus récent au plus ancien.
     *
     * Idéal pour un filtre par statut dans l'UI (menu déroulant, onglets...).
     */
    List<OrdreMission> findByIdEcoleAndStatutOrderByCreatedAtDesc(Integer idEcole, String statut);

    /**
     * Récupère un OM en garantissant qu'il appartient à une école donnée.
     *
     * Très utile côté service pour sécuriser l'accès :
     *  - l'école connectée ne peut toucher qu'à ses propres OM.
     */
    Optional<OrdreMission> findByIdOrdreMissionAndIdEcole(Integer idOrdreMission, Integer idEcole);

    // ---------------------------------------------------------------------
    // CÔTÉ FORMATEUR
    // ---------------------------------------------------------------------

    /**
     * Liste tous les ordres de mission affectés à un formateur,
     * du plus récent au plus ancien.
     */
    List<OrdreMission> findByIdFormateurOrderByCreatedAtDesc(Integer idFormateur);

    /**
     * Liste les ordres de mission d'un formateur parmi une liste de statuts
     * (par ex. EN_ATTENTE_VALIDATION, PROPOSE, SIGNE, ...),
     * du plus récent au plus ancien.
     *
     * Servira pour le tableau des OM coté FORMATEUR avec filtres.
     */
    List<OrdreMission> findByIdFormateurAndStatutInOrderByCreatedAtDesc(
            Integer idFormateur,
            Collection<String> statuts
    );

    // ---------------------------------------------------------------------
    // PLANNING / ADMIN
    // ---------------------------------------------------------------------

    /**
     * Ordres de mission sur une plage de dates.
     * Utile pour un “planning” ou un filtre par période globale.
     */
    List<OrdreMission> findByDateDebutBetween(LocalDate dateDebutMin, LocalDate dateDebutMax);

    /**
     * Liste globale (toutes écoles / formateurs) des OM d'un statut donné,
     * du plus récent au plus ancien.
     *
     * Potentiellement utile côté ADMIN (vue globale par statut).
     */
    List<OrdreMission> findByStatutOrderByCreatedAtDesc(String statut);

    /**
     * Les 20 derniers ordres créés, triés du plus récent au plus ancien.
     *
     * Pratique pour une page d'accueil Admin ou un widget de monitoring.
     */
    List<OrdreMission> findTop20ByOrderByCreatedAtDesc();
    

    Page<OrdreMission> findByIdEcole(Integer idEcole, Pageable pageable);

    Page<OrdreMission> findByIdEcoleAndStatut(Integer idEcole, String statut, Pageable pageable);
}

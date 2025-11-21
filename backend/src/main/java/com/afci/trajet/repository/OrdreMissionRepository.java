package com.afci.trajet.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afci.trajet.entity.OrdreMission;

/**
 * Repository JPA pour la table ORDRE_MISSION.
 *
 * Ici on utilise des méthodes dérivées des noms :
 *  - findByCodeOrdre(...)
 *  - findByIdFormateurAndStatutIn(...)
 *  etc.
 *
 * Chaque méthode servira très vite pour nos futurs endpoints REST.
 */
public interface OrdreMissionRepository extends JpaRepository<OrdreMission, Integer> {

    /**
     * Retrouve un ordre de mission via son code fonctionnel (OM-2025-0001, ...).
     */
    Optional<OrdreMission> findByCodeOrdre(String codeOrdre);

    /**
     * Liste les ordres de mission d'un formateur, tous statuts confondus.
     */
    List<OrdreMission> findByIdFormateur(Integer idFormateur);

    /**
     * Liste les ordres de mission pour une école donnée.
     */
    List<OrdreMission> findByIdEcole(Integer idEcole);

    /**
     * Liste les ordres de mission filtrés par statut (BROUILLON, VALIDE, SIGNE, ...).
     */
    List<OrdreMission> findByStatut(String statut);

    /**
     * Ordres de mission d'un formateur, parmi une liste de statuts (par ex. EN_ATTENTE_VALIDATION, PROPOSE, ...).
     */
    List<OrdreMission> findByIdFormateurAndStatutIn(Integer idFormateur, Collection<String> statuts);

    /**
     * Ordres de mission sur une plage de dates.
     * Utile pour un “planning” ou un filtre par période.
     */
    List<OrdreMission> findByDateDebutBetween(LocalDate dateDebutMin, LocalDate dateDebutMax);

    /**
     * Les derniers ordres créés, triés du plus récent au plus ancien.
     * Pratique pour une page d'accueil admin.
     */
    List<OrdreMission> findTop20ByOrderByCreatedAtDesc();
}

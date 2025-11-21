package com.afci.trajet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afci.trajet.entity.Trajet;

/**
 * Repository JPA pour la table TRAJET.
 *
 * Un trajet est rattaché à un ORDRE_MISSION via id_ordre_mission.
 * On ne mappe pas (pour l'instant) les relations avec @ManyToOne :
 * on reste sur une approche simple basée sur les IDs.
 */
public interface TrajetRepository extends JpaRepository<Trajet, Integer> {

    /**
     * Tous les trajets proposés pour un ordre de mission donné.
     */
    List<Trajet> findByIdOrdreMission(Integer idOrdreMission);

    /**
     * Le trajet retenu (retenu = TRUE) pour un ordre de mission.
     * D'après le modèle, il ne devrait y en avoir au maximum qu'un.
     */
    Optional<Trajet> findByIdOrdreMissionAndRetenuTrue(Integer idOrdreMission);

    /**
     * Tous les trajets marqués comme retenus.
     * Utile pour des stats globales (coûts, distances, etc.).
     */
    List<Trajet> findByRetenuTrue();

    /**
     * Tous les trajets d'un certain type de moyen principal (TRAIN, VOITURE, ...).
     * On laisse la responsabilité de passer une valeur cohérente (déjà uppercased).
     */
    List<Trajet> findByMoyenPrincipal(String moyenPrincipal);
}

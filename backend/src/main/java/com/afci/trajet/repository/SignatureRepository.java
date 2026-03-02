package com.afci.trajet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afci.trajet.entity.Signature;

/**
 * Repository JPA pour la table SIGNATURE.
 *
 * Une signature est rattachée à un ordre de mission (id_ordre_mission)
 * et à un signataire (id_signataire).
 */
public interface SignatureRepository extends JpaRepository<Signature, Integer> {

    /**
     * Signature associée à un ordre de mission.
     * D'après le modèle, on vise 0 ou 1 ligne par OM.
     */
    Optional<Signature> findByIdOrdreMission(Integer idOrdreMission);

    /**
     * Toutes les signatures d'un utilisateur signataire donné.
     * Intéressant si on a plusieurs validateurs potentiels.
     */
    List<Signature> findByIdSignataire(Integer idSignataire);

    /**
     * Signatures filtrées par état (EN_ATTENTE, SIGNE, REFUSE).
     */
    List<Signature> findByEtat(String etat);
}

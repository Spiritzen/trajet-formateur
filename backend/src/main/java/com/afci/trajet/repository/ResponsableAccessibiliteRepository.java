// package à adapter si besoin, par ex. com.afci.trajet.repository;
package com.afci.trajet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.afci.trajet.entity.ResponsableAccessibilite;

/**
 * Repository pour l'entité ResponsableAccessibilite.
 *
 * Rôle : accéder à la table responsable_accessibilite
 * (lecture / écriture).
 *
 * On part sur la règle métier : 0 ou 1 responsable par école.
 */
@Repository
public interface ResponsableAccessibiliteRepository extends JpaRepository<ResponsableAccessibilite, Integer> {

    /**
     * Récupère, si présent, le responsable accessibilité
     * pour une école donnée (id_ecole).
     *
     * Règle métier : une école a au plus 1 responsable.
     */
    Optional<ResponsableAccessibilite> findByIdEcole(Integer idEcole);
}

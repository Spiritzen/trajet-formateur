package com.afci.trajet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.afci.trajet.entity.Ecole;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité Ecole.
 *
 * - Hérite de JpaRepository<Ecole, Integer> :
 *   => fournit automatiquement toutes les opérations CRUD basiques
 *      (save, findById, findAll, deleteById, etc.).
 *
 * - Contient des méthodes de recherche spécifiques
 *   (ville, code postal, combinaison des deux, etc.).
 */
@Repository
public interface EcoleRepository extends JpaRepository<Ecole, Integer> {

    // Recherche contenant (ex: "ami" match "Amiens")
    List<Ecole> findByVilleIgnoreCaseContaining(String ville);

    // Recherche par début de code postal (ex: "80" → 80000, 80100, etc.)
    List<Ecole> findByCodePostalStartingWith(String codePostal);

    // Combiner les deux (ville + code postal)
    List<Ecole> findByVilleIgnoreCaseContainingAndCodePostalStartingWith(
            String ville,
            String codePostal
    );

    /**
     * Retrouve l'école liée à un compte utilisateur ECOLE
     * via la FK id_user sur la table ecole.
     */
    Optional<Ecole> findByIdUser(Integer idUser);
}

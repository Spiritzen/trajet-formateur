// src/main/java/com/afci/trajet/repository/FormateurRepository.java
package com.afci.trajet.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.afci.trajet.dto.formateur.admin.AdminFormateurListItemResponse;
import com.afci.trajet.entity.Formateur;

/**
 * Repository JPA pour l'entité Formateur.
 *
 * CRUD de base :
 *  - findById(id)
 *  - existsById(id)
 *  - findAll()
 *
 * Méthodes avancées :
 *  - searchFormateursForAdmin(...) pour la recherche paginée côté ADMIN.
 *  - findByIdUser(...) pour "Mon compte" formateur.
 */
@Repository
public interface FormateurRepository extends JpaRepository<Formateur, Integer> {

    /**
     * Recherche paginée des formateurs pour l'ADMIN.
     *
     * 🔥 On renvoie directement le DTO AdminFormateurListItemResponse
     *     via une projection JPQL, avec un JOIN sur Utilisateur.
     *
     * Filtre sur :
     *  - nom / prénom / email / ville (UTILISATEUR)
     *  - commentaire (FORMATEUR)
     */
    @Query("""
        SELECT new com.afci.trajet.dto.formateur.admin.AdminFormateurListItemResponse(
            f.idFormateur,
            u.idUser,
            u.email,
            u.prenom,
            u.nom,
            u.telephone,
            f.zoneKm,
            f.vehiculePerso,
            f.permis,
            f.commentaire,
            u.ville
        )
        FROM Formateur f
        JOIN Utilisateur u ON f.idUser = u.idUser
        WHERE (
            :search IS NULL
            OR :search = ''
            OR LOWER(u.nom)        LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.prenom)     LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.email)      LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(COALESCE(u.ville, '')) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(COALESCE(f.commentaire, '')) LIKE LOWER(CONCAT('%', :search, '%'))
        )
        ORDER BY u.nom ASC, u.prenom ASC
    """)
    Page<AdminFormateurListItemResponse> searchFormateursForAdmin(
            @Param("search") String search,
            Pageable pageable
    );

    /**
     * Recherche du formateur à partir de l'identifiant utilisateur.
     *
     * Utilisé côté "Mon compte" formateur :
     *  - on part de l'id_user du JWT
     *  - on retrouve la fiche formateur associée.
     */
    Optional<Formateur> findByIdUser(Integer idUser);
    
    
    @Query("""
            SELECT f
            FROM Formateur f
            JOIN Utilisateur u ON f.idUser = u.idUser
            WHERE u.actif = true
              AND u.lat IS NOT NULL
              AND u.lon IS NOT NULL
        """)
        java.util.List<Formateur> findAllActifsAvecCoordonnees();
}

package com.afci.trajet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.afci.trajet.entity.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    /**
     * Recherche un utilisateur par email (insensible à la casse).
     * Utilisé par CustomUserDetailsService pour l’authentification
     * et par les services métier qui partent de l'email connecté.
     */
    Optional<Utilisateur> findByEmailIgnoreCase(String email);

    /**
     * Vérifie si un compte existe déjà avec cet email.
     * Très utile avant une création d'utilisateur.
     */
    boolean existsByEmail(String email);

    /**
     * Ajoute un rôle à un utilisateur dans la table pivot utilisateur_role.
     * Relation gérée "à la main", sans annotation @ManyToMany.
     */
    @Modifying
    @Query(
        value = "INSERT INTO utilisateur_role (id_user, id_role) VALUES (:userId, :roleId)",
        nativeQuery = true
    )
    void assignRoleToUser(Integer userId, Integer roleId);
}

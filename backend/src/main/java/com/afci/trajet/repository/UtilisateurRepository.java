package com.afci.trajet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afci.trajet.entity.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    // recherche par email (case insensitive si tu veux l'utiliser avec LOWER(email))
    Optional<Utilisateur> findByEmailIgnoreCase(String email);
}

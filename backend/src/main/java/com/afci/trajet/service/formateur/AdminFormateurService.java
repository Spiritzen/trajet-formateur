package com.afci.trajet.service.formateur;

import com.afci.trajet.dto.common.PageResponse;
import com.afci.trajet.dto.formateur.admin.AdminFormateurCreateRequest;
import com.afci.trajet.dto.formateur.admin.AdminFormateurDetailResponse;
import com.afci.trajet.dto.formateur.admin.AdminFormateurListItemResponse;
import com.afci.trajet.dto.formateur.admin.AdminFormateurUpdateRequest;

/**
 * Service métier dédié à la gestion des FORMATEURS
 * côté ADMIN.
 *
 * Ce service encapsule toute la logique :
 *  - création d'un formateur (UTILISATEUR + ROLE + FORMATEUR)
 *  - mise à jour des infos
 *  - soft delete (désactivation du compte)
 *  - listing paginé avec recherche.
 */
public interface AdminFormateurService {

    /**
     * Liste paginée des formateurs, avec recherche texte simple
     * (nom / prénom / email).
     */
    PageResponse<AdminFormateurListItemResponse> listFormateurs(
            String search,
            int page,
            int size
    );

    /**
     * Récupération du détail complet d'un formateur
     * (UTILISATEUR + FORMATEUR).
     */
    AdminFormateurDetailResponse getFormateurDetail(Integer idFormateur);

    /**
     * Création d'un nouveau formateur côté ADMIN.
     *
     * Étapes (impl) :
     *  - vérifier l'unicité de l'email
     *  - créer l'utilisateur avec un mot de passe temporaire
     *  - associer le rôle FORMATEUR
     *  - créer l'entrée FORMATEUR
     */
    AdminFormateurDetailResponse createFormateur(AdminFormateurCreateRequest request);

    /**
     * Mise à jour des infos utilisateur + formateur.
     */
    AdminFormateurDetailResponse updateFormateur(Integer idFormateur,
                                                 AdminFormateurUpdateRequest request);

    /**
     * Suppression "soft" d'un formateur :
     *  - on désactive le compte (actif = false)
     *  - on remplit deleted_at
     *  - on garde les données pour l'historique.
     */
    void softDeleteFormateur(Integer idFormateur);
}

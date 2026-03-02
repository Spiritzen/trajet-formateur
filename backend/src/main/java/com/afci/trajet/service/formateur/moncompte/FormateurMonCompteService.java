// src/main/java/com/afci/trajet/service/formateur/moncompte/FormateurMonCompteService.java
package com.afci.trajet.service.formateur.moncompte;

import com.afci.trajet.dto.formateur.moncompte.ChangePasswordRequest;
import com.afci.trajet.dto.formateur.moncompte.FormateurProfileResponse;
import com.afci.trajet.dto.formateur.moncompte.FormateurProfileUpdateRequest;

/**
 * Service métier pour la gestion du "Mon compte" côté FORMATEUR.
 *
 * Toutes les méthodes partent de l'id_user du formateur connecté.
 */
public interface FormateurMonCompteService {

    /**
     * Récupérer le profil complet du formateur (utilisateur + formateur)
     * à partir de l'id_user.
     */
    FormateurProfileResponse getProfilByUserId(Integer idUser);

    /**
     * Mise à jour des infos du formateur (téléphone, adresse, zone, etc.).
     * Le geocoding est déclenché automatiquement si l'adresse change.
     */
    FormateurProfileResponse updateProfilByUserId(Integer idUser,
                                                  FormateurProfileUpdateRequest request);

    /**
     * Changement de mot de passe du formateur.
     */
    void changePassword(Integer idUser, ChangePasswordRequest request);
}

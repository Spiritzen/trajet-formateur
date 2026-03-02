package com.afci.trajet.dto.ecole;

/**
 * DTO pour la mise à jour du PROFIL utilisateur ECOLE
 * (compte de connexion de l'établissement).
 *
 * Exemples de champs :
 *  - prenom
 *  - nom
 *  - telephone
 *
 * ⚠ L'email n'est pas modifié ici : il sert d'identifiant de connexion.
 */
public class MonProfilUpdateRequest {

    private String prenom;
    private String nom;
    private String telephone;

    public MonProfilUpdateRequest() {
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}

package com.afci.trajet.dto.ecole;

/**
 * DTO pour le changement de mot de passe
 * par l'utilisateur ECOLE.
 *
 * On demande :
 *  - ancienMotDePasse : pour vérifier que c'est bien le titulaire
 *  - nouveauMotDePasse
 *  - confirmationMotDePasse : pour éviter les fautes de frappe
 */
public class MonMotDePasseUpdateRequest {

    private String ancienMotDePasse;
    private String nouveauMotDePasse;
    private String confirmationMotDePasse;

    public MonMotDePasseUpdateRequest() {
    }

    public String getAncienMotDePasse() {
        return ancienMotDePasse;
    }

    public void setAncienMotDePasse(String ancienMotDePasse) {
        this.ancienMotDePasse = ancienMotDePasse;
    }

    public String getNouveauMotDePasse() {
        return nouveauMotDePasse;
    }

    public void setNouveauMotDePasse(String nouveauMotDePasse) {
        this.nouveauMotDePasse = nouveauMotDePasse;
    }

    public String getConfirmationMotDePasse() {
        return confirmationMotDePasse;
    }

    public void setConfirmationMotDePasse(String confirmationMotDePasse) {
        this.confirmationMotDePasse = confirmationMotDePasse;
    }
}

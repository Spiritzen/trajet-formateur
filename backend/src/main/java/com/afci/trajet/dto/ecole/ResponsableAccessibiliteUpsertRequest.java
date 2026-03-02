// package à adapter, par ex. com.afci.trajet.dto.ecole;
package com.afci.trajet.dto.ecole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour créer / mettre à jour le responsable accessibilité
 * côté école (MonEcole.jsx).
 *
 * On ne passe pas idEcole ici : on le déduit de l'utilisateur connecté.
 */
public class ResponsableAccessibiliteUpsertRequest {

    @NotBlank
    @Size(max = 100)
    private String nom;

    @NotBlank
    @Size(max = 100)
    private String prenom;

    @Size(max = 100)
    private String fonction;

    @Size(max = 20)
    private String telephone;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 100)
    private String plageHoraire;

    public ResponsableAccessibiliteUpsertRequest() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlageHoraire() {
        return plageHoraire;
    }

    public void setPlageHoraire(String plageHoraire) {
        this.plageHoraire = plageHoraire;
    }
}

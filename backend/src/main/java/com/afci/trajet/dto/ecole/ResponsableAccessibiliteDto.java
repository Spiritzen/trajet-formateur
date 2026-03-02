// package à adapter, par ex. com.afci.trajet.dto.ecole;
package com.afci.trajet.dto.ecole;

/**
 * DTO "flat" pour exposer le responsable accessibilité
 * côté API (lecture).
 *
 * Ce DTO sera utilisé :
 *  - dans GET /api/ecole/mon-responsable-accessibilite,
 *  - éventuellement imbriqué dans MonEtablissementResponse,
 *  - plus tard côté formateur (lecture seule).
 */
public class ResponsableAccessibiliteDto {

    private Integer idResponsable;
    private String nom;
    private String prenom;
    private String fonction;
    private String telephone;
    private String email;
    private String plageHoraire;

    public ResponsableAccessibiliteDto() {
    }

    public ResponsableAccessibiliteDto(Integer idResponsable,
                                       String nom,
                                       String prenom,
                                       String fonction,
                                       String telephone,
                                       String email,
                                       String plageHoraire) {
        this.idResponsable = idResponsable;
        this.nom = nom;
        this.prenom = prenom;
        this.fonction = fonction;
        this.telephone = telephone;
        this.email = email;
        this.plageHoraire = plageHoraire;
    }

    public Integer getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(Integer idResponsable) {
        this.idResponsable = idResponsable;
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

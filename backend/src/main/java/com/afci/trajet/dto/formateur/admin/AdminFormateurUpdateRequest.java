package com.afci.trajet.dto.formateur.admin;

import java.io.Serializable;

/**
 * Payload mise à jour formateur côté ADMIN.
 *
 * Tous les champs sont optionnels : on met à jour seulement ce qui est non null / non blanc.
 */
public class AdminFormateurUpdateRequest implements Serializable {

    // Utilisateur
    private String email;
    private String prenom;
    private String nom;
    private String telephone;

    private String adresseL1;
    private String adresseL2;
    private String codePostal;
    private String ville;
    private String paysCode;

    // Formateur
    private Short zoneKm;
    private Boolean vehiculePerso;
    private Boolean permis;
    private String commentaire;
    private String mobilitePrefJson;
    private String disponibiliteJson;

    public AdminFormateurUpdateRequest() {
    }

    // full-args optionnel si tu veux

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAdresseL1() {
        return adresseL1;
    }

    public void setAdresseL1(String adresseL1) {
        this.adresseL1 = adresseL1;
    }

    public String getAdresseL2() {
        return adresseL2;
    }

    public void setAdresseL2(String adresseL2) {
        this.adresseL2 = adresseL2;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPaysCode() {
        return paysCode;
    }

    public void setPaysCode(String paysCode) {
        this.paysCode = paysCode;
    }

    public Short getZoneKm() {
        return zoneKm;
    }

    public void setZoneKm(Short zoneKm) {
        this.zoneKm = zoneKm;
    }

    public Boolean getVehiculePerso() {
        return vehiculePerso;
    }

    public void setVehiculePerso(Boolean vehiculePerso) {
        this.vehiculePerso = vehiculePerso;
    }

    public Boolean getPermis() {
        return permis;
    }

    public void setPermis(Boolean permis) {
        this.permis = permis;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getMobilitePrefJson() {
        return mobilitePrefJson;
    }

    public void setMobilitePrefJson(String mobilitePrefJson) {
        this.mobilitePrefJson = mobilitePrefJson;
    }

    public String getDisponibiliteJson() {
        return disponibiliteJson;
    }

    public void setDisponibiliteJson(String disponibiliteJson) {
        this.disponibiliteJson = disponibiliteJson;
    }
}

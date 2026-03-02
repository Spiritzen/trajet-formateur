// src/main/java/com/afci/trajet/dto/formateur/admin/AdminFormateurListItemResponse.java
package com.afci.trajet.dto.formateur.admin;

/**
 * DTO pour la liste des formateurs côté ADMIN.
 *
 * Utilisé dans la page :
 *  - /admin/formateurs (listing)
 *
 * Colonnes :
 *  - idFormateur
 *  - idUser
 *  - email
 *  - prenom
 *  - nom
 *  - telephone
 *  - zoneKm
 *  - vehiculePerso
 *  - permis
 *  - commentaire
 *  - ville
 */
public class AdminFormateurListItemResponse {

    private Integer idFormateur;
    private Integer idUser;

    private String email;
    private String prenom;
    private String nom;
    private String telephone;

    private Short zoneKm;
    private boolean vehiculePerso;
    private boolean permis;

    private String commentaire;
    private String ville;

    // =========================
    //  Constructeurs
    // =========================

    public AdminFormateurListItemResponse() {
        // constructeur vide requis par Jackson / Spring
    }

    public AdminFormateurListItemResponse(
            Integer idFormateur,
            Integer idUser,
            String email,
            String prenom,
            String nom,
            String telephone,
            Short zoneKm,
            boolean vehiculePerso,
            boolean permis,
            String commentaire,
            String ville
    ) {
        this.idFormateur = idFormateur;
        this.idUser = idUser;
        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
        this.zoneKm = zoneKm;
        this.vehiculePerso = vehiculePerso;
        this.permis = permis;
        this.commentaire = commentaire;
        this.ville = ville;
    }

    // =========================
    //  Getters / Setters
    // =========================

    public Integer getIdFormateur() {
        return idFormateur;
    }

    public void setIdFormateur(Integer idFormateur) {
        this.idFormateur = idFormateur;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

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

    public Short getZoneKm() {
        return zoneKm;
    }

    public void setZoneKm(Short zoneKm) {
        this.zoneKm = zoneKm;
    }

    public boolean isVehiculePerso() {
        return vehiculePerso;
    }

    public void setVehiculePerso(boolean vehiculePerso) {
        this.vehiculePerso = vehiculePerso;
    }

    public boolean isPermis() {
        return permis;
    }

    public void setPermis(boolean permis) {
        this.permis = permis;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }
}

// src/main/java/com/afci/trajet/dto/om/ecole/OmFormateurCandidatResponse.java
package com.afci.trajet.dto.om.ecole;

public class OmFormateurCandidatResponse {

    private Integer idFormateur;
    private Integer idUser;

    private String prenom;
    private String nom;
    private String ville;
    private Short zoneKm;
    private boolean vehiculePerso;
    private boolean permis;
    private String commentaire;

    private Double distanceKm;

    // --- Getters / Setters ---

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

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
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

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
}

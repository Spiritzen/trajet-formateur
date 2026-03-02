package com.afci.trajet.dto.formateur.admin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO détail complet d'un formateur côté ADMIN.
 *
 * Regroupe :
 *  - les infos Utilisateur (adresse, lat/lon, actif...)
 *  - les infos Formateur (zoneKm, dispo, mobilité, etc.)
 */
public class AdminFormateurDetailResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;

    // -------- Utilisateur --------
    private Integer idUser;
    private String prenom;
    private String nom;
    private String email;
    private String telephone;

    private String adresseL1;
    private String adresseL2;
    private String codePostal;
    private String ville;
    private String paysCode;

    private BigDecimal lat;
    private BigDecimal lon;

    private boolean actif;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;

    // -------- Formateur --------
    private Integer idFormateur;
    private Short zoneKm;
    private Boolean vehiculePerso;
    private Boolean permis;
    private String commentaire;
    private String mobilitePrefJson;
    private String disponibiliteJson;

    private OffsetDateTime createdAtFormateur;
    private OffsetDateTime updatedAtFormateur;

    public AdminFormateurDetailResponse() {
    }

    // (Si tu veux un full-args constructor, tu peux l'ajouter plus tard)

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getIdFormateur() {
        return idFormateur;
    }

    public void setIdFormateur(Integer idFormateur) {
        this.idFormateur = idFormateur;
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

    public OffsetDateTime getCreatedAtFormateur() {
        return createdAtFormateur;
    }

    public void setCreatedAtFormateur(OffsetDateTime createdAtFormateur) {
        this.createdAtFormateur = createdAtFormateur;
    }

    public OffsetDateTime getUpdatedAtFormateur() {
        return updatedAtFormateur;
    }

    public void setUpdatedAtFormateur(OffsetDateTime updatedAtFormateur) {
        this.updatedAtFormateur = updatedAtFormateur;
    }
}

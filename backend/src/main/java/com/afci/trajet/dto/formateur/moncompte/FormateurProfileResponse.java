// src/main/java/com/afci/trajet/dto/formateur/moncompte/FormateurProfileResponse.java
package com.afci.trajet.dto.formateur.moncompte;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO de retour pour le "Mon compte" côté FORMATEUR.
 *
 * Combine les infos de :
 *  - utilisateur
 *  - formateur
 *
 * ⚠️ On n'expose pas le password_hash évidemment.
 */
public class FormateurProfileResponse {

    // --- Partie UTILISATEUR ---

    /** Identifiant technique de l'utilisateur (table utilisateur.id_user). */
    private Integer idUser;

    /** Email de connexion (non modifiable par le formateur). */
    private String email;

    /** Prénom du formateur (non modifiable par le formateur). */
    private String prenom;

    /** Nom de famille du formateur (non modifiable par le formateur). */
    private String nom;

    /** Téléphone du formateur (modifiable). */
    private String telephone;

    /** Adresse ligne 1 (modifiable). */
    private String adresseL1;

    /** Adresse ligne 2 (modifiable). */
    private String adresseL2;

    /** Code postal (modifiable). */
    private String codePostal;

    /** Ville (modifiable). */
    private String ville;

    /** Code pays ISO2 (modifiable, ex : FR). */
    private String paysCode;

    /** Latitude géocodée de l'adresse (lecture seule). */
    private BigDecimal lat;

    /** Longitude géocodée de l'adresse (lecture seule). */
    private BigDecimal lon;

    /** Date de création du compte utilisateur. */
    private OffsetDateTime createdAtUser;

    /** Dernière mise à jour du compte utilisateur. */
    private OffsetDateTime updatedAtUser;

    // --- Partie FORMATEUR ---

    /** Identifiant technique du formateur (table formateur.id_formateur). */
    private Integer idFormateur;

    /** Zone kilométrique maximale (0–500 km). */
    private Short zoneKm;

    /** Possède un véhicule personnel ? */
    private boolean vehiculePerso;

    /** Détient un permis de conduire ? */
    private boolean permis;

    /** Préférences de mobilité (JSON sérialisé en String). */
    private String mobilitePrefJson;

    /** Disponibilités (JSON sérialisé en String). */
    private String disponibiliteJson;

    /** Commentaire libre saisi par l'admin / le formateur. */
    private String commentaire;

    /** Date de création de la fiche formateur. */
    private OffsetDateTime createdAtFormateur;

    /** Dernière mise à jour de la fiche formateur. */
    private OffsetDateTime updatedAtFormateur;

    // ----------------------------------------------------
    // Constructeurs
    // ----------------------------------------------------

    /** Constructeur sans argument (requis par Jackson). */
    public FormateurProfileResponse() {
    }

    /** Constructeur complet pratique si besoin. */
    public FormateurProfileResponse(
            Integer idUser,
            String email,
            String prenom,
            String nom,
            String telephone,
            String adresseL1,
            String adresseL2,
            String codePostal,
            String ville,
            String paysCode,
            BigDecimal lat,
            BigDecimal lon,
            OffsetDateTime createdAtUser,
            OffsetDateTime updatedAtUser,
            Integer idFormateur,
            Short zoneKm,
            boolean vehiculePerso,
            boolean permis,
            String mobilitePrefJson,
            String disponibiliteJson,
            String commentaire,
            OffsetDateTime createdAtFormateur,
            OffsetDateTime updatedAtFormateur
    ) {
        this.idUser = idUser;
        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
        this.adresseL1 = adresseL1;
        this.adresseL2 = adresseL2;
        this.codePostal = codePostal;
        this.ville = ville;
        this.paysCode = paysCode;
        this.lat = lat;
        this.lon = lon;
        this.createdAtUser = createdAtUser;
        this.updatedAtUser = updatedAtUser;
        this.idFormateur = idFormateur;
        this.zoneKm = zoneKm;
        this.vehiculePerso = vehiculePerso;
        this.permis = permis;
        this.mobilitePrefJson = mobilitePrefJson;
        this.disponibiliteJson = disponibiliteJson;
        this.commentaire = commentaire;
        this.createdAtFormateur = createdAtFormateur;
        this.updatedAtFormateur = updatedAtFormateur;
    }

    // ----------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------

    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresseL1() { return adresseL1; }
    public void setAdresseL1(String adresseL1) { this.adresseL1 = adresseL1; }

    public String getAdresseL2() { return adresseL2; }
    public void setAdresseL2(String adresseL2) { this.adresseL2 = adresseL2; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getPaysCode() { return paysCode; }
    public void setPaysCode(String paysCode) { this.paysCode = paysCode; }

    public BigDecimal getLat() { return lat; }
    public void setLat(BigDecimal lat) { this.lat = lat; }

    public BigDecimal getLon() { return lon; }
    public void setLon(BigDecimal lon) { this.lon = lon; }

    public OffsetDateTime getCreatedAtUser() { return createdAtUser; }
    public void setCreatedAtUser(OffsetDateTime createdAtUser) { this.createdAtUser = createdAtUser; }

    public OffsetDateTime getUpdatedAtUser() { return updatedAtUser; }
    public void setUpdatedAtUser(OffsetDateTime updatedAtUser) { this.updatedAtUser = updatedAtUser; }

    public Integer getIdFormateur() { return idFormateur; }
    public void setIdFormateur(Integer idFormateur) { this.idFormateur = idFormateur; }

    public Short getZoneKm() { return zoneKm; }
    public void setZoneKm(Short zoneKm) { this.zoneKm = zoneKm; }

    public boolean isVehiculePerso() { return vehiculePerso; }
    public void setVehiculePerso(boolean vehiculePerso) { this.vehiculePerso = vehiculePerso; }

    public boolean isPermis() { return permis; }
    public void setPermis(boolean permis) { this.permis = permis; }

    public String getMobilitePrefJson() { return mobilitePrefJson; }
    public void setMobilitePrefJson(String mobilitePrefJson) { this.mobilitePrefJson = mobilitePrefJson; }

    public String getDisponibiliteJson() { return disponibiliteJson; }
    public void setDisponibiliteJson(String disponibiliteJson) { this.disponibiliteJson = disponibiliteJson; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public OffsetDateTime getCreatedAtFormateur() { return createdAtFormateur; }
    public void setCreatedAtFormateur(OffsetDateTime createdAtFormateur) { this.createdAtFormateur = createdAtFormateur; }

    public OffsetDateTime getUpdatedAtFormateur() { return updatedAtFormateur; }
    public void setUpdatedAtFormateur(OffsetDateTime updatedAtFormateur) { this.updatedAtFormateur = updatedAtFormateur; }
}

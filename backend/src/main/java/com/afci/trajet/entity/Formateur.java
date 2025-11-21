package com.afci.trajet.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant un formateur.
 *
 * Correspond à la table SQL : formateur
 * Bloc : Profils & Accessibilité
 */
@Entity
@Table(name = "formateur")
public class Formateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formateur")
    private Integer idFormateur;

    /**
     * FK vers utilisateur.id_user
     * (on garde l'ID simple, sans relation JPA pour rester lisible).
     */
    @Column(name = "id_user", nullable = false)
    private Integer idUser;

    /**
     * Rayon de mobilité en kilomètres.
     */
    @Min(0)
    @Max(500)
    @Column(name = "zone_km", nullable = false)
    private short zoneKm;

    /**
     * Le formateur dispose-t-il de son véhicule personnel ?
     */
    @Column(name = "vehicule_perso", nullable = false)
    private boolean vehiculePerso = false;

    /**
     * Le formateur possède-t-il le permis de conduire ?
     */
    @Column(name = "permis", nullable = false)
    private boolean permis = false;

    /**
     * Préférences de mobilité (JSONB côté PostgreSQL).
     * Exemple : { "moyen_principal": "VOITURE", "alternatives": ["TRAIN"] }
     */
    @Column(name = "mobilite_pref_json", columnDefinition = "jsonb")
    private String mobilitePrefJson;

    /**
     * Créneaux de disponibilités (JSONB côté PostgreSQL).
     */
    @Column(name = "disponibilite_json", columnDefinition = "jsonb")
    private String disponibiliteJson;

    /**
     * Commentaire libre.
     */
    @Size(max = 1000)
    @Column(name = "commentaire", columnDefinition = "text")
    private String commentaire;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public Formateur() {
        // requis par JPA
    }

    public Formateur(Integer idFormateur,
                     Integer idUser,
                     short zoneKm,
                     boolean vehiculePerso,
                     boolean permis,
                     String mobilitePrefJson,
                     String disponibiliteJson,
                     String commentaire,
                     OffsetDateTime createdAt,
                     OffsetDateTime updatedAt) {
        this.idFormateur = idFormateur;
        this.idUser = idUser;
        this.zoneKm = zoneKm;
        this.vehiculePerso = vehiculePerso;
        this.permis = permis;
        this.mobilitePrefJson = mobilitePrefJson;
        this.disponibiliteJson = disponibiliteJson;
        this.commentaire = commentaire;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --------------------------------------------------------
    // Getters / Setters
    // --------------------------------------------------------

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

    public short getZoneKm() {
        return zoneKm;
    }

    public void setZoneKm(short zoneKm) {
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

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
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

    // --------------------------------------------------------
    // toString
    // --------------------------------------------------------

    @Override
    public String toString() {
        return "Formateur{" +
                "idFormateur=" + idFormateur +
                ", idUser=" + idUser +
                ", zoneKm=" + zoneKm +
                ", vehiculePerso=" + vehiculePerso +
                ", permis=" + permis +
                '}';
    }
}

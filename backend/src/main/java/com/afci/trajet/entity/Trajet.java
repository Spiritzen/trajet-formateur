package com.afci.trajet.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant un trajet proposé pour un ordre de mission.
 *
 * Correspond à la table SQL : trajet
 * Bloc : Missions & Trajets
 */
@Entity
@Table(name = "trajet")
public class Trajet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trajet")
    private Integer idTrajet;

    /**
     * FK vers ordre_mission.id_ordre_mission
     */
    @Column(name = "id_ordre_mission", nullable = false)
    private Integer idOrdreMission;

    /**
     * Moyen principal : VOITURE, TRAIN, BUS, ...
     */
    @Size(max = 15)
    @Column(name = "moyen_principal", nullable = false, length = 15)
    private String moyenPrincipal;

    @Column(name = "distance_km", precision = 7, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "duree_min")
    private Integer dureeMin;

    @Column(name = "cout_estime", precision = 10, scale = 2)
    private BigDecimal coutEstime;

    /**
     * Itinéraire détaillé stocké au format JSONB côté PostgreSQL.
     */
    @Column(name = "itineraire_json", columnDefinition = "jsonb")
    private String itineraireJson;

    /**
     * Qui a proposé le trajet : FORMATEUR, SYSTEME, GESTIONNAIRE
     */
    @Size(max = 15)
    @Column(name = "propose_par", nullable = false, length = 15)
    private String proposePar = "FORMATEUR";

    /**
     * TRUE si ce trajet a été retenu pour l'ordre de mission.
     */
    @Column(name = "retenu", nullable = false)
    private boolean retenu = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public Trajet() {
    }

    public Trajet(Integer idTrajet,
                  Integer idOrdreMission,
                  String moyenPrincipal,
                  BigDecimal distanceKm,
                  Integer dureeMin,
                  BigDecimal coutEstime,
                  String itineraireJson,
                  String proposePar,
                  boolean retenu,
                  OffsetDateTime createdAt,
                  OffsetDateTime updatedAt) {
        this.idTrajet = idTrajet;
        this.idOrdreMission = idOrdreMission;
        this.moyenPrincipal = moyenPrincipal;
        this.distanceKm = distanceKm;
        this.dureeMin = dureeMin;
        this.coutEstime = coutEstime;
        this.itineraireJson = itineraireJson;
        this.proposePar = proposePar;
        this.retenu = retenu;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --------------------------------------------------------
    // Getters / Setters
    // --------------------------------------------------------

    public Integer getIdTrajet() {
        return idTrajet;
    }

    public void setIdTrajet(Integer idTrajet) {
        this.idTrajet = idTrajet;
    }

    public Integer getIdOrdreMission() {
        return idOrdreMission;
    }

    public void setIdOrdreMission(Integer idOrdreMission) {
        this.idOrdreMission = idOrdreMission;
    }

    public String getMoyenPrincipal() {
        return moyenPrincipal;
    }

    public void setMoyenPrincipal(String moyenPrincipal) {
        this.moyenPrincipal = moyenPrincipal;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getDureeMin() {
        return dureeMin;
    }

    public void setDureeMin(Integer dureeMin) {
        this.dureeMin = dureeMin;
    }

    public BigDecimal getCoutEstime() {
        return coutEstime;
    }

    public void setCoutEstime(BigDecimal coutEstime) {
        this.coutEstime = coutEstime;
    }

    public String getItineraireJson() {
        return itineraireJson;
    }

    public void setItineraireJson(String itineraireJson) {
        this.itineraireJson = itineraireJson;
    }

    public String getProposePar() {
        return proposePar;
    }

    public void setProposePar(String proposePar) {
        this.proposePar = proposePar;
    }

    public boolean isRetenu() {
        return retenu;
    }

    public void setRetenu(boolean retenu) {
        this.retenu = retenu;
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
        return "Trajet{" +
                "idTrajet=" + idTrajet +
                ", idOrdreMission=" + idOrdreMission +
                ", moyenPrincipal='" + moyenPrincipal + '\'' +
                ", retenu=" + retenu +
                '}';
    }
}

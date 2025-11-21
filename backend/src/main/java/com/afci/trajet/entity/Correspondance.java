package com.afci.trajet.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant une correspondance / segment de trajet.
 *
 * Correspond à la table SQL : correspondance
 * Bloc : Missions & Trajets
 */
@Entity
@Table(name = "correspondance")
public class Correspondance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_corresp")
    private Integer idCorresp;

    /**
     * FK vers trajet.id_trajet
     */
    @Column(name = "id_trajet", nullable = false)
    private Integer idTrajet;

    /**
     * Ordre de la correspondance dans le trajet (1, 2, 3, ...)
     */
    @Min(1)
    @Column(name = "ordre", nullable = false)
    private short ordre;

    /**
     * Type de transport : TRAIN, BUS, METRO, ...
     */
    @Size(max = 10)
    @Column(name = "type_transport", nullable = false, length = 10)
    private String typeTransport;

    @Column(name = "duree_min")
    private Integer dureeMin;

    @Column(name = "cout", precision = 10, scale = 2)
    private BigDecimal cout;

    @Size(max = 200)
    @Column(name = "details", length = 200)
    private String details;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public Correspondance() {
    }

    public Correspondance(Integer idCorresp,
                          Integer idTrajet,
                          short ordre,
                          String typeTransport,
                          Integer dureeMin,
                          BigDecimal cout,
                          String details,
                          OffsetDateTime createdAt,
                          OffsetDateTime updatedAt) {
        this.idCorresp = idCorresp;
        this.idTrajet = idTrajet;
        this.ordre = ordre;
        this.typeTransport = typeTransport;
        this.dureeMin = dureeMin;
        this.cout = cout;
        this.details = details;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --------------------------------------------------------
    // Getters / Setters
    // --------------------------------------------------------

    public Integer getIdCorresp() {
        return idCorresp;
    }

    public void setIdCorresp(Integer idCorresp) {
        this.idCorresp = idCorresp;
    }

    public Integer getIdTrajet() {
        return idTrajet;
    }

    public void setIdTrajet(Integer idTrajet) {
        this.idTrajet = idTrajet;
    }

    public short getOrdre() {
        return ordre;
    }

    public void setOrdre(short ordre) {
        this.ordre = ordre;
    }

    public String getTypeTransport() {
        return typeTransport;
    }

    public void setTypeTransport(String typeTransport) {
        this.typeTransport = typeTransport;
    }

    public Integer getDureeMin() {
        return dureeMin;
    }

    public void setDureeMin(Integer dureeMin) {
        this.dureeMin = dureeMin;
    }

    public BigDecimal getCout() {
        return cout;
    }

    public void setCout(BigDecimal cout) {
        this.cout = cout;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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
        return "Correspondance{" +
                "idCorresp=" + idCorresp +
                ", idTrajet=" + idTrajet +
                ", ordre=" + ordre +
                ", typeTransport='" + typeTransport + '\'' +
                '}';
    }
}

package com.afci.trajet.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant la signature associée à un ordre de mission.
 *
 * Correspond à la table SQL : signature
 * Bloc : Missions & Trajets
 */
@Entity
@Table(name = "signature")
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_signature")
    private Integer idSignature;

    /**
     * FK vers ordre_mission.id_ordre_mission
     */
    @Column(name = "id_ordre_mission", nullable = false)
    private Integer idOrdreMission;

    /**
     * Etat de la signature : EN_ATTENTE, SIGNE, REFUSE
     */
    @Size(max = 12)
    @Column(name = "etat", nullable = false, length = 12)
    private String etat = "EN_ATTENTE";

    @Size(max = 255)
    @Column(name = "fichier_path", length = 255)
    private String fichierPath;

    /**
     * Horodatage de la signature (date/heure exacte).
     */
    @Column(name = "horodatage")
    private OffsetDateTime horodatage;

    /**
     * FK vers utilisateur.id_user (qui a signé ou refusé), optionnel.
     */
    @Column(name = "id_signataire")
    private Integer idSignataire;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public Signature() {
    }

    public Signature(Integer idSignature,
                     Integer idOrdreMission,
                     String etat,
                     String fichierPath,
                     OffsetDateTime horodatage,
                     Integer idSignataire,
                     OffsetDateTime createdAt,
                     OffsetDateTime updatedAt) {
        this.idSignature = idSignature;
        this.idOrdreMission = idOrdreMission;
        this.etat = etat;
        this.fichierPath = fichierPath;
        this.horodatage = horodatage;
        this.idSignataire = idSignataire;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --------------------------------------------------------
    // Getters / Setters
    // --------------------------------------------------------

    public Integer getIdSignature() {
        return idSignature;
    }

    public void setIdSignature(Integer idSignature) {
        this.idSignature = idSignature;
    }

    public Integer getIdOrdreMission() {
        return idOrdreMission;
    }

    public void setIdOrdreMission(Integer idOrdreMission) {
        this.idOrdreMission = idOrdreMission;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getFichierPath() {
        return fichierPath;
    }

    public void setFichierPath(String fichierPath) {
        this.fichierPath = fichierPath;
    }

    public OffsetDateTime getHorodatage() {
        return horodatage;
    }

    public void setHorodatage(OffsetDateTime horodatage) {
        this.horodatage = horodatage;
    }

    public Integer getIdSignataire() {
        return idSignataire;
    }

    public void setIdSignataire(Integer idSignataire) {
        this.idSignataire = idSignataire;
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
        return "Signature{" +
                "idSignature=" + idSignature +
                ", idOrdreMission=" + idOrdreMission +
                ", etat='" + etat + '\'' +
                '}';
    }
}

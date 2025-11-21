package com.afci.trajet.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entité JPA pour la table NOTIFICATION.
 *
 * Table SQL (rappel) :
 *  - id_notification  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY
 *  - id_ordre_mission INTEGER NOT NULL
 *  - canal            VARCHAR(10) NOT NULL
 *  - destinataire     VARCHAR(150) NOT NULL
 *  - message          TEXT NOT NULL
 *  - envoye_at        TIMESTAMPTZ
 *  - provider_msg_id  VARCHAR(100)
 *  - etat             VARCHAR(10) NOT NULL DEFAULT 'PENDING'
 *  - created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
 *  - updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
 *
 * Les contraintes de type CHECK (canal, etat) et les index/foreign keys
 * sont gérés côté base (01/02_xxx.sql). Ici on se concentre sur le mapping objet.
 */
@Entity
@Table(name = "notification")
public class Notification {

    // ==============
    //  Clé primaire
    // ==============

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Integer idNotification;

    // =======================
    //  Colonnes de relation
    // =======================

    /**
     * Référence à l'ordre de mission concerné.
     * On reste sur un mapping par ID (pas de @ManyToOne pour l’instant).
     */
    @Column(name = "id_ordre_mission", nullable = false)
    private Integer idOrdreMission;

    // =======================
    //  Données fonctionnelles
    // =======================

    /**
     * Canal de notification : 'SMS' ou 'EMAIL' (géré par CHECK en base).
     */
    @Column(name = "canal", length = 10, nullable = false)
    private String canal;

    /**
     * Destinataire : email ou numéro de téléphone selon le canal.
     */
    @Column(name = "destinataire", length = 150, nullable = false)
    private String destinataire;

    /**
     * Contenu du message envoyé.
     */
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * Horodatage réel d’envoi (rempli quand le provider confirme l’envoi).
     */
    @Column(name = "envoye_at")
    private Instant envoyeAt;

    /**
     * Identifiant du message chez le provider (Twilio, Mailjet, etc.).
     * Permet de tracer / réconcilier les statuts.
     */
    @Column(name = "provider_msg_id", length = 100)
    private String providerMsgId;

    /**
     * Etat de la notification : 'PENDING', 'SENT', 'FAILED'.
     */
    @Column(name = "etat", length = 10, nullable = false)
    private String etat;

    // =========================
    //  Colonnes de métadonnées
    // =========================

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ==================
    //  Constructeurs
    // ==================

    /**
     * Constructeur vide requis par JPA.
     */
    public Notification() {
    }

    /**
     * Constructeur complet (pratique pour les tests ou les créations explicites).
     */
    public Notification(Integer idNotification,
                        Integer idOrdreMission,
                        String canal,
                        String destinataire,
                        String message,
                        Instant envoyeAt,
                        String providerMsgId,
                        String etat,
                        Instant createdAt,
                        Instant updatedAt) {
        this.idNotification = idNotification;
        this.idOrdreMission = idOrdreMission;
        this.canal = canal;
        this.destinataire = destinataire;
        this.message = message;
        this.envoyeAt = envoyeAt;
        this.providerMsgId = providerMsgId;
        this.etat = etat;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ===========
    //  Getters
    // ===========

    public Integer getIdNotification() {
        return idNotification;
    }

    public Integer getIdOrdreMission() {
        return idOrdreMission;
    }

    public String getCanal() {
        return canal;
    }

    public String getDestinataire() {
        return destinataire;
    }

    public String getMessage() {
        return message;
    }

    public Instant getEnvoyeAt() {
        return envoyeAt;
    }

    public String getProviderMsgId() {
        return providerMsgId;
    }

    public String getEtat() {
        return etat;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // ===========
    //  Setters
    // ===========

    public void setIdNotification(Integer idNotification) {
        this.idNotification = idNotification;
    }

    public void setIdOrdreMission(Integer idOrdreMission) {
        this.idOrdreMission = idOrdreMission;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public void setDestinataire(String destinataire) {
        this.destinataire = destinataire;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setEnvoyeAt(Instant envoyeAt) {
        this.envoyeAt = envoyeAt;
    }

    public void setProviderMsgId(String providerMsgId) {
        this.providerMsgId = providerMsgId;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ===========
    //  toString
    // ===========

    @Override
    public String toString() {
        return "Notification{" +
                "idNotification=" + idNotification +
                ", idOrdreMission=" + idOrdreMission +
                ", canal='" + canal + '\'' +
                ", destinataire='" + destinataire + '\'' +
                ", message='" + message + '\'' +
                ", envoyeAt=" + envoyeAt +
                ", providerMsgId='" + providerMsgId + '\'' +
                ", etat='" + etat + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

// src/main/java/com/afci/trajet/dto/om/OrdreMissionCreateRequest.java
package com.afci.trajet.dto.om;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO utilisé par l'ECOLE pour créer un nouvel Ordre de Mission.
 *
 * Rappel des règles :
 *  - Le statut est toujours initialisé à BROUILLON côté service.
 *  - Le codeOrdre est généré automatiquement (ex : OM-2025-0001).
 *  - idEcole et idUserCreateur sont déduits du compte connecté, pas envoyés par le front.
 *  - idFormateur n'est PAS renseigné ici (il sera affecté plus tard).
 */
public class OrdreMissionCreateRequest {

    /**
     * Date de début de la mission (obligatoire).
     */
    private LocalDate dateDebut;

    /**
     * Date de fin de la mission (obligatoire).
     */
    private LocalDate dateFin;

    /**
     * Commentaire libre : objectif de la mission, précisions, etc.
     */
    private String commentaire;

    /**
     * Coût total estimé de la mission (facultatif, peut être null tant que le formateur
     * n'a pas proposé de trajet complet).
     */
    private BigDecimal coutTotalEstime;

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public OrdreMissionCreateRequest() {
    }

    public OrdreMissionCreateRequest(LocalDate dateDebut,
                                     LocalDate dateFin,
                                     String commentaire,
                                     BigDecimal coutTotalEstime) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.commentaire = commentaire;
        this.coutTotalEstime = coutTotalEstime;
    }

    // --------------------------------------------------------
    // Getters / Setters
    // --------------------------------------------------------

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public BigDecimal getCoutTotalEstime() {
        return coutTotalEstime;
    }

    public void setCoutTotalEstime(BigDecimal coutTotalEstime) {
        this.coutTotalEstime = coutTotalEstime;
    }
}

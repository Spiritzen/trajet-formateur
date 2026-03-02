// src/main/java/com/afci/trajet/dto/om/OrdreMissionUpdateRequest.java
package com.afci.trajet.dto.om;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO utilisé par l'ECOLE pour modifier un Ordre de Mission
 * tant que celui-ci est encore au statut BROUILLON.
 *
 * Champs modifiables :
 *  - dateDebut
 *  - dateFin
 *  - commentaire
 *  - coutTotalEstime
 *
 * Les champs suivants ne sont PAS modifiables ici :
 *  - codeOrdre
 *  - idEcole
 *  - idUserCreateur
 *  - idFormateur (géré via une route dédiée d'affectation)
 *  - statut (géré par les transitions métier)
 */
public class OrdreMissionUpdateRequest {

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String commentaire;
    private BigDecimal coutTotalEstime;

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public OrdreMissionUpdateRequest() {
    }

    public OrdreMissionUpdateRequest(LocalDate dateDebut,
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

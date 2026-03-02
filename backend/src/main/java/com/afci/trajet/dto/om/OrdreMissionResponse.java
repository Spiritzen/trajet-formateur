// src/main/java/com/afci/trajet/dto/om/OrdreMissionResponse.java
package com.afci.trajet.dto.om;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * DTO "complet" représentant un Ordre de Mission.
 *
 * Utilisé pour :
 *  - le détail côté ECOLE,
 *  - le détail côté FORMATEUR,
 *  - certaines réponses Admin.
 *
 * Il reflète quasi intégralement l'entité OrdreMission,
 * en ajoutant éventuellement quelques champs de confort.
 */
public class OrdreMissionResponse {

    private Integer idOrdreMission;
    private String codeOrdre;

    private Integer idFormateur;
    private Integer idEcole;

    private Integer idUserCreateur;
    private Integer idUserValidateur;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private String statut;
    private BigDecimal coutTotalEstime;
    private String commentaire;

    private Integer idTrajetRetenu;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public OrdreMissionResponse() {
    }

    public OrdreMissionResponse(Integer idOrdreMission,
                                String codeOrdre,
                                Integer idFormateur,
                                Integer idEcole,
                                Integer idUserCreateur,
                                Integer idUserValidateur,
                                LocalDate dateDebut,
                                LocalDate dateFin,
                                String statut,
                                BigDecimal coutTotalEstime,
                                String commentaire,
                                Integer idTrajetRetenu,
                                OffsetDateTime createdAt,
                                OffsetDateTime updatedAt) {
        this.idOrdreMission = idOrdreMission;
        this.codeOrdre = codeOrdre;
        this.idFormateur = idFormateur;
        this.idEcole = idEcole;
        this.idUserCreateur = idUserCreateur;
        this.idUserValidateur = idUserValidateur;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.coutTotalEstime = coutTotalEstime;
        this.commentaire = commentaire;
        this.idTrajetRetenu = idTrajetRetenu;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --------------------------------------------------------
    // Getters / Setters
    // --------------------------------------------------------

    public Integer getIdOrdreMission() {
        return idOrdreMission;
    }

    public void setIdOrdreMission(Integer idOrdreMission) {
        this.idOrdreMission = idOrdreMission;
    }

    public String getCodeOrdre() {
        return codeOrdre;
    }

    public void setCodeOrdre(String codeOrdre) {
        this.codeOrdre = codeOrdre;
    }

    public Integer getIdFormateur() {
        return idFormateur;
    }

    public void setIdFormateur(Integer idFormateur) {
        this.idFormateur = idFormateur;
    }

    public Integer getIdEcole() {
        return idEcole;
    }

    public void setIdEcole(Integer idEcole) {
        this.idEcole = idEcole;
    }

    public Integer getIdUserCreateur() {
        return idUserCreateur;
    }

    public void setIdUserCreateur(Integer idUserCreateur) {
        this.idUserCreateur = idUserCreateur;
    }

    public Integer getIdUserValidateur() {
        return idUserValidateur;
    }

    public void setIdUserValidateur(Integer idUserValidateur) {
        this.idUserValidateur = idUserValidateur;
    }

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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public BigDecimal getCoutTotalEstime() {
        return coutTotalEstime;
    }

    public void setCoutTotalEstime(BigDecimal coutTotalEstime) {
        this.coutTotalEstime = coutTotalEstime;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Integer getIdTrajetRetenu() {
        return idTrajetRetenu;
    }

    public void setIdTrajetRetenu(Integer idTrajetRetenu) {
        this.idTrajetRetenu = idTrajetRetenu;
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
}

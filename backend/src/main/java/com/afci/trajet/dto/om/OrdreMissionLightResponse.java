// src/main/java/com/afci/trajet/dto/om/OrdreMissionLightResponse.java
package com.afci.trajet.dto.om;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO "léger" pour afficher une liste d'Ordres de Mission
 * (par exemple dans un tableau côté ECOLE ou FORMATEUR).
 *
 * On se concentre sur les informations essentielles :
 *  - identifiant technique
 *  - code fonctionnel
 *  - statut actuel
 *  - dates
 *  - coût estimé
 *  - nom/prénom du formateur (pré-formaté côté service)
 */
public class OrdreMissionLightResponse {

    private Integer idOrdreMission;
    private String codeOrdre;
    private String statut;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal coutTotalEstime;

    /**
     * Nom/prénom du formateur sous forme lisible (ex : "Martin Claire").
     * Peut être null si aucun formateur n'a encore été affecté.
     */
    private String formateurNomPrenom;

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public OrdreMissionLightResponse() {
    }

    public OrdreMissionLightResponse(Integer idOrdreMission,
                                     String codeOrdre,
                                     String statut,
                                     LocalDate dateDebut,
                                     LocalDate dateFin,
                                     BigDecimal coutTotalEstime,
                                     String formateurNomPrenom) {
        this.idOrdreMission = idOrdreMission;
        this.codeOrdre = codeOrdre;
        this.statut = statut;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.coutTotalEstime = coutTotalEstime;
        this.formateurNomPrenom = formateurNomPrenom;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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

    public BigDecimal getCoutTotalEstime() {
        return coutTotalEstime;
    }

    public void setCoutTotalEstime(BigDecimal coutTotalEstime) {
        this.coutTotalEstime = coutTotalEstime;
    }

    public String getFormateurNomPrenom() {
        return formateurNomPrenom;
    }

    public void setFormateurNomPrenom(String formateurNomPrenom) {
        this.formateurNomPrenom = formateurNomPrenom;
    }
}

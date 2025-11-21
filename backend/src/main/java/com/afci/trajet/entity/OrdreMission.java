package com.afci.trajet.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant un ordre de mission.
 *
 * Correspond à la table SQL : ordre_mission
 * Bloc : Missions & Trajets
 */
@Entity
@Table(name = "ordre_mission")
public class OrdreMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ordre_mission")
    private Integer idOrdreMission;

    @NotBlank
    @Size(max = 30)
    @Column(name = "code_ordre", nullable = false, length = 30)
    private String codeOrdre;

    /**
     * FK vers formateur.id_formateur
     */
    @Column(name = "id_formateur", nullable = false)
    private Integer idFormateur;

    /**
     * FK vers ecole.id_ecole
     */
    @Column(name = "id_ecole", nullable = false)
    private Integer idEcole;

    /**
     * FK vers utilisateur.id_user (créateur)
     */
    @Column(name = "id_user_createur", nullable = false)
    private Integer idUserCreateur;

    /**
     * FK vers utilisateur.id_user (validateur), optionnel
     */
    @Column(name = "id_user_validateur")
    private Integer idUserValidateur;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    /**
     * Statut de l'OM :
     * BROUILLON, PROPOSE, EN_ATTENTE_VALIDATION, VALIDE, SIGNE, CLOTURE, REJETE
     */
    @Size(max = 24)
    @Column(name = "statut", nullable = false, length = 24)
    private String statut = "BROUILLON";

    @Column(name = "cout_total_estime", precision = 10, scale = 2)
    private BigDecimal coutTotalEstime;

    @Column(name = "commentaire", columnDefinition = "text")
    private String commentaire;

    /**
     * FK vers trajet.id_trajet (trajet retenu) - optionnel
     */
    @Column(name = "id_trajet_retenu")
    private Integer idTrajetRetenu;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public OrdreMission() {
    }

    public OrdreMission(Integer idOrdreMission,
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

    // --------------------------------------------------------
    // toString
    // --------------------------------------------------------

    @Override
    public String toString() {
        return "OrdreMission{" +
                "idOrdreMission=" + idOrdreMission +
                ", codeOrdre='" + codeOrdre + '\'' +
                ", idFormateur=" + idFormateur +
                ", idEcole=" + idEcole +
                ", statut='" + statut + '\'' +
                '}';
    }
}

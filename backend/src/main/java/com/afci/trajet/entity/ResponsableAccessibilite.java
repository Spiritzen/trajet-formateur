package com.afci.trajet.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant le responsable / référent accessibilité
 * au sein d'un établissement.
 *
 * Correspond à la table SQL : responsable_accessibilite
 * Bloc : Profils & Accessibilité
 */
@Entity
@Table(name = "responsable_accessibilite")
public class ResponsableAccessibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_responsable")
    private Integer idResponsable;

    /**
     * FK vers ecole.id_ecole
     */
    @Column(name = "id_ecole", nullable = false)
    private Integer idEcole;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @NotBlank
    @Size(max = 100)
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Size(max = 100)
    @Column(name = "fonction", length = 100)
    private String fonction;

    @Size(max = 20)
    @Column(name = "telephone", length = 20)
    private String telephone;

    @Email
    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;

    /**
     * Infos sur les créneaux de disponibilité (texte libre).
     */
    @Size(max = 100)
    @Column(name = "plage_horaire", length = 100)
    private String plageHoraire;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public ResponsableAccessibilite() {
    }

    public ResponsableAccessibilite(Integer idResponsable,
                                    Integer idEcole,
                                    String nom,
                                    String prenom,
                                    String fonction,
                                    String telephone,
                                    String email,
                                    String plageHoraire,
                                    OffsetDateTime createdAt,
                                    OffsetDateTime updatedAt) {
        this.idResponsable = idResponsable;
        this.idEcole = idEcole;
        this.nom = nom;
        this.prenom = prenom;
        this.fonction = fonction;
        this.telephone = telephone;
        this.email = email;
        this.plageHoraire = plageHoraire;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --------------------------------------------------------
    // Getters / Setters
    // --------------------------------------------------------

    public Integer getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(Integer idResponsable) {
        this.idResponsable = idResponsable;
    }

    public Integer getIdEcole() {
        return idEcole;
    }

    public void setIdEcole(Integer idEcole) {
        this.idEcole = idEcole;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlageHoraire() {
        return plageHoraire;
    }

    public void setPlageHoraire(String plageHoraire) {
        this.plageHoraire = plageHoraire;
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
        return "ResponsableAccessibilite{" +
                "idResponsable=" + idResponsable +
                ", idEcole=" + idEcole +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                '}';
    }
}

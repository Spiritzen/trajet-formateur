package com.afci.trajet.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant un établissement scolaire (école / lycée / collège).
 *
 * Correspond à la table SQL : ecole
 * Bloc : Profils & Accessibilité
 */
@Entity
@Table(name = "ecole")
public class Ecole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ecole")
    private Integer idEcole;

    /**
     * FK vers utilisateur.id_user (compte "ECOLE").
     */
    @Column(name = "id_user", nullable = false)
    private Integer idUser;

    @NotBlank
    @Size(max = 150)
    @Column(name = "nom_ecole", nullable = false, length = 150)
    private String nomEcole;

    @NotBlank
    @Size(max = 200)
    @Column(name = "adresse_l1", nullable = false, length = 200)
    private String adresseL1;

    @Size(max = 200)
    @Column(name = "adresse_l2", length = 200)
    private String adresseL2;

    @NotBlank
    @Size(max = 10)
    @Column(name = "code_postal", nullable = false, length = 10)
    private String codePostal;

    @NotBlank
    @Size(max = 120)
    @Column(name = "ville", nullable = false, length = 120)
    private String ville;

    @Size(max = 2)
    @Column(name = "pays_code", nullable = false, length = 2)
    private String paysCode = "FR";

    @Column(name = "lat", precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(name = "lon", precision = 9, scale = 6)
    private BigDecimal lon;

    /**
     * Niveau d’accessibilité global : FACILE, MOYENNE, DIFFICILE.
     */
    @Size(max = 10)
    @Column(name = "niveau_accessibilite", nullable = false, length = 10)
    private String niveauAccessibilite = "MOYENNE";

    /**
     * Infos supplémentaires sur l'accès (transport, parking, PMR...).
     */
    @Column(name = "infos_acces", columnDefinition = "text")
    private String infosAcces;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public Ecole() {
    }

    public Ecole(Integer idEcole,
                 Integer idUser,
                 String nomEcole,
                 String adresseL1,
                 String adresseL2,
                 String codePostal,
                 String ville,
                 String paysCode,
                 BigDecimal lat,
                 BigDecimal lon,
                 String niveauAccessibilite,
                 String infosAcces,
                 OffsetDateTime createdAt,
                 OffsetDateTime updatedAt) {
        this.idEcole = idEcole;
        this.idUser = idUser;
        this.nomEcole = nomEcole;
        this.adresseL1 = adresseL1;
        this.adresseL2 = adresseL2;
        this.codePostal = codePostal;
        this.ville = ville;
        this.paysCode = paysCode;
        this.lat = lat;
        this.lon = lon;
        this.niveauAccessibilite = niveauAccessibilite;
        this.infosAcces = infosAcces;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --------------------------------------------------------
    // Getters / Setters
    // --------------------------------------------------------

    public Integer getIdEcole() {
        return idEcole;
    }

    public void setIdEcole(Integer idEcole) {
        this.idEcole = idEcole;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getNomEcole() {
        return nomEcole;
    }

    public void setNomEcole(String nomEcole) {
        this.nomEcole = nomEcole;
    }

    public String getAdresseL1() {
        return adresseL1;
    }

    public void setAdresseL1(String adresseL1) {
        this.adresseL1 = adresseL1;
    }

    public String getAdresseL2() {
        return adresseL2;
    }

    public void setAdresseL2(String adresseL2) {
        this.adresseL2 = adresseL2;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPaysCode() {
        return paysCode;
    }

    public void setPaysCode(String paysCode) {
        this.paysCode = paysCode;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public String getNiveauAccessibilite() {
        return niveauAccessibilite;
    }

    public void setNiveauAccessibilite(String niveauAccessibilite) {
        this.niveauAccessibilite = niveauAccessibilite;
    }

    public String getInfosAcces() {
        return infosAcces;
    }

    public void setInfosAcces(String infosAcces) {
        this.infosAcces = infosAcces;
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
        return "Ecole{" +
                "idEcole=" + idEcole +
                ", idUser=" + idUser +
                ", nomEcole='" + nomEcole + '\'' +
                ", ville='" + ville + '\'' +
                ", codePostal='" + codePostal + '\'' +
                '}';
    }
}

// src/main/java/com/afci/trajet/dto/ecole/EcoleDetailAdminResponse.java
package com.afci.trajet.dto.ecole;

import java.math.BigDecimal;

/**
 * DTO "détail école" renvoyé à l'ADMIN.
 *
 * Contient :
 *  - toutes les informations principales sur l'établissement ;
 *  - les informations du compte utilisateur référent (rôle ECOLE).
 *
 * Utilisation typique :
 *  - affichage dans la modale "Voir l'école" côté admin.
 */
public class EcoleDetailAdminResponse {

    // ==== IDENTIFIANTS ====

    /** Identifiant de l'école (table ECOLE). */
    private Integer idEcole;

    /** Identifiant de l'utilisateur référent (table UTILISATEUR). */
    private Integer idUserReferent;

    // ==== INFOS ÉCOLE ====

    /** Nom officiel de l'établissement. */
    private String nomEcole;

    /** Adresse ligne 1 (numéro + rue). */
    private String adresseL1;

    /** Adresse complément (bâtiment, étage, etc.). */
    private String adresseL2;

    /** Code postal de l'établissement. */
    private String codePostal;

    /** Ville de l'établissement. */
    private String ville;

    /** Code pays ISO (ex. "FR"). */
    private String paysCode;

    /** Latitude géocodée. */
    private BigDecimal lat;

    /** Longitude géocodée. */
    private BigDecimal lon;

    /** Description du niveau d'accessibilité (ex. "Accès PMR complet"). */
    private String niveauAccessibilite;

    /** Informations complémentaires d'accès (parking, digicode, etc.). */
    private String infosAcces;

    // ==== INFOS RÉFÉRENT (UTILISATEUR ECOLE) ====

    /** Adresse e-mail du référent. */
    private String emailReferent;

    /** Prénom du référent. */
    private String prenomReferent;

    /** Nom du référent. */
    private String nomReferent;

    /** Téléphone du référent. */
    private String telephoneReferent;

    // ==== CONSTRUCTEURS ====

    /** Constructeur sans argument (obligatoire pour Jackson). */
    public EcoleDetailAdminResponse() {
    }

    /** Constructeur complet. */
    public EcoleDetailAdminResponse(
            Integer idEcole,
            Integer idUserReferent,
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
            String emailReferent,
            String prenomReferent,
            String nomReferent,
            String telephoneReferent
    ) {
        this.idEcole = idEcole;
        this.idUserReferent = idUserReferent;
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
        this.emailReferent = emailReferent;
        this.prenomReferent = prenomReferent;
        this.nomReferent = nomReferent;
        this.telephoneReferent = telephoneReferent;
    }

    // ==== GETTERS / SETTERS ====

    public Integer getIdEcole() {
        return idEcole;
    }

    public void setIdEcole(Integer idEcole) {
        this.idEcole = idEcole;
    }

    public Integer getIdUserReferent() {
        return idUserReferent;
    }

    public void setIdUserReferent(Integer idUserReferent) {
        this.idUserReferent = idUserReferent;
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

    public String getEmailReferent() {
        return emailReferent;
    }

    public void setEmailReferent(String emailReferent) {
        this.emailReferent = emailReferent;
    }

    public String getPrenomReferent() {
        return prenomReferent;
    }

    public void setPrenomReferent(String prenomReferent) {
        this.prenomReferent = prenomReferent;
    }

    public String getNomReferent() {
        return nomReferent;
    }

    public void setNomReferent(String nomReferent) {
        this.nomReferent = nomReferent;
    }

    public String getTelephoneReferent() {
        return telephoneReferent;
    }

    public void setTelephoneReferent(String telephoneReferent) {
        this.telephoneReferent = telephoneReferent;
    }
}

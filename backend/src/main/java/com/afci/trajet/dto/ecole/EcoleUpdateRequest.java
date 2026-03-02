package com.afci.trajet.dto.ecole;

import java.math.BigDecimal;

/**
 * DTO utilisé pour la MISE À JOUR d'une école existante.
 *
 * Ce DTO est volontairement proche de EcoleCreateRequest, mais on ne touche
 * PAS à l'utilisateur référent (compte ECOLE) dans cette première version :
 * on se concentre sur les données "école" (adresse, ville, accessibilité, etc.).
 */
public class EcoleUpdateRequest {

    // -----------------------------
    // Champs modifiables de l'école
    // -----------------------------

    private String nomEcole;
    private String adresseL1;
    private String adresseL2;
    private String codePostal;
    private String ville;
    private String paysCode;

    private BigDecimal lat;
    private BigDecimal lon;

    private String niveauAccessibilite;
    private String infosAcces;

    // -----------------------------
    // Constructeurs
    // -----------------------------

    public EcoleUpdateRequest() {
        // Constructeur vide requis par Spring / Jackson
    }

    public EcoleUpdateRequest(
            String nomEcole,
            String adresseL1,
            String adresseL2,
            String codePostal,
            String ville,
            String paysCode,
            BigDecimal lat,
            BigDecimal lon,
            String niveauAccessibilite,
            String infosAcces
    ) {
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
    }

    // -----------------------------
    // Getters / Setters
    // -----------------------------

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
}

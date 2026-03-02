package com.afci.trajet.dto.ecole;

import java.math.BigDecimal;

/**
 * DTO de vue globale "Mon établissement" côté ECOLE.
 *
 * Il combine :
 *  - les infos du compte utilisateur ECOLE (profil),
 *  - les infos de l'établissement (école).
 *
 * Plus tard, on pourra enrichir avec :
 *  - référent accessibilité,
 *  - indicateurs, etc.
 */
public class MonEtablissementResponse {

    // ===== PROFIL UTILISATEUR ECOLE =====
    private Integer idUser;
    private String email;
    private String prenom;
    private String nom;
    private String telephone;

    // ===== ETABLISSEMENT =====
    private Integer idEcole;
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

    // --- Bloc responsable accessibilité (peut être null si non défini) ---
    private ResponsableAccessibiliteDto responsableAccessibilite;
    public MonEtablissementResponse() {
    }

    public MonEtablissementResponse(
            Integer idUser,
            String email,
            String prenom,
            String nom,
            String telephone,
            Integer idEcole,
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
            ResponsableAccessibiliteDto responsableAccessibilite
    ) {
        this.idUser = idUser;
        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
        this.idEcole = idEcole;
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
        this.responsableAccessibilite = responsableAccessibilite;
    }

    // ===== Getters / Setters =====

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getIdEcole() {
        return idEcole;
    }

    public void setIdEcole(Integer idEcole) {
        this.idEcole = idEcole;
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
    
    public ResponsableAccessibiliteDto getResponsableAccessibilite() {
        return responsableAccessibilite;
    }

    public void setResponsableAccessibilite(ResponsableAccessibiliteDto responsableAccessibilite) {
        this.responsableAccessibilite = responsableAccessibilite;
    }
}

package com.afci.trajet.dto.ecole;

import java.math.BigDecimal;

public class EcoleCreateRequest {
	//DTO création d’école par l’ADMIN
    // --- Infos compte utilisateur ECOLE (référent) ---
	
	// ==== ATTRIBUTS ====
    private String email;
    private String prenom;
    private String nom;
    private String telephone;

    // Mot de passe provisoire (ou généré côté back si tu préfères)
    private String motDePasse;

    // --- Infos établissement ---
    private String nomEcole;
    private String adresseL1;
    private String adresseL2;
    private String codePostal;
    private String ville;
    private String paysCode;          // ex: "FR"
    private BigDecimal lat;
    private BigDecimal lon;
    private String niveauAccessibilite; // FACILE / MOYENNE / DIFFICILE
    private String infosAcces;

    // ==== CONSTRUCTEURS ====

    public EcoleCreateRequest() {
    }

    public EcoleCreateRequest(
            String email,
            String prenom,
            String nom,
            String telephone,
            String motDePasse,
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
        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
        this.motDePasse = motDePasse;
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

    // ==== GETTERS / SETTERS ====

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getNomEcole() { return nomEcole; }
    public void setNomEcole(String nomEcole) { this.nomEcole = nomEcole; }

    public String getAdresseL1() { return adresseL1; }
    public void setAdresseL1(String adresseL1) { this.adresseL1 = adresseL1; }

    public String getAdresseL2() { return adresseL2; }
    public void setAdresseL2(String adresseL2) { this.adresseL2 = adresseL2; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getPaysCode() { return paysCode; }
    public void setPaysCode(String paysCode) { this.paysCode = paysCode; }

    public BigDecimal getLat() { return lat; }
    public void setLat(BigDecimal lat) { this.lat = lat; }

    public BigDecimal getLon() { return lon; }
    public void setLon(BigDecimal lon) { this.lon = lon; }

    public String getNiveauAccessibilite() { return niveauAccessibilite; }
    public void setNiveauAccessibilite(String niveauAccessibilite) {
        this.niveauAccessibilite = niveauAccessibilite;
    }

    public String getInfosAcces() { return infosAcces; }
    public void setInfosAcces(String infosAcces) { this.infosAcces = infosAcces; }
}

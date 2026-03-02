package com.afci.trajet.dto.ecole;

/**
 * DTO utilisé côté ADMIN pour mettre à jour
 * à la fois :
 *   - les informations de l'établissement (table ecole)
 *   - les informations du compte référent (table utilisateur)
 */
public class EcoleUpdateAdminRequest {

    // ==== INFOS ÉTABLISSEMENT ====
    private String nomEcole;
    private String adresseL1;
    private String adresseL2;
    private String codePostal;
    private String ville;
    private String paysCode;              // ex : "FR"
    private String niveauAccessibilite;   // FACILE / MOYENNE / DIFFICILE
    private String infosAcces;

    // ==== INFOS COMPTE RÉFÉRENT (UTILISATEUR ECOLE) ====
    private String emailReferent;
    private String prenomReferent;
    private String nomReferent;
    private String telephoneReferent;

    // ==== CONSTRUCTEURS ====

    public EcoleUpdateAdminRequest() {
    }

    public EcoleUpdateAdminRequest(
            String nomEcole,
            String adresseL1,
            String adresseL2,
            String codePostal,
            String ville,
            String paysCode,
            String niveauAccessibilite,
            String infosAcces,
            String emailReferent,
            String prenomReferent,
            String nomReferent,
            String telephoneReferent
    ) {
        this.nomEcole = nomEcole;
        this.adresseL1 = adresseL1;
        this.adresseL2 = adresseL2;
        this.codePostal = codePostal;
        this.ville = ville;
        this.paysCode = paysCode;
        this.niveauAccessibilite = niveauAccessibilite;
        this.infosAcces = infosAcces;
        this.emailReferent = emailReferent;
        this.prenomReferent = prenomReferent;
        this.nomReferent = nomReferent;
        this.telephoneReferent = telephoneReferent;
    }

    // ==== GETTERS / SETTERS ====

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

    public String getNiveauAccessibilite() { return niveauAccessibilite; }
    public void setNiveauAccessibilite(String niveauAccessibilite) {
        this.niveauAccessibilite = niveauAccessibilite;
    }

    public String getInfosAcces() { return infosAcces; }
    public void setInfosAcces(String infosAcces) { this.infosAcces = infosAcces; }

    public String getEmailReferent() { return emailReferent; }
    public void setEmailReferent(String emailReferent) { this.emailReferent = emailReferent; }

    public String getPrenomReferent() { return prenomReferent; }
    public void setPrenomReferent(String prenomReferent) {
        this.prenomReferent = prenomReferent;
    }

    public String getNomReferent() { return nomReferent; }
    public void setNomReferent(String nomReferent) { this.nomReferent = nomReferent; }

    public String getTelephoneReferent() { return telephoneReferent; }
    public void setTelephoneReferent(String telephoneReferent) {
        this.telephoneReferent = telephoneReferent;
    }
}

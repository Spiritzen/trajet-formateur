package com.afci.trajet.dto.ecole;

/**
 * DTO pour la mise à jour des INFORMATIONS D'ÉTABLISSEMENT
 * depuis l'espace ECOLE.
 *
 * On y retrouve les champs "école" que l'établissement peut modifier :
 *  - nomEcole
 *  - adresseL1
 *  - adresseL2
 *  - codePostal
 *  - ville
 *  - paysCode
 *  - niveauAccessibilite
 *  - infosAcces
 *
 * ⚠ lat / lon NE SONT PAS exposés ici : ils sont recalculés automatiquement
 * via le service GeocodingService à partir de l'adresse.
 */
public class MonEtablissementUpdateRequest {

    private String nomEcole;
    private String adresseL1;
    private String adresseL2;
    private String codePostal;
    private String ville;
    private String paysCode;
    private String niveauAccessibilite;
    private String infosAcces;

    public MonEtablissementUpdateRequest() {
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

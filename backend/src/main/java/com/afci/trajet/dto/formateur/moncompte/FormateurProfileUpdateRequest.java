// src/main/java/com/afci/trajet/dto/formateur/moncompte/FormateurProfileUpdateRequest.java
package com.afci.trajet.dto.formateur.moncompte;

/**
 * DTO d'entrée pour la mise à jour du "Mon compte" côté FORMATEUR.
 *
 * ⚠️ Volontairement :
 *  - pas d'email
 *  - pas de prénom / nom
 *  -> modifiables uniquement par l'ADMIN.
 */
public class FormateurProfileUpdateRequest {

    // --- Partie UTILISATEUR modifiable ---

    private String telephone;
    private String adresseL1;
    private String adresseL2;
    private String codePostal;
    private String ville;
    private String paysCode;

    // --- Partie FORMATEUR modifiable ---

    private Short zoneKm;
    private Boolean vehiculePerso;
    private Boolean permis;
    private String mobilitePrefJson;
    private String disponibiliteJson;
    private String commentaire;

    public FormateurProfileUpdateRequest() {
    }

    public FormateurProfileUpdateRequest(
            String telephone,
            String adresseL1,
            String adresseL2,
            String codePostal,
            String ville,
            String paysCode,
            Short zoneKm,
            Boolean vehiculePerso,
            Boolean permis,
            String mobilitePrefJson,
            String disponibiliteJson,
            String commentaire
    ) {
        this.telephone = telephone;
        this.adresseL1 = adresseL1;
        this.adresseL2 = adresseL2;
        this.codePostal = codePostal;
        this.ville = ville;
        this.paysCode = paysCode;
        this.zoneKm = zoneKm;
        this.vehiculePerso = vehiculePerso;
        this.permis = permis;
        this.mobilitePrefJson = mobilitePrefJson;
        this.disponibiliteJson = disponibiliteJson;
        this.commentaire = commentaire;
    }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

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

    public Short getZoneKm() { return zoneKm; }
    public void setZoneKm(Short zoneKm) { this.zoneKm = zoneKm; }

    public Boolean getVehiculePerso() { return vehiculePerso; }
    public void setVehiculePerso(Boolean vehiculePerso) { this.vehiculePerso = vehiculePerso; }

    public Boolean getPermis() { return permis; }
    public void setPermis(Boolean permis) { this.permis = permis; }

    public String getMobilitePrefJson() { return mobilitePrefJson; }
    public void setMobilitePrefJson(String mobilitePrefJson) { this.mobilitePrefJson = mobilitePrefJson; }

    public String getDisponibiliteJson() { return disponibiliteJson; }
    public void setDisponibiliteJson(String disponibiliteJson) { this.disponibiliteJson = disponibiliteJson; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}

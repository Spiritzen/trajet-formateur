package com.afci.trajet.dto.ecole;

public class EcoleResponse {
    //DTO réponse d’école (pour la liste admin)
	
	// ==== ATTRIBUTS ====
    private Integer idEcole;
    private String nomEcole;
    private String ville;
    private String codePostal;
    private String nomReferent;
    private String emailReferent;

    // ==== CONSTRUCTEURS ====
    public EcoleResponse() {
    }

    public EcoleResponse(Integer idEcole,
                         String nomEcole,
                         String ville,
                         String codePostal,
                         String nomReferent,
                         String emailReferent) {
        this.idEcole = idEcole;
        this.nomEcole = nomEcole;
        this.ville = ville;
        this.codePostal = codePostal;
        this.nomReferent = nomReferent;
        this.emailReferent = emailReferent;
    }
    
    // ==== GETTERS / SETTERS ====

    public Integer getIdEcole() { return idEcole; }
    public void setIdEcole(Integer idEcole) { this.idEcole = idEcole; }

    public String getNomEcole() { return nomEcole; }
    public void setNomEcole(String nomEcole) { this.nomEcole = nomEcole; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }

    public String getNomReferent() { return nomReferent; }
    public void setNomReferent(String nomReferent) { this.nomReferent = nomReferent; }

    public String getEmailReferent() { return emailReferent; }
    public void setEmailReferent(String emailReferent) { this.emailReferent = emailReferent; }
}

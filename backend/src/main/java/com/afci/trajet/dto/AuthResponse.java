package com.afci.trajet.dto;

import java.util.List;

/**
 * Réponse renvoyée après un login réussi :
 * contient le JWT + quelques infos utiles sur l'utilisateur.
 */
public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";   // standard
    private Integer userId;
    private String email;
    private String prenom;
    private String nom;
    private List<String> roles;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken,
                        String tokenType,
                        Integer userId,
                        String email,
                        String prenom,
                        String nom,
                        List<String> roles) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.userId = userId;
        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
        this.roles = roles;
    }

    // Getters / Setters

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

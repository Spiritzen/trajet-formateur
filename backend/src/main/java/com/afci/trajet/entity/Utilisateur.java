package com.afci.trajet.entity;



import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entité représentant un utilisateur du système.
 *
 * Correspond à la table SQL : utilisateur
 *
 * Schéma principal : sécurité / identité
 */
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer idUser;

    @Email
    @NotBlank
    @Size(max = 150)
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @NotBlank
    @Size(min = 6, max = 255)
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank
    @Size(max = 100)
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Size(max = 20)
    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "telephone_verified", nullable = false)
    private boolean telephoneVerified = false;

    @Size(max = 255)
    @Column(name = "avatar_path", length = 255)
    private String avatarPath;

    @Size(max = 200)
    @Column(name = "adresse_l1", length = 200)
    private String adresseL1;

    @Size(max = 200)
    @Column(name = "adresse_l2", length = 200)
    private String adresseL2;

    @Size(max = 10)
    @Column(name = "code_postal", length = 10)
    private String codePostal;

    @Size(max = 120)
    @Column(name = "ville", length = 120)
    private String ville;

    @Size(max = 2)
    @Column(name = "pays_code", length = 2)
    private String paysCode;

    @Column(name = "lat", precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(name = "lon", precision = 9, scale = 6)
    private BigDecimal lon;

    @Column(name = "last_login_at")
    private OffsetDateTime lastLoginAt;

    @Column(name = "password_updated_at")
    private OffsetDateTime passwordUpdatedAt;

    @Column(name = "failed_login_attempts", nullable = false)
    private short failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private OffsetDateTime lockedUntil;

    /**
     * Préférences utilisateur en JSON (stockées en JSONB côté PostgreSQL).
     *
     * Pour l'instant, on ne les manipule pas côté Java.
     * On marque donc ce champ en "lecture seule" pour éviter
     * que Hibernate essaie de l'insérer / le mettre à jour,
     * ce qui provoquait l'erreur de type (jsonb vs varchar).
     */
    @Column(
        name = "preferences_json",
        columnDefinition = "jsonb",
        insertable = false,   // Hibernate ne mettra PAS ce champ dans les INSERT
        updatable = false     // ni dans les UPDATE
    )
    private String preferencesJson;

    @Column(name = "terms_accepted_at")
    private OffsetDateTime termsAcceptedAt;

    @Column(name = "privacy_consent_at")
    private OffsetDateTime privacyConsentAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public Utilisateur() {
        // Constructeur par défaut requis par JPA
    }

    public Utilisateur(
            Integer idUser,
            String email,
            String passwordHash,
            String prenom,
            String nom,
            String telephone,
            boolean actif,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            boolean emailVerified,
            boolean telephoneVerified,
            String avatarPath,
            String adresseL1,
            String adresseL2,
            String codePostal,
            String ville,
            String paysCode,
            BigDecimal lat,
            BigDecimal lon,
            OffsetDateTime lastLoginAt,
            OffsetDateTime passwordUpdatedAt,
            short failedLoginAttempts,
            OffsetDateTime lockedUntil,
            String preferencesJson,
            OffsetDateTime termsAcceptedAt,
            OffsetDateTime privacyConsentAt,
            OffsetDateTime deletedAt) {
        this.idUser = idUser;
        this.email = email;
        this.passwordHash = passwordHash;
        this.prenom = prenom;
        this.nom = nom;
        this.telephone = telephone;
        this.actif = actif;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.emailVerified = emailVerified;
        this.telephoneVerified = telephoneVerified;
        this.avatarPath = avatarPath;
        this.adresseL1 = adresseL1;
        this.adresseL2 = adresseL2;
        this.codePostal = codePostal;
        this.ville = ville;
        this.paysCode = paysCode;
        this.lat = lat;
        this.lon = lon;
        this.lastLoginAt = lastLoginAt;
        this.passwordUpdatedAt = passwordUpdatedAt;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lockedUntil = lockedUntil;
        this.preferencesJson = preferencesJson;
        this.termsAcceptedAt = termsAcceptedAt;
        this.privacyConsentAt = privacyConsentAt;
        this.deletedAt = deletedAt;
    }

    // --------------------------------------------------------
    // Getters / Setters
    // --------------------------------------------------------

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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
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

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isTelephoneVerified() {
        return telephoneVerified;
    }

    public void setTelephoneVerified(boolean telephoneVerified) {
        this.telephoneVerified = telephoneVerified;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
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

    public OffsetDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(OffsetDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public OffsetDateTime getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }

    public void setPasswordUpdatedAt(OffsetDateTime passwordUpdatedAt) {
        this.passwordUpdatedAt = passwordUpdatedAt;
    }

    public short getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(short failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public OffsetDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(OffsetDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public String getPreferencesJson() {
        return preferencesJson;
    }

    public void setPreferencesJson(String preferencesJson) {
        this.preferencesJson = preferencesJson;
    }

    public OffsetDateTime getTermsAcceptedAt() {
        return termsAcceptedAt;
    }

    public void setTermsAcceptedAt(OffsetDateTime termsAcceptedAt) {
        this.termsAcceptedAt = termsAcceptedAt;
    }

    public OffsetDateTime getPrivacyConsentAt() {
        return privacyConsentAt;
    }

    public void setPrivacyConsentAt(OffsetDateTime privacyConsentAt) {
        this.privacyConsentAt = privacyConsentAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // --------------------------------------------------------
    // toString (utile pour les logs / debug)
    // --------------------------------------------------------

    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUser=" + idUser +
                ", email='" + email + '\'' +
                ", prenom='" + prenom + '\'' +
                ", nom='" + nom + '\'' +
                ", actif=" + actif +
                ", emailVerified=" + emailVerified +
                ", telephoneVerified=" + telephoneVerified +
                '}';
    }
}


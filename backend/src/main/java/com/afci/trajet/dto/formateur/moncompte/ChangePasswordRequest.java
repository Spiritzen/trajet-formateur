// src/main/java/com/afci/trajet/dto/formateur/moncompte/ChangePasswordRequest.java
package com.afci.trajet.dto.formateur.moncompte;

/**
 * DTO pour la demande de changement de mot de passe
 * depuis le "Mon compte" du formateur.
 */
public class ChangePasswordRequest {

    /** Ancien mot de passe saisi par le formateur. */
    private String currentPassword;

    /** Nouveau mot de passe souhaité. */
    private String newPassword;

    /** Confirmation du nouveau mot de passe. */
    private String confirmNewPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String currentPassword, String newPassword, String confirmNewPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmNewPassword() { return confirmNewPassword; }
    public void setConfirmNewPassword(String confirmNewPassword) { this.confirmNewPassword = confirmNewPassword; }
}

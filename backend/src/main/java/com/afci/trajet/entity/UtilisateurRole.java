// com/afci/trajet/entity/UtilisateurRole.java
package com.afci.trajet.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "utilisateur_role")
public class UtilisateurRole {

    @EmbeddedId
    private UtilisateurRoleId id;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UtilisateurRole() {}

    public UtilisateurRole(UtilisateurRoleId id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public UtilisateurRoleId getId() { return id; }
    public void setId(UtilisateurRoleId id) { this.id = id; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "UtilisateurRole{" +
               "idUser=" + (id != null ? id.getIdUser() : null) +
               ", idRole=" + (id != null ? id.getIdRole() : null) +
               ", createdAt=" + createdAt +
               '}';
    }
}

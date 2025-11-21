// com/afci/trajet/entity/UtilisateurRoleId.java
package com.afci.trajet.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UtilisateurRoleId implements Serializable {

    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "id_role")
    private Integer idRole;

    public UtilisateurRoleId() {}

    public UtilisateurRoleId(Integer idUser, Integer idRole) {
        this.idUser = idUser;
        this.idRole = idRole;
    }

    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }

    public Integer getIdRole() { return idRole; }
    public void setIdRole(Integer idRole) { this.idRole = idRole; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UtilisateurRoleId)) return false;
        UtilisateurRoleId that = (UtilisateurRoleId) o;
        return Objects.equals(idUser, that.idUser) &&
               Objects.equals(idRole, that.idRole);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, idRole);
    }
}

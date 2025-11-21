package com.afci.trajet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afci.trajet.entity.UtilisateurRole;
import com.afci.trajet.entity.UtilisateurRoleId;

public interface UtilisateurRoleRepository extends JpaRepository<UtilisateurRole, UtilisateurRoleId> {

    List<UtilisateurRole> findByIdIdUser(Integer idUser);

    List<UtilisateurRole> findByIdIdRole(Integer idRole);
}

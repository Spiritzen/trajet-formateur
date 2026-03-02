package com.afci.trajet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.afci.trajet.entity.UtilisateurRole;
import com.afci.trajet.entity.UtilisateurRoleId;

public interface UtilisateurRoleRepository extends JpaRepository<UtilisateurRole, UtilisateurRoleId> {

    List<UtilisateurRole> findByIdIdUser(Integer idUser);

    List<UtilisateurRole> findByIdIdRole(Integer idRole);
    
    /**
     * Recherche l'association UtilisateurRole correspondant
     * à un utilisateur et un code de rôle précis.
     *
     * On travaille en ID only :
     *  - ur.id.idUser  = colonne id_user
     *  - ur.id.idRole  = colonne id_role
     *  - on joint sur l'entité Role pour filtrer sur r.code
     */
    @Query("""
            SELECT ur
            FROM UtilisateurRole ur
            JOIN Role r
              ON r.idRole = ur.id.idRole
            WHERE ur.id.idUser = :idUser
              AND r.code = :code
           """)
    Optional<UtilisateurRole> findByUserIdAndRoleCode(
            @Param("idUser") Integer idUser,
            @Param("code")  String code
    );
}

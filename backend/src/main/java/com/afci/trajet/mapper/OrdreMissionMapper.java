package com.afci.trajet.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.afci.trajet.dto.om.OrdreMissionAssignFormateurRequest;
import com.afci.trajet.dto.om.OrdreMissionCreateRequest;
import com.afci.trajet.dto.om.OrdreMissionResponse;
import com.afci.trajet.dto.om.OrdreMissionUpdateRequest;
import com.afci.trajet.entity.OrdreMission;

/**
 * Mapper central pour l'entité OrdreMission.
 *
 * Rôle :
 *  - transformer les DTO "d'entrée" (Create / Update / Assign) en entité ;
 *  - transformer l'entité en DTO "de sortie" (Response).
 *
 * On garde une classe simple avec des méthodes statiques : pas d'état interne.
 */
public final class OrdreMissionMapper {

    private OrdreMissionMapper() {
        // utilitaire stateless
    }

    // ---------------------------------------------------------------------
    // 1️⃣ CreateRequest -> Entity
    // ---------------------------------------------------------------------

    /**
     * Crée une nouvelle entité OrdreMission à partir d'un DTO de création.
     *
     * Attention :
     *  - cette méthode NE positionne PAS :
     *      - idEcole
     *      - idUserCreateur
     *      - codeOrdre
     *      - statut
     *  Ces champs sont contextuels et gérés dans le service.
     */
    public static OrdreMission toEntityFromCreate(OrdreMissionCreateRequest dto) {
        if (dto == null) {
            return null;
        }

        OrdreMission om = new OrdreMission();

        om.setDateDebut(dto.getDateDebut());
        om.setDateFin(dto.getDateFin());
        om.setCoutTotalEstime(dto.getCoutTotalEstime());
        om.setCommentaire(dto.getCommentaire());

        // Le statut sera forcé à "BROUILLON" dans le service.
        // idEcole / idUserCreateur / codeOrdre seront également
        // définis dans le service.

        return om;
    }

    // ---------------------------------------------------------------------
    // 2️⃣ UpdateRequest -> Entity (update "in place")
    // ---------------------------------------------------------------------

    /**
     * Applique les modifications permises depuis un DTO de mise à jour
     * sur une entité OrdreMission existante.
     *
     * L'école ne peut modifier que :
     *  - dateDebut
     *  - dateFin
     *  - coutTotalEstime
     *  - commentaire
     *
     * On ne touche PAS à :
     *  - idEcole
     *  - idUserCreateur
     *  - idFormateur
     *  - statut
     */
    public static void updateEntityFromDto(OrdreMissionUpdateRequest dto, OrdreMission om) {
        if (dto == null || om == null) {
            return;
        }

        om.setDateDebut(dto.getDateDebut());
        om.setDateFin(dto.getDateFin());
        om.setCoutTotalEstime(dto.getCoutTotalEstime());
        om.setCommentaire(dto.getCommentaire());
    }

    // ---------------------------------------------------------------------
    // 3️⃣ Entity -> Response DTO
    // ---------------------------------------------------------------------

    /**
     * Transforme une entité OrdreMission en DTO de réponse "plat"
     * pour le front.
     */
    public static OrdreMissionResponse toResponse(OrdreMission om) {
        if (om == null) {
            return null;
        }

        OrdreMissionResponse dto = new OrdreMissionResponse();

        dto.setIdOrdreMission(om.getIdOrdreMission());
        dto.setCodeOrdre(om.getCodeOrdre());
        dto.setIdFormateur(om.getIdFormateur());
        dto.setIdEcole(om.getIdEcole());
        dto.setIdUserCreateur(om.getIdUserCreateur());
        dto.setIdUserValidateur(om.getIdUserValidateur());

        dto.setDateDebut(om.getDateDebut());
        dto.setDateFin(om.getDateFin());

        dto.setStatut(om.getStatut());
        dto.setCoutTotalEstime(om.getCoutTotalEstime());
        dto.setCommentaire(om.getCommentaire());
        dto.setIdTrajetRetenu(om.getIdTrajetRetenu());

        dto.setCreatedAt(om.getCreatedAt());
        dto.setUpdatedAt(om.getUpdatedAt());

        return dto;
    }

    /**
     * Transforme une liste d'entités OrdreMission en liste de DTO de réponse.
     */
    public static List<OrdreMissionResponse> toResponseList(List<OrdreMission> ordres) {
        if (ordres == null) {
            return List.of();
        }

        return ordres.stream()
                .map(OrdreMissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // 4️⃣ Helpers possibles pour d'autres cas
    // ---------------------------------------------------------------------

    /**
     * Applique la mise à jour "assign formateur" sur une entité OM existante.
     *
     * Ici on se contente de mettre à jour l'idFormateur,
     * le statut, et éventuellement un commentaire.
     */
    public static void applyAssignFormateur(OrdreMissionAssignFormateurRequest dto, OrdreMission om) {
        if (dto == null || om == null) {
            return;
        }

        om.setIdFormateur(dto.getIdFormateur());
        // Le nouveau statut ("PROPOSE", ...) est géré dans le service
        // pour rester maître de l'automate de statut.
    }
}

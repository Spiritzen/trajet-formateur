package com.afci.trajet.service.om;

import com.afci.trajet.dto.common.PageResponse;
import com.afci.trajet.dto.om.OrdreMissionAssignFormateurRequest;
import com.afci.trajet.dto.om.OrdreMissionCreateRequest;
import com.afci.trajet.dto.om.OrdreMissionResponse;
import com.afci.trajet.dto.om.OrdreMissionUpdateRequest;

import java.util.List;
import com.afci.trajet.dto.om.ecole.OmFormateurCandidatResponse;

/**
 * Interface de service pour la gestion des Ordres de Mission (OM).
 *
 * Cette interface décrit les cas d'usage côté ECOLE :
 *  - création d'un OM en mode BROUILLON ;
 *  - mise à jour d'un OM tant qu'il est en BROUILLON ;
 *  - listing des OM de l'école, avec filtre éventuel par statut (paginé) ;
 *  - consultation du détail d'un OM de l'école ;
 *  - affectation d'un formateur à un OM (BROUILLON -> PROPOSE).
 *
 * Les scénarios côté FORMATEUR (choix des trajets, validation, etc.)
 * seront ajoutés plus tard.
 */
public interface OrdreMissionService {

    /**
     * Création d'un nouvel ordre de mission pour une école (côté ECOLE).
     *
     * L'utilisateur est identifié par son email (Spring Security),
     * ce qui permet de retrouver l'école à laquelle il est rattaché.
     *
     * Règles métier :
     *  - statut initial : BROUILLON ;
     *  - pas encore de formateur affecté (idFormateur = null) ;
     *  - génération d'un code ordre (OM-AAAA-XXXX).
     */
    OrdreMissionResponse createOrdreMissionPourEcole(String emailConnected,
                                                     OrdreMissionCreateRequest request);

    /**
     * Mise à jour d'un ordre de mission en statut BROUILLON
     * pour l'école de l'utilisateur connecté.
     *
     * Champs modifiables :
     *  - dateDebut, dateFin
     *  - coutTotalEstime
     *  - commentaire
     */
    OrdreMissionResponse updateOrdreMissionBrouillonPourEcole(String emailConnected,
                                                               Integer idOrdreMission,
                                                               OrdreMissionUpdateRequest request);

    /**
     * Liste paginée des ordres de mission de l'école de l'utilisateur connecté.
     *
     * @param emailConnected email de l'utilisateur (contexte sécurité)
     * @param statut         statut optionnel pour filtrer (BROUILLON, PROPOSE, etc.)
     *                       - si null ou vide → tous les statuts
     * @param page           index de page (0-based)
     * @param size           taille de page
     */
    PageResponse<OrdreMissionResponse> listerOrdresMissionPourEcole(
            String emailConnected,
            String statut,
            int page,
            int size
    );

    /**
     * Récupération du détail d'un ordre de mission pour l'école
     * de l'utilisateur connecté.
     *
     * Vérifie que l'OM appartient bien à l'école.
     */
    OrdreMissionResponse getOrdreMissionDetailPourEcole(String emailConnected,
                                                        Integer idOrdreMission);

    /**
     * Affectation d'un formateur à un OM (côté ECOLE).
     *
     * Règles métier :
     *  - l'OM doit appartenir à l'école de l'utilisateur connecté ;
     *  - l'OM doit être en statut BROUILLON ;
     *  - après affectation, le statut passe à PROPOSE.
     */
    OrdreMissionResponse assignerFormateurPourOrdreMission(String emailConnected,
                                                            Integer idOrdreMission,
                                                            OrdreMissionAssignFormateurRequest request);

    List<OmFormateurCandidatResponse> findCandidatsPourOrdre(Integer idOrdreMission);

    OrdreMissionResponse affecterFormateur(Integer idOrdreMission, Integer idFormateur);

}

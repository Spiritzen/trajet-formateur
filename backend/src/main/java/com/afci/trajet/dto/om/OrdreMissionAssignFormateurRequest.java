// src/main/java/com/afci/trajet/dto/om/OrdreMissionAssignFormateurRequest.java
package com.afci.trajet.dto.om;

/**
 * DTO utilisé par l'ECOLE pour affecter un formateur à un Ordre de Mission.
 *
 * Règles métier :
 *  - L'OM doit appartenir à l'école connectée.
 *  - L'OM doit être au statut BROUILLON pour permettre l'affectation.
 *  - Après affectation, le service passera le statut à PROPOSE.
 */
public class OrdreMissionAssignFormateurRequest {

    /**
     * Identifiant du formateur choisi pour cette mission.
     */
    private Integer idFormateur;

    // --------------------------------------------------------
    // Constructeurs
    // --------------------------------------------------------

    public OrdreMissionAssignFormateurRequest() {
    }

    public OrdreMissionAssignFormateurRequest(Integer idFormateur) {
        this.idFormateur = idFormateur;
    }

    // --------------------------------------------------------
    // Getters / Setters
    // --------------------------------------------------------

    public Integer getIdFormateur() {
        return idFormateur;
    }

    public void setIdFormateur(Integer idFormateur) {
        this.idFormateur = idFormateur;
    }
}

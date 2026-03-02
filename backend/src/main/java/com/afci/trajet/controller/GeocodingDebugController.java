package com.afci.trajet.controller;

import com.afci.trajet.service.GeocodingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/debug/geocode")
public class GeocodingDebugController {

    private final GeocodingService geocodingService;

    public GeocodingDebugController(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    /**
     * Endpoint de test pour vérifier le géocodage d'une adresse.
     *
     * Exemple d'appel :
     * GET http://localhost:8080/api/debug/geocode
     *   ?adresseL1=15+rue+du+Progrès
     *   &codePostal=80090
     *   &ville=Amiens
     *   &paysCode=FR
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // à adapter si besoin
    public ResponseEntity<?> geocodeAdresse(
            @RequestParam String adresseL1,
            @RequestParam(required = false) String adresseL2,
            @RequestParam String codePostal,
            @RequestParam String ville,
            @RequestParam(defaultValue = "FR") String paysCode
    ) {

        // 1) Construire l’adresse complète avec le service
        String adresseComplete = geocodingService.buildAdresseComplete(
                adresseL1,
                adresseL2,
                codePostal,
                ville,
                paysCode
        );

        // 2) Appeler l’API de géocodage
        return geocodingService.geocodeAdresse(adresseComplete)
                .map(coords -> ResponseEntity.ok(
                        Map.of(
                                "adresseComplete", adresseComplete,
                                "lat", coords.lat(),   // BigDecimal
                                "lon", coords.lon()    // BigDecimal
                        )
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of(
                                "adresseComplete", adresseComplete,
                                "message", "Aucune coordonnée trouvée pour cette adresse."
                        )
                ));
    }
}

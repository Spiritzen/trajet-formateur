package com.afci.trajet.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.afci.trajet.service.DistanceService;

/**
 * Contrôleur DEBUG permettant de tester la distance entre deux points GPS.
 *
 * Exemple :
 * GET /api/debug/distance?lat1=49.894&lon1=2.295&lat2=49.889&lon2=2.309
 *
 * ⚠ Accessible uniquement par un ADMIN.
 */
@RestController
@RequestMapping("/api/debug")
public class GeocodingDistanceDebugController {

    private final DistanceService distanceService;

    public GeocodingDistanceDebugController(DistanceService distanceService) {
        this.distanceService = distanceService;
    }

    /**
     * Calcule la distance en kilomètres entre deux points GPS.
     *
     * @param lat1 latitude du point 1
     * @param lon1 longitude du point 1
     * @param lat2 latitude du point 2
     * @param lon2 longitude du point 2
     * @return JSON { distanceKm, point1, point2 }
     */
    @GetMapping("/distance")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> distance(
            @RequestParam BigDecimal lat1,
            @RequestParam BigDecimal lon1,
            @RequestParam BigDecimal lat2,
            @RequestParam BigDecimal lon2
    ) {

        double distanceKm = distanceService.distanceEnKm(lat1, lon1, lat2, lon2);

        return Map.of(
            "point1", Map.of("lat", lat1, "lon", lon1),
            "point2", Map.of("lat", lat2, "lon", lon2),
            "distanceKm", distanceKm
        );
    }
}

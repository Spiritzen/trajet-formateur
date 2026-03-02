package com.afci.trajet.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

/**
 * Service utilitaire pour calculer des distances entre deux points GPS.
 *
 * Utilise la formule de Haversine pour calculer la distance
 * "à vol d'oiseau" en kilomètres.
 *
 * ⚠ Les coordonnées sont en degrés (lat/lon).
 */
@Service
public class DistanceService {

    // Rayon moyen de la Terre en kilomètres
    private static final double RAYON_TERRE_KM = 6371.0;

    /**
     * Calcule la distance en kilomètres entre deux points GPS
     * (lat1, lon1) et (lat2, lon2) avec la formule de Haversine.
     *
     * @param lat1 latitude du point 1 (BigDecimal, en degrés)
     * @param lon1 longitude du point 1 (BigDecimal, en degrés)
     * @param lat2 latitude du point 2 (BigDecimal, en degrés)
     * @param lon2 longitude du point 2 (BigDecimal, en degrés)
     * @return distance en kilomètres (double)
     */
    public double distanceEnKm(BigDecimal lat1,
                               BigDecimal lon1,
                               BigDecimal lat2,
                               BigDecimal lon2) {

        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            throw new IllegalArgumentException("Les coordonnées lat/lon ne doivent pas être null.");
        }

        // 1) Conversion BigDecimal -> double
        double lat1Deg = lat1.doubleValue();
        double lon1Deg = lon1.doubleValue();
        double lat2Deg = lat2.doubleValue();
        double lon2Deg = lon2.doubleValue();

        // 2) Conversion degrés -> radians
        double lat1Rad = Math.toRadians(lat1Deg);
        double lon1Rad = Math.toRadians(lon1Deg);
        double lat2Rad = Math.toRadians(lat2Deg);
        double lon2Rad = Math.toRadians(lon2Deg);

        // 3) Différences
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // 4) Formule de Haversine
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 5) Distance finale
        return RAYON_TERRE_KM * c;
    }
}

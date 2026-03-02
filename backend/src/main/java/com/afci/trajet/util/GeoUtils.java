package com.afci.trajet.util;

import java.math.BigDecimal;

/**
 * Utilitaires géographiques (distance, conversions, etc.).
 */
public final class GeoUtils {

    private static final double RAYON_TERRE_KM = 6371.0;

    private GeoUtils() {
        // classe utilitaire, pas d'instanciation
    }

    /**
     * Distance "à vol d'oiseau" en kilomètres entre deux points GPS.
     * 
     * @param lat1 latitude du point 1 (BigDecimal, en degrés)
     * @param lon1 longitude du point 1 (BigDecimal, en degrés)
     * @param lat2 latitude du point 2 (BigDecimal, en degrés)
     * @param lon2 longitude du point 2 (BigDecimal, en degrés)
     * @return distance en kilomètres (double)
     */
    public static double distanceKm(BigDecimal lat1, BigDecimal lon1,
                                    BigDecimal lat2, BigDecimal lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            throw new IllegalArgumentException("Les lat/lon ne doivent pas être null");
        }

        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lon1Rad = Math.toRadians(lon1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double lon2Rad = Math.toRadians(lon2.doubleValue());

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAYON_TERRE_KM * c;
    }
}

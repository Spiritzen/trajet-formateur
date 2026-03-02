package com.afci.trajet.service;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingService {

    private static final Logger log = LoggerFactory.getLogger(GeocodingService.class);

    // URL de l’API Adresse
    private static final String API_URL = "https://api-adresse.data.gouv.fr/search/";

    private final RestTemplate restTemplate;

    public GeocodingService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    /**
     * Construit une adresse complète sous forme de chaîne lisible,
     * à partir des champs de l'école.
     *
     * Exemple : "20 rue Jules Barni, 80000 Amiens, FR"
     */
    public String buildAdresseComplete(
            String adresseL1,
            String adresseL2,
            String codePostal,
            String ville,
            String paysCode
    ) {
        StringBuilder sb = new StringBuilder();

        if (adresseL1 != null && !adresseL1.isBlank()) {
            sb.append(adresseL1.trim());
        }

        if (adresseL2 != null && !adresseL2.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(adresseL2.trim());
        }

        if (codePostal != null && !codePostal.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(codePostal.trim());
        }

        if (ville != null && !ville.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(ville.trim());
        }

        if (paysCode != null && !paysCode.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(paysCode.trim());
        }

        return sb.toString();
    }

    /**
     * Appelle l’API Adresse pour récupérer lat / lon à partir d'une adresse texte.
     *
     * Retourne Optional.empty() si aucune coordonnée trouvée ou en cas d’erreur.
     */
    @SuppressWarnings("unchecked")
    public Optional<Coords> geocodeAdresse(String adresseComplete) {
        try {
            // ⚠️ IMPORTANT : on laisse Spring gérer l’encodage
            // -> PAS de build(true), on fait build() puis encode()
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(API_URL)
                    .queryParam("q", adresseComplete)
                    .queryParam("limit", 1)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            log.info("Appel API Adresse : {}", uri);

            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.warn("Réponse non valide de l'API Adresse pour '{}': status={}",
                        adresseComplete, response.getStatusCode());
                return Optional.empty();
            }

            Map<String, Object> body = response.getBody();
            Object featuresObj = body.get("features");

            if (!(featuresObj instanceof List<?> features) || features.isEmpty()) {
                log.warn("Aucune feature retournée pour '{}'", adresseComplete);
                return Optional.empty();
            }

            Object firstFeature = features.get(0);
            if (!(firstFeature instanceof Map<?, ?> featureMapRaw)) {
                log.warn("Format inattendu de feature pour '{}'", adresseComplete);
                return Optional.empty();
            }

            Map<String, Object> featureMap = (Map<String, Object>) featureMapRaw;
            Object geometryObj = featureMap.get("geometry");

            if (!(geometryObj instanceof Map<?, ?> geometryRaw)) {
                log.warn("Geometry absente ou invalide pour '{}'", adresseComplete);
                return Optional.empty();
            }

            Map<String, Object> geometry = (Map<String, Object>) geometryRaw;
            Object coordsObj = geometry.get("coordinates");

            if (!(coordsObj instanceof List<?> coords) || coords.size() < 2) {
                log.warn("Coordonnées absentes ou incomplètes pour '{}'", adresseComplete);
                return Optional.empty();
            }

            // Format API Adresse : [lon, lat]
            BigDecimal lon = new BigDecimal(coords.get(0).toString());
            BigDecimal lat = new BigDecimal(coords.get(1).toString());

            log.info("Coordonnées trouvées pour '{}': lat={}, lon={}", adresseComplete, lat, lon);

            return Optional.of(new Coords(lat, lon));

        } catch (Exception ex) {
            log.error("Erreur lors de l'appel à l'API Adresse pour '{}'", adresseComplete, ex);
            return Optional.empty();
        }
    }

    /**
     * Petit record pour encapsuler proprement lat / lon.
     */
    public record Coords(BigDecimal lat, BigDecimal lon) {
    }
}

package com.afci.trajet.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Service utilitaire pour gérer les JSON Web Tokens (JWT).
 *
 * Rôle de cette classe :
 *  - Générer un JWT lors de la connexion (login)
 *  - Extraire des informations du JWT (email, date d'expiration, etc.)
 *  - Vérifier si un JWT est valide (signature correcte + non expiré)
 *
 * IMPORTANT :
 * ==========
 *  - Le JWT est signé avec une clé secrète HMAC (algorithme HS256).
 *  - Cette clé est configurée dans application.yml via la propriété :
 *        security.jwt.secret
 *  - Dans cette version, la clé est considérée comme une chaîne "normale"
 *    (UTF-8), PAS comme une chaîne encodée en Base64.
 *    => On NE fait plus de Decoders.BASE64.decode(...).
 */
@Service
public class JwtService {

    /**
     * Clé secrète utilisée pour signer les JWT.
     *
     * ATTENTION :
     *  - Cette chaîne doit être LONGUE et DIFFICILE à deviner.
     *  - Pour HS256, il est recommandé d'avoir au minimum 32 caractères.
     *  - Exemple de valeur (à mettre dans application.yml) :
     *
     *      security.jwt.secret: "MaSuperCleSecretePourTrajetFormateur_ChangeMoi"
     *
     *  - En production, cette valeur ne doit JAMAIS être commitée en clair
     *    dans le repository. Utiliser plutôt des variables d'environnement,
     *    un vault, etc.
     */
    private final String secret;

    /**
     * Durée de validité du token en millisecondes.
     *
     * Exemple :
     *  - 24h = 24 * 60 * 60 * 1000 = 86400000
     *
     * Cette valeur est également configurée dans application.yml :
     *
     *      security.jwt.expiration-ms: 86400000
     */
    private final long expirationMs;

    /**
     * Constructeur : Spring injecte automatiquement les valeurs définies
     * dans application.yml grâce à l'annotation @Value.
     */
    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    // ==========================================================
    //                 1. GÉNÉRATION DU TOKEN
    // ==========================================================

    /**
     * Génère un JWT pour l'utilisateur connecté.
     *
     * Utilisation typique :
     *  - Dans AuthService, après authentification du user (email + mot de passe),
     *    on appelle :
     *        String token = jwtService.generateToken(userDetails);
     *  - Ce token est ensuite renvoyé au frontend, qui le stocke (localStorage,
     *    cookie, etc.) et le renvoie dans le header Authorization: Bearer <token>
     *    sur les prochaines requêtes.
     *
     * Contenu du token :
     *  - subject = username de Spring Security, ici = email de l'utilisateur
     *  - iat (issued at) = date de création
     *  - exp (expiration) = date d'expiration (now + expirationMs)
     */
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();                              // date actuelle
        Date expiration = new Date(now.getTime() + expirationMs); // date de fin de validité

        return Jwts.builder()
                .setSubject(userDetails.getUsername())      // subject = email
                .setIssuedAt(now)                           // iat
                .setExpiration(expiration)                  // exp
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // signature HMAC
                .compact();                                 // sérialisation finale du JWT (String)
    }

    // ==========================================================
    //              2. EXTRACTION D'INFORMATIONS
    // ==========================================================

    /**
     * Extrait l'email (subject) à partir d'un token.
     *
     * Rappel : lors de la génération, on a mis :
     *      setSubject(userDetails.getUsername())
     * et le username correspond à l'email de l'utilisateur.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Méthode générique permettant d'extraire n'importe quel "claim"
     * du JWT en passant une fonction de transformation.
     *
     * Exemple :
     *  - extractClaim(token, Claims::getSubject)     -> subject (email)
     *  - extractClaim(token, Claims::getExpiration)  -> date d'expiration
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Récupère tous les "claims" (le contenu du payload) en vérifiant la
     * signature avec la clé.
     *
     * Si la signature est invalide, un parsing error sera levé par la
     * librairie jjwt.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey()) // vérifie la signature
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Construit la clé de signature HMAC à partir de la chaîne secrète.
     *
     * POINT IMPORTANT :
     * -----------------
     *  - Avant : on considérait que "secret" était encodé en Base64 → on faisait
     *        Decoders.BASE64.decode(secret)
     *
     *  - Problème : si la chaîne contient des caractères non autorisés en Base64
     *    (comme '-'), on obtient l'erreur :
     *        Illegal base64 character: '-'
     *
     *  - Maintenant : on considère que "secret" est simplement une chaîne UTF-8
     *    classique, suffisamment longue et aléatoire.
     *    On la convertit en bytes avec UTF-8 puis on construit la clé HMAC.
     */
    private Key getSigningKey() {
        // On prend les octets UTF-8 de la chaîne "secret"
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        // On construit une clé HMAC-SHA adaptée à la longueur de la clé
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ==========================================================
    //                    3. VALIDATION DU TOKEN
    // ==========================================================

    /**
     * Vérifie si un token JWT est valide pour un utilisateur donné :
     *  - la signature doit être correcte (sinon parsing error)
     *  - le token ne doit pas être expiré
     *  - l'email dans le token doit correspondre à l'email de l'utilisateur
     *
     * Cette méthode est typiquement utilisée dans le filtre JWT :
     *  - on récupère le token dans le header Authorization: Bearer <token>
     *  - on en extrait l'email
     *  - on charge l'utilisateur (UserDetails)
     *  - on appelle isTokenValid(token, userDetails)
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equalsIgnoreCase(userDetails.getUsername())
                    && !isTokenExpired(token);
        } catch (Exception e) {
            // Si le parsing du token échoue (signature invalide, token mal formé, etc.)
            // on considère que le token est invalide.
            return false;
        }
    }

    /**
     * Indique si le token est expiré (true = expiré).
     */
    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }
}

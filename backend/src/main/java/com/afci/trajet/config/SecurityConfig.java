package com.afci.trajet.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.afci.trajet.security.jwt.JwtAuthenticationFilter;

/**
 * Configuration centrale de Spring Security (stateless, JWT).
 *
 * Rôles principaux :
 *  - déclarer les URL publiques vs protégées ;
 *  - brancher le filtre JWT dans la chaîne de filtres ;
 *  - configurer CORS pour permettre à un front (React) sur un autre port d’appeler l’API ;
 *  - désactiver la gestion de session côté serveur (mode 100 % stateless).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Chaîne de filtres principale de Spring Security.
     * C'est ici que l'on branche le filtre JWT et que l'on décrit les règles d'accès.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 1) Pas de CSRF : on expose une API REST stateless consommée par un front JS.
            .csrf(csrf -> csrf.disable())

            // 2) CORS : autorisation des appels depuis le front (port différent).
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 3) Mode stateless : Spring Security ne crée PAS de session HTTP côté serveur.
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 4) Gestion des erreurs de sécurité (401 vs 403).
            .exceptionHandling(ex -> ex
                // Utilisateur non authentifié, JWT absent/erroné → 401
                .authenticationEntryPoint(authenticationEntryPoint())
                // Utilisateur authentifié mais sans les droits → 403
                .accessDeniedHandler(accessDeniedHandler())
            )

            // 5) Règles d'autorisation URL par URL.
            .authorizeHttpRequests(auth -> auth
                // Health checks (pour monitoring / tests simples).
                .requestMatchers("/actuator/health/**", "/api/health/**").permitAll()

                // Authentification : login et refresh sont publics.
                // - /api/auth/login   : l'utilisateur envoie email + password
                // - /api/auth/refresh : le navigateur envoie automatiquement le refresh token
                .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()

                // Préflight CORS (OPTIONS) : toujours autorisé.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Toute autre requête doit être authentifiée par un JWT valide.
                .anyRequest().authenticated()
            )

            // 6) Service pour charger les utilisateurs (par email) lors de l'authentification.
            .userDetailsService(userDetailsService)

            // 7) Insertion du filtre JWT AVANT le filtre standard UsernamePasswordAuthenticationFilter.
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Encoder de mot de passe : BCrypt (recommandé pour les mots de passe utilisateurs).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager utilisé dans AuthService/AuthController pour déclencher
     * l'authentification avec email + mot de passe.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Configuration CORS :
     *  - en dev : on autorise toutes les origines (pattern "*") pour simplifier ;
     *  - en prod : à restreindre à la véritable URL du front (ex. https://trajet.afci.fr).
     *
     * allowCredentials(true) est important si on utilise des cookies (refresh token HttpOnly).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        // Autorise l'envoi de cookies (pour le refresh token HttpOnly)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // On applique cette config à toutes les routes de l'API.
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Réponse standard renvoyée quand l'utilisateur n'est PAS authentifié.
     * Exemple : pas de JWT, JWT expiré ou invalide, etc.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
        };
    }

    /**
     * Réponse standard renvoyée quand l'utilisateur est authentifié mais n'a
     * pas les droits suffisants pour accéder à une ressource.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Forbidden\"}");
        };
    }
}

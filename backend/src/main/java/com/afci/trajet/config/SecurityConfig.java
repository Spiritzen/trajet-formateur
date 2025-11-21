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
 * - configure les URL publiques / protégées
 * - ajoute le filtre JWT dans la chaîne de filtres
 * - définit le CORS pour le futur frontend React
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
     * Chaîne de filtres principale.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // On désactive CSRF car on expose une API stateless (pas de session, pas de cookie).
            .csrf(csrf -> csrf.disable())

            // Gestion CORS (frontend sur un autre port/domain).
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Pas de session HTTP côté serveur : tout est porté par le JWT.
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Gestion des exceptions d'accès.
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )

            // Définition des droits d'accès URL par URL.
            .authorizeHttpRequests(auth -> auth
                // Actuator & health check
                .requestMatchers("/actuator/health/**", "/api/health/**").permitAll()

                // Auth / login (quand on créera AuthController)
                .requestMatchers("/api/auth/login").permitAll()

                // OPTIONS pour CORS (préflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Tout le reste doit être authentifié
                .anyRequest().authenticated()
            )

            // On indique à Security comment charger les utilisateurs
            .userDetailsService(userDetailsService)

            // On insère notre filtre JWT AVANT le filtre standard UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Encoder de mot de passe : BCrypt (standard de facto).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager, utilisé notamment dans AuthController (login).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Configuration CORS de base (à adapter pour ton futur front : ports, origins).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Pour le dev : on autorise tout. En prod, restreindre les origines.
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Appliqué sur toutes les routes
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Réponse standard quand l'utilisateur n'est PAS authentifié
     * (pas de JWT, token invalide, etc.).
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
     * Réponse standard quand l'utilisateur est authentifié mais n'a pas les droits.
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

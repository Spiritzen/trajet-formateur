package com.afci.trajet.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.afci.trajet.dto.AuthRequest;
import com.afci.trajet.dto.AuthResponse;
import com.afci.trajet.entity.Utilisateur;
import com.afci.trajet.repository.UtilisateurRepository;
import com.afci.trajet.security.jwt.JwtService;

/**
 * Service métier pour tout ce qui concerne l'authentification :
 * - Vérifier email/mot de passe via Spring Security
 * - Générer un JWT
 * - Mettre à jour les infos de connexion (lastLoginAt ...)
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UtilisateurRepository utilisateurRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Authentifie l'utilisateur à partir de l'email et du password,
     * génère un JWT et renvoie un AuthResponse complet.
     */
    @Transactional
    public AuthResponse login(AuthRequest request) {

        // 1) On délègue à Spring Security la vérification email / mot de passe.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2) Si on arrive ici, l'authentification est OK.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3) On récupère l'entité Utilisateur pour renvoyer des infos métier (id, prénom, nom...)
        Utilisateur utilisateur = utilisateurRepository
                .findByEmailIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException(
                        "Utilisateur introuvable alors que l'authentification a réussi : " + userDetails.getUsername()
                ));

        // 4) On génère le JWT à partir des UserDetails.
        //    (si ton JwtService prend d'autres paramètres, adapte ici)
        String token = jwtService.generateToken(userDetails);

        // 5) On met à jour la date de dernière connexion.
        utilisateur.setLastLoginAt(OffsetDateTime.now());
        utilisateurRepository.save(utilisateur);

        // 6) On extrait les rôles (ROLE_ADMIN, ROLE_FORMATEUR...) des authorities
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 7) On construit la réponse pour le frontend.
        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setTokenType("Bearer");
        response.setUserId(utilisateur.getIdUser());
        response.setEmail(utilisateur.getEmail());
        response.setPrenom(utilisateur.getPrenom());
        response.setNom(utilisateur.getNom());
        response.setRoles(roles);

        return response;
    }
}

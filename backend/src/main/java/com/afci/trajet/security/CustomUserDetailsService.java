package com.afci.trajet.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.afci.trajet.entity.Role;
import com.afci.trajet.entity.Utilisateur;
import com.afci.trajet.entity.UtilisateurRole;
import com.afci.trajet.entity.UtilisateurRoleId;
import com.afci.trajet.repository.RoleRepository;
import com.afci.trajet.repository.UtilisateurRepository;
import com.afci.trajet.repository.UtilisateurRoleRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurRoleRepository utilisateurRoleRepository;
    private final RoleRepository roleRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository,
                                    UtilisateurRoleRepository utilisateurRoleRepository,
                                    RoleRepository roleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurRoleRepository = utilisateurRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username = email
        Utilisateur utilisateur = utilisateurRepository
                .findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));

        // Rôles => authorities
        List<GrantedAuthority> authorities = loadAuthoritiesForUser(utilisateur.getIdUser());

        boolean actif = utilisateur.isActif();

        // On construit un User de Spring Security (objet interne à Spring)
        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getPasswordHash())
                .authorities(authorities)
                .accountLocked(!actif)
                .disabled(!actif)
                .build();
    }

    private List<GrantedAuthority> loadAuthoritiesForUser(Integer idUser) {
        List<UtilisateurRole> liens = utilisateurRoleRepository.findByIdIdUser(idUser);

        return liens.stream()
                .map(UtilisateurRole::getId)
                .map(UtilisateurRoleId::getIdRole)
                .distinct()
                .map(roleId -> roleRepository.findById(roleId).orElse(null))
                .filter(role -> role != null)
                .map(Role::getCode)              // ADMIN, FORMATEUR, ...
                .distinct()
                .map(code -> "ROLE_" + code)    // ROLE_ADMIN, ROLE_FORMATEUR, ...
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}

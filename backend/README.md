Trajet Formateur – Documentation Sécurité & Authentification (JWT)

1. Objectif
Ce document décrit l’architecture de sécurité du backend Trajet Formateur :
- Entités sécurité (Utilisateur, Role, UtilisateurRole)
- Spring Security (stateless, CORS, filters)
- JWT (génération, validation)
- Authentification (/api/auth/login)
- Configuration du projet
- Bonnes pratiques

2. Vue d’ensemble
Le backend utilise :
- Spring Boot 3.5
- Spring Security 6
- JWT (HS256)
- BCryptPasswordEncoder
- PostgreSQL
- Architecture stateless (aucune session)

3. Architecture d’authentification
1) L’utilisateur envoie email + password sur /api/auth/login
2) AuthService vérifie l’utilisateur + BCrypt.matches()
3) Les rôles sont récupérés via UtilisateurRoleRepository
4) JwtService génère un token HS256
5) Le contrôleur renvoie : accessToken, type, userId, email, prenom, nom, roles
6) Le frontend place ce token dans les requêtes -> Authorization: Bearer <token>

4. Fichiers clés

4.1 SecurityConfig.java
- Désactive CSRF
- Active CORS global
- Configure stateless: session = STATELESS
- Insère le filtre JwtAuthenticationFilter avant UsernamePasswordAuthenticationFilter
- Déclare AuthenticationManager + PasswordEncoder

4.2 JwtAuthenticationFilter.java
- Intercepte chaque requête HTTP
- Lit l’en-tête Authorization
- Extrait 'Bearer <token>'
- Utilise JwtService pour valider le token
- Récupère UserDetails via CustomUserDetailsService
- Remplit le SecurityContext pour la requête en cours

4.3 JwtService.java
- Génère un JWT avec HS256
- Décode la clé Base64
- Vérifie expiration / signature
- Extrait subject (email)
- Documenté pour que l'équipe comprenne le fonctionnement

4.4 CustomUserDetailsService.java
- Charge un Utilisateur depuis la base via email
- Charge ses rôles via UtilisateurRoleRepository
- Convertit en GrantedAuthority: ROLE_ADMIN, ROLE_GESTIONNAIRE, ROLE_FORMATEUR, etc.

4.5 AuthService.java
- Vérifie email/password grâce à BCryptPasswordEncoder
- Met à jour last_login_at
- Construit le DTO de réponse avec les rôles

4.6 AuthController.java
Route POST /api/auth/login :
- Appelle AuthService
- Retourne AuthResponse DTO
Exemple retourné :
{
  "accessToken": "xxxxx.yyyyy.zzzzz",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "admin@trajet.afci.test",
  "prenom": "Alice",
  "nom": "Admin",
  "roles": ["ROLE_ADMIN"]
}

5. Rôles & Permissions
Les rôles proviennent de la table role + jointure utilisateur_role.
Ils sont exposés dans Spring Security sous forme de GrantedAuthority = ROLE_XXX.

Exemple annotation :
@PreAuthorize("hasRole('ADMIN')")

6. Configuration application.yml
security:
  jwt:
    secret: <clé Base64 valide 256 bits>
    expiration-ms: 3600000

La clé secrète doit être encodée en Base64 et longue (>= 32 bytes).

7. Erreurs fréquentes corrigées
- Mauvaise clé Base64 → "Illegal base64 character"
- Mot de passe non hashé → BCryptPasswordEncoder.matches() échoue
- preferences_json (jsonb) ne doit jamais être mis à "" vide
- Manque du filtre jwt dans la chain → 403 systématique

8. Bonnes pratiques
- Toujours hasher les mots de passe via BCrypt
- Ne jamais stocker les tokens en base
- Toujours appliquer Bearer correctement
- Aucun accès sans JWT dans les routes protégées
- Ajouter un refresh token ultérieurement

Fin du document.

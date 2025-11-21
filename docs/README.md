# Dossier `docs/` – Trajet Formateur

Ce dossier contient **les documents de référence fonctionnelle et technique** du projet Trajet Formateur.

## 1. Contenu du dossier

- `dictionnaryData.ods`  
  Dictionnaire de données principal (format LibreOffice / OpenOffice Calc).  
  Chaque feuille décrit une ou plusieurs tables de la base PostgreSQL.

- (optionnel) `dictionnaryData.pdf`  
  Export du dictionnaire de données en PDF pour lecture rapide par le formateur / jury.

- Ce fichier `README.md`  
  Explique la démarche et les conventions utilisées.

## 2. Objectif du dictionnaire de données

Le dictionnaire de données (DD) est **la vérité fonctionnelle** sur :

- quelles tables existent,
- quelles colonnes,
- quels types (PostgreSQL),
- quelles contraintes (PK, FK, NOT NULL, UNIQUE, CHECK…),
- et à quoi sert chaque champ (description + exemples).

Il sert de base à :

- la modélisation MCD/MLD,
- l’écriture des scripts SQL (`01_create_tables.sql`, `02_alter_fk.sql`, etc.),
- la création des entités JPA dans le backend Spring Boot,
- la compréhension globale par un développeur junior ou par le jury CDA.

## 3. Format des tableaux dans le DD

Chaque table est décrite avec les colonnes suivantes (une ligne = une colonne SQL) :

- **colonne** : nom exact de la colonne dans la base (`id_user`, `email`, `id_role`, etc.)
- **type** : type SQL PostgreSQL (`INTEGER`, `VARCHAR(150)`, `TIMESTAMPTZ`, `JSONB`, `UUID`…)
- **PK** : indique si la colonne fait partie de la **clé primaire** (✅ / vide)
- **Unique** : indique si la colonne (ou combinaison) doit être **unique**
- **Null ?** : `❌` si `NOT NULL`, `✅` si `NULL` autorisé
- **Défaut** : valeur par défaut SQL (`now()`, `true`, `false`, `identity`, etc.)
- **Description** : rôle fonctionnel du champ, en français clair
- **Exemple** : valeur de test réaliste (utile aux juniors)
- **Contraintes (principales)** : résumé des contraintes importantes  
  (PK, FK, `CHECK`, `CHAR_LENGTH`, logique métier, etc.)

Exemple pour la table `utilisateur` :

- clé primaire : `id_user` (auto-incrément)
- `email` : `VARCHAR(150)`, `UNIQUE`, `NOT NULL`, format email
- `password_hash` : hash sécurisé (BCrypt / Argon2), jamais le mot de passe en clair
- colonnes d’audit : `created_at`, `updated_at`, `last_login_at`, etc.
- colonnes RGPD : `terms_accepted_at`, `privacy_consent_at`, `deleted_at`

## 4. Découpage logique des tables

Le modèle est organisé en 3 grands blocs :

### 4.1. Bloc Sécurité & Identité

Tables :

- `utilisateur`
- `role`
- `utilisateur_role`
- `refresh_token`

Objectif :

- gérer les comptes utilisateurs (ADMIN, GESTIONNAIRE, FORMATEUR, ECOLE),
- centraliser l’authentification (email + mot de passe),
- gérer la sécurité JWT (access token + refresh token),
- permettre plusieurs rôles par utilisateur si besoin (même si on commence simple).

### 4.2. Bloc Profils & Accessibilité

Tables :

- `formateur`
- `ecole`
- `responsable_accessibilite`

Objectif :

- stocker le profil de mobilité du formateur (zone en km, véhicule, permis, dispos),
- décrire les écoles (adresse, géolocalisation, accessibilité),
- avoir un contact dédié pour l’accessibilité sur site.

### 4.3. Bloc Missions & Trajets

Tables :

- `ordre_mission`
- `trajet`
- `correspondance`
- `signature`
- `notification`

Objectif :

- créer des **ordres de mission** pour un formateur vers une école,
- proposer plusieurs **trajets** (voiture, train, combiné…),
- décrire les **correspondances** (train + bus + marche),
- gérer la **signature** de l’ordre de mission (PDF, état de signature),
- tracer les **notifications** (SMS / email) envoyées (Twilio, etc.).

## 5. Conventions de nommage (SQL / JPA)

### 5.1. Noms de tables

- snake_case, **singulier** :
  - `utilisateur`, `formateur`, `ecole`, `ordre_mission`, `trajet`, `notification`…
- tables de jointure : `utilisateur_role` (composition des deux noms).

### 5.2. Clés primaires

- toujours de la forme : `id_<table>`  
  Exemples :
  - `id_user` pour `utilisateur`
  - `id_role` pour `role`
  - `id_formateur` pour `formateur`
  - `id_ordre_mission` pour `ordre_mission`

En JPA (Java), ça deviendra par exemple :

- champ `idUser` dans la classe `Utilisateur`
- champ `idRole` dans la classe `Role`, etc.

### 5.3. Relations / clés étrangères

- colonnes FK nommées avec le **préfixe `id_` + table cible** :
  - `id_user` dans `formateur`
  - `id_ecole` dans `ordre_mission`
  - `id_trajet` dans `correspondance`, etc.

- les contraintes FK seront déclarées dans les scripts SQL :  
  `FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON UPDATE CASCADE ON DELETE CASCADE`

### 5.4. Types PostgreSQL

- chaînes : `VARCHAR(n)` avec une taille réfléchie (`150` pour email, `255` pour chemins, etc.)
- booléens : `BOOLEAN`
- dates / heures : `TIMESTAMPTZ` (date + heure + fuseau)
- nombres :
  - `INTEGER`, `SMALLINT`
  - `NUMERIC(p,s)` pour les montants (`NUMERIC(10,2)`)
- JSON : `JSONB` quand on veut de la flexibilité contrôlée (`preferences_json`, etc.)
- identifiants de tokens : `UUID` (`id_refresh_token`)

## 6. Lien avec la suite du projet

À partir de ce dossier `docs/` :

1. On génère :
   - les scripts SQL (`01_create_tables.sql`, `02_alter_fk.sql`, `03_insert_data.sql`)
   - le MCD / MLD dans l’outil de modélisation
2. On crée les entités JPA dans le backend Spring Boot, en respectant :
   - même noms de colonnes
   - mêmes types
   - mêmes contraintes (NOT NULL, UNIQUE, etc.)
3. On s’appuie sur ce DD pour :
   - les API REST
   - la validation côté backend
   - les écrans du frontend (React + Vite)

---

## 👥 Mainteneurs
- **Conception technique & modélisation :** Spiritzen & Partenaire IA
- **Référent pédagogique :** Nassim Chabanne (AFCI)
- **Contributeurs :** CDA AFCI — Promotion 2025

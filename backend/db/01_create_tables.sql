-- ============================================================
-- 01_create_tables.sql
-- Schéma PostgreSQL – Projet "Trajet Formateur"
-- Auteur : Sébastien Cantrelle & Partenaire IA
-- Source : dictionnaryData.ods + README (docs/)
-- ============================================================

-- Optionnel : schéma cible
-- SET search_path TO public;

-- ============================================================
-- TABLE : utilisateur
-- Bloc : Sécurité & Identité
-- ============================================================
CREATE TABLE IF NOT EXISTS utilisateur (
    id_user               INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email                 VARCHAR(150)        NOT NULL,
    password_hash         VARCHAR(255)       NOT NULL,
    prenom                VARCHAR(100)       NOT NULL,
    nom                   VARCHAR(100)       NOT NULL,
    telephone             VARCHAR(20),
    actif                 BOOLEAN            NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMPTZ        NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ        NOT NULL DEFAULT now(),
    email_verified        BOOLEAN            NOT NULL DEFAULT FALSE,
    telephone_verified    BOOLEAN            NOT NULL DEFAULT FALSE,
    avatar_path           VARCHAR(255),
    adresse_l1            VARCHAR(200),
    adresse_l2            VARCHAR(200),
    code_postal           VARCHAR(10),
    ville                 VARCHAR(120),
    pays_code             CHAR(2),
    lat                   NUMERIC(9,6),
    lon                   NUMERIC(9,6),
    last_login_at         TIMESTAMPTZ,
    password_updated_at   TIMESTAMPTZ,
    failed_login_attempts SMALLINT           NOT NULL DEFAULT 0,
    locked_until          TIMESTAMPTZ,
    preferences_json      JSONB,
    terms_accepted_at     TIMESTAMPTZ,
    privacy_consent_at    TIMESTAMPTZ,
    deleted_at            TIMESTAMPTZ,

    -- Contraintes "simples" issues du DD
    CHECK (pays_code IS NULL OR pays_code ~ '^[A-Z]{2}$'),
    CHECK (lat IS NULL OR (lat BETWEEN -90 AND 90)),
    CHECK (lon IS NULL OR (lon BETWEEN -180 AND 180)),
    CHECK (
        email ~* '^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$'
    )
    -- ⚠️ L’unicité logique sur LOWER(email) avec deleted_at IS NULL
    -- sera créée en index partiel dans 02_alter_fk.sql (pas ici).
);

-- ============================================================
-- TABLE : role
-- Bloc : Sécurité & Identité
-- ============================================================
CREATE TABLE IF NOT EXISTS role (
    id_role     INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code        VARCHAR(30)      NOT NULL,
    libelle     VARCHAR(100)     NOT NULL,
    description VARCHAR(255),
    actif       BOOLEAN          NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ      NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ      NOT NULL DEFAULT now(),

    CONSTRAINT uq_role_code UNIQUE (code),
    CHECK (code = UPPER(code)),
    CHECK (code IN ('ADMIN','GESTIONNAIRE','FORMATEUR','ECOLE'))
);

-- ============================================================
-- TABLE : utilisateur_role
-- Jointure N–N utilisateur / role
-- ============================================================
CREATE TABLE IF NOT EXISTS utilisateur_role (
    id_user    INTEGER       NOT NULL,
    id_role    INTEGER       NOT NULL,
    created_at TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_utilisateur_role PRIMARY KEY (id_user, id_role)
    -- FK + contraintes métier (rôles) dans 02_alter_fk.sql
);

-- ============================================================
-- TABLE : refresh_token
-- Gestion des refresh tokens (JWT)
-- ============================================================
CREATE TABLE IF NOT EXISTS refresh_token (
    id_token    UUID           PRIMARY KEY,
    id_user     INTEGER        NOT NULL,
    token       VARCHAR(512)   NOT NULL,
    issued_at   TIMESTAMPTZ    NOT NULL,
    expires_at  TIMESTAMPTZ    NOT NULL,
    revoked     BOOLEAN        NOT NULL DEFAULT FALSE,
    user_agent  VARCHAR(255),
    ip_address  VARCHAR(45),
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT now(),
    revoked_at  TIMESTAMPTZ,

    CONSTRAINT uq_refresh_token_token UNIQUE (token),
    CHECK (expires_at >= issued_at)
    -- FK vers utilisateur + index/partial UNIQUE éventuels dans 02_alter_fk.sql
);

-- ============================================================
-- TABLE : formateur
-- Bloc : Profils & Accessibilité
-- ============================================================
CREATE TABLE IF NOT EXISTS formateur (
    id_formateur       INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_user            INTEGER       NOT NULL,
    zone_km            SMALLINT      NOT NULL,
    vehicule_perso     BOOLEAN       NOT NULL DEFAULT FALSE,
    permis             BOOLEAN       NOT NULL DEFAULT FALSE,
    mobilite_pref_json JSONB,
    disponibilite_json JSONB,
    commentaire        TEXT,
    created_at         TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CHECK (zone_km BETWEEN 0 AND 500)
    -- UNIQUE(id_user) + FK vers utilisateur dans 02_alter_fk.sql
);

-- ============================================================
-- TABLE : ecole
-- Bloc : Profils & Accessibilité
-- ============================================================
CREATE TABLE IF NOT EXISTS ecole (
    id_ecole              INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_user               INTEGER        NOT NULL,
    nom_ecole             VARCHAR(150)   NOT NULL,
    adresse_l1            VARCHAR(200)   NOT NULL,
    adresse_l2            VARCHAR(200),
    code_postal           VARCHAR(10)    NOT NULL,
    ville                 VARCHAR(120)   NOT NULL,
    pays_code             CHAR(2)        NOT NULL DEFAULT 'FR',
    lat                   NUMERIC(9,6),
    lon                   NUMERIC(9,6),
    niveau_accessibilite  VARCHAR(10)    NOT NULL DEFAULT 'MOYENNE',
    infos_acces           TEXT,
    created_at            TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ    NOT NULL DEFAULT now(),

    CHECK (pays_code ~ '^[A-Z]{2}$'),
    CHECK (lat IS NULL OR (lat BETWEEN -90 AND 90)),
    CHECK (lon IS NULL OR (lon BETWEEN -180 AND 180)),
    CHECK (UPPER(niveau_accessibilite) IN ('FACILE','MOYENNE','DIFFICILE'))
    -- UNIQUE (nom_ecole, code_postal, ville) + FK vers utilisateur dans 02_alter_fk.sql
);

-- ============================================================
-- TABLE : responsable_accessibilite
-- Bloc : Profils & Accessibilité
-- ============================================================
CREATE TABLE IF NOT EXISTS responsable_accessibilite (
    id_responsable  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_ecole        INTEGER       NOT NULL,
    nom             VARCHAR(100)  NOT NULL,
    prenom          VARCHAR(100)  NOT NULL,
    fonction        VARCHAR(100),
    telephone       VARCHAR(20),
    email           VARCHAR(150),
    plage_horaire   VARCHAR(100),
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now()
    -- FK vers ecole dans 02_alter_fk.sql
);

-- ============================================================
-- TABLE : ordre_mission
-- Bloc : Missions & Trajets
-- ============================================================
CREATE TABLE IF NOT EXISTS ordre_mission (
    id_ordre_mission   INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code_ordre         VARCHAR(30)      NOT NULL,
    id_formateur       INTEGER         NOT NULL,
    id_ecole           INTEGER         NOT NULL,
    id_user_createur   INTEGER         NOT NULL,
    id_user_validateur INTEGER,
    date_debut         DATE            NOT NULL,
    date_fin           DATE            NOT NULL,
    statut             VARCHAR(24)     NOT NULL DEFAULT 'BROUILLON',
    cout_total_estime  NUMERIC(10,2),
    commentaire        TEXT,
    id_trajet_retenu   INTEGER,
    created_at         TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT uq_ordre_mission_code UNIQUE (code_ordre),
    CHECK (date_fin IS NULL OR date_debut <= date_fin),
    CHECK (UPPER(statut) IN (
        'BROUILLON',
        'PROPOSE',
        'EN_ATTENTE_VALIDATION',
        'VALIDE',
        'SIGNE',
        'CLOTURE',
        'REJETE'
    )),
    CHECK (cout_total_estime IS NULL OR cout_total_estime >= 0)
    -- FKs vers formateur, ecole, utilisateur, trajet + partial UNIQUE (id_ordre_mission) WHERE retenu = true dans 02_alter_fk.sql
);

-- ============================================================
-- TABLE : trajet
-- Bloc : Missions & Trajets
-- ============================================================
CREATE TABLE IF NOT EXISTS trajet (
    id_trajet        INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_ordre_mission INTEGER       NOT NULL,
    moyen_principal  VARCHAR(15)   NOT NULL,
    distance_km      NUMERIC(7,2),
    duree_min        INTEGER,
    cout_estime      NUMERIC(10,2),
    itineraire_json  JSONB,
    propose_par      VARCHAR(15)   NOT NULL DEFAULT 'FORMATEUR',
    retenu           BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CHECK (UPPER(moyen_principal) IN (
        'VOITURE','TRAIN','BUS','METRO','TRAM',
        'AVION','COVOITURAGE','VELO','MARCHE','TAXI'
    )),
    CHECK (distance_km IS NULL OR distance_km >= 0),
    CHECK (duree_min IS NULL OR duree_min >= 0),
    CHECK (cout_estime IS NULL OR cout_estime >= 0),
    CHECK (UPPER(propose_par) IN ('FORMATEUR','SYSTEME','GESTIONNAIRE'))
    -- FK vers ordre_mission + partial UNIQUE (id_ordre_mission) WHERE retenu = true dans 02_alter_fk.sql
);

-- ============================================================
-- TABLE : correspondance
-- Bloc : Missions & Trajets
-- ============================================================
CREATE TABLE IF NOT EXISTS correspondance (
    id_corresp      INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_trajet       INTEGER       NOT NULL,
    ordre           SMALLINT      NOT NULL,
    type_transport  VARCHAR(10)   NOT NULL,
    duree_min       INTEGER,
    cout            NUMERIC(10,2),
    details         VARCHAR(200),
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CHECK (ordre >= 1),
    CHECK (UPPER(type_transport) IN (
        'TRAIN','BUS','METRO','TRAM','AVION','MARCHE','VOITURE','TAXI'
    )),
    CHECK (duree_min IS NULL OR duree_min >= 0),
    CHECK (cout IS NULL OR cout >= 0)
    -- FK vers trajet + UNIQUE(id_trajet, ordre) dans 02_alter_fk.sql
);

-- ============================================================
-- TABLE : signature
-- Bloc : Missions & Trajets
-- ============================================================
CREATE TABLE IF NOT EXISTS signature (
    id_signature     INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_ordre_mission INTEGER       NOT NULL,
    etat             VARCHAR(12)   NOT NULL DEFAULT 'EN_ATTENTE',
    fichier_path     VARCHAR(255),
    horodatage       TIMESTAMPTZ,
    id_signataire    INTEGER,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CHECK (UPPER(etat) IN ('EN_ATTENTE','SIGNE','REFUSE'))
    -- UNIQUE(id_ordre_mission) + FKs vers ordre_mission / utilisateur dans 02_alter_fk.sql
);

-- ============================================================
-- TABLE : notification
-- Bloc : Missions & Trajets
-- ============================================================
CREATE TABLE IF NOT EXISTS notification (
    id_notification  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_ordre_mission INTEGER       NOT NULL,
    canal            VARCHAR(10)   NOT NULL,
    destinataire     VARCHAR(150)  NOT NULL,
    message          TEXT          NOT NULL,
    envoye_at        TIMESTAMPTZ,
    provider_msg_id  VARCHAR(100),
    etat             VARCHAR(10)   NOT NULL DEFAULT 'PENDING',
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CHECK (UPPER(canal) IN ('SMS','EMAIL')),
    CHECK (UPPER(etat) IN ('PENDING','SENT','FAILED'))
    -- FK vers ordre_mission + index/UNIQUE provider_msg_id éventuel dans 02_alter_fk.sql
);

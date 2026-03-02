-- ============================================================
-- 02_alter_fk.sql
-- Ajout des contraintes FOREIGN KEY, UNIQUE composées et index
-- Projet : Trajet Formateur (PostgreSQL)
-- Auteur : Sébastien  Cantrelle & Partenaire IA
-- Source : dictionnaryData.ods/ README docs
-- ============================================================

-- ============================================================
-- BLOC 1 : Sécurité & Identité
-- Tables : utilisateur, role, utilisateur_role, refresh_token
-- ============================================================

-- ------------------------------------------------------------
-- 1.1 Unicité logique sur l'email utilisateur (soft delete)
--    - email insensible à la casse
--    - uniquement pour les comptes non "supprimés" (deleted_at IS NULL)
-- ------------------------------------------------------------
CREATE UNIQUE INDEX IF NOT EXISTS uq_utilisateur_email_active
    ON utilisateur (LOWER(email))
    WHERE deleted_at IS NULL;

-- ============================================================
-- TABLE : utilisateur_role
-- FK vers utilisateur & role
-- ============================================================

ALTER TABLE utilisateur_role
    ADD CONSTRAINT fk_utilisateur_role_user
        FOREIGN KEY (id_user)
        REFERENCES utilisateur (id_user)
        ON UPDATE CASCADE
        ON DELETE CASCADE;

ALTER TABLE utilisateur_role
    ADD CONSTRAINT fk_utilisateur_role_role
        FOREIGN KEY (id_role)
        REFERENCES role (id_role)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;

-- ============================================================
-- TABLE : refresh_token
-- FK vers utilisateur
-- ============================================================

ALTER TABLE refresh_token
    ADD CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (id_user)
        REFERENCES utilisateur (id_user)
        ON UPDATE CASCADE
        ON DELETE CASCADE;

-- (optionnel mais propre) index sur la FK pour les recherches par user
CREATE INDEX IF NOT EXISTS idx_refresh_token_id_user
    ON refresh_token (id_user);

-- ============================================================
-- BLOC 2 : Profils & Accessibilité
-- Tables : formateur, ecole, responsable_accessibilite
-- ============================================================

-- ============================================================
-- TABLE : formateur
--  - FK 1–1 vers utilisateur
--  - id_user doit être UNIQUE
-- ============================================================

ALTER TABLE formateur
    ADD CONSTRAINT fk_formateur_user
        FOREIGN KEY (id_user)
        REFERENCES utilisateur (id_user)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;

ALTER TABLE formateur
    ADD CONSTRAINT uq_formateur_id_user
        UNIQUE (id_user);

-- index FK (bonus perf)
CREATE INDEX IF NOT EXISTS idx_formateur_id_user
    ON formateur (id_user);

-- ============================================================
-- TABLE : ecole
--  - FK vers utilisateur (compte "école")
--  - Unicité logique établissement (nom, CP, ville)
-- ============================================================

ALTER TABLE ecole
    ADD CONSTRAINT fk_ecole_user
        FOREIGN KEY (id_user)
        REFERENCES utilisateur (id_user)
        ON UPDATE CASCADE
        ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_ecole_id_user
    ON ecole (id_user);

ALTER TABLE ecole
    ADD CONSTRAINT uq_ecole_nom_cp_ville
        UNIQUE (nom_ecole, code_postal, ville);

-- ============================================================
-- TABLE : responsable_accessibilite
--  - FK vers ecole
-- ============================================================

ALTER TABLE responsable_accessibilite
    ADD CONSTRAINT fk_responsable_accessibilite_ecole
        FOREIGN KEY (id_ecole)
        REFERENCES ecole (id_ecole)
        ON UPDATE CASCADE
        ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_responsable_accessibilite_id_ecole
    ON responsable_accessibilite (id_ecole);

-- ============================================================
-- BLOC 3 : Missions & Trajets
-- Tables : ordre_mission, trajet, correspondance, signature, notification
-- ============================================================

-- ============================================================
-- TABLE : ordre_mission
--  - FK vers formateur
--  - FK vers ecole
--  - FK vers utilisateur (créateur / validateur)
--  - FK vers trajet retenu (optionnelle)
-- ============================================================

ALTER TABLE ordre_mission
    ADD CONSTRAINT fk_ordre_mission_formateur
        FOREIGN KEY (id_formateur)
        REFERENCES formateur (id_formateur)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;

ALTER TABLE ordre_mission
    ADD CONSTRAINT fk_ordre_mission_ecole
        FOREIGN KEY (id_ecole)
        REFERENCES ecole (id_ecole)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;

ALTER TABLE ordre_mission
    ADD CONSTRAINT fk_ordre_mission_user_createur
        FOREIGN KEY (id_user_createur)
        REFERENCES utilisateur (id_user)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;

ALTER TABLE ordre_mission
    ADD CONSTRAINT fk_ordre_mission_user_validateur
        FOREIGN KEY (id_user_validateur)
        REFERENCES utilisateur (id_user)
        ON UPDATE CASCADE
        ON DELETE SET NULL;

-- FK vers trajet retenu (nullable, SET NULL si le trajet est supprimé)
ALTER TABLE ordre_mission
    ADD CONSTRAINT fk_ordre_mission_trajet_retenu
        FOREIGN KEY (id_trajet_retenu)
        REFERENCES trajet (id_trajet)
        ON UPDATE CASCADE
        ON DELETE SET NULL;

-- Index FKs (perf)
CREATE INDEX IF NOT EXISTS idx_ordre_mission_id_formateur
    ON ordre_mission (id_formateur);

CREATE INDEX IF NOT EXISTS idx_ordre_mission_id_ecole
    ON ordre_mission (id_ecole);

CREATE INDEX IF NOT EXISTS idx_ordre_mission_id_user_createur
    ON ordre_mission (id_user_createur);

CREATE INDEX IF NOT EXISTS idx_ordre_mission_id_user_validateur
    ON ordre_mission (id_user_validateur);

CREATE INDEX IF NOT EXISTS idx_ordre_mission_id_trajet_retenu
    ON ordre_mission (id_trajet_retenu);

-- ============================================================
-- TABLE : trajet
--  - FK vers ordre_mission
--  - Partial UNIQUE : au plus un trajet retenu par OM
-- ============================================================

ALTER TABLE trajet
    ADD CONSTRAINT fk_trajet_ordre_mission
        FOREIGN KEY (id_ordre_mission)
        REFERENCES ordre_mission (id_ordre_mission)
        ON UPDATE CASCADE
        ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_trajet_id_ordre_mission
    ON trajet (id_ordre_mission);

-- Un seul trajet "retenu = true" par ordre de mission
CREATE UNIQUE INDEX IF NOT EXISTS uq_trajet_ordre_mission_retenu
    ON trajet (id_ordre_mission)
    WHERE retenu = TRUE;

-- ============================================================
-- TABLE : correspondance
--  - FK vers trajet
--  - Unicité (id_trajet, ordre)
-- ============================================================

ALTER TABLE correspondance
    ADD CONSTRAINT fk_correspondance_trajet
        FOREIGN KEY (id_trajet)
        REFERENCES trajet (id_trajet)
        ON UPDATE CASCADE
        ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_correspondance_id_trajet
    ON correspondance (id_trajet);

ALTER TABLE correspondance
    ADD CONSTRAINT uq_correspondance_trajet_ordre
        UNIQUE (id_trajet, ordre);

-- ============================================================
-- TABLE : signature
--  - FK vers ordre_mission
--  - FK vers utilisateur (signataire) avec ON DELETE SET NULL
--  - UNIQUE(id_ordre_mission) déjà défini dans 01_create_tables.sql
-- ============================================================

ALTER TABLE signature
    ADD CONSTRAINT fk_signature_ordre_mission
        FOREIGN KEY (id_ordre_mission)
        REFERENCES ordre_mission (id_ordre_mission)
        ON UPDATE CASCADE
        ON DELETE CASCADE;

ALTER TABLE signature
    ADD CONSTRAINT fk_signature_signataire
        FOREIGN KEY (id_signataire)
        REFERENCES utilisateur (id_user)
        ON UPDATE CASCADE
        ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_signature_id_ordre_mission
    ON signature (id_ordre_mission);

CREATE INDEX IF NOT EXISTS idx_signature_id_signataire
    ON signature (id_signataire);

-- ============================================================
-- TABLE : notification
--  - FK vers ordre_mission
--  - Index optionnel sur provider_msg_id (unique si renseigné)
-- ============================================================

ALTER TABLE notification
    ADD CONSTRAINT fk_notification_ordre_mission
        FOREIGN KEY (id_ordre_mission)
        REFERENCES ordre_mission (id_ordre_mission)
        ON UPDATE CASCADE
        ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_notification_id_ordre_mission
    ON notification (id_ordre_mission);

-- provider_msg_id "peut être unique si renseigné"
CREATE UNIQUE INDEX IF NOT EXISTS uq_notification_provider_msg_id_not_null
    ON notification (provider_msg_id)
    WHERE provider_msg_id IS NOT NULL;

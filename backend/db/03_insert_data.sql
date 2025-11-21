-- ============================================================
-- 03_insert_data.sql
-- Données de démo cohérentes (non "fake" 😉)
-- Projet : Trajet Formateur (PostgreSQL)
-- Auteur : Sébastien  Cantrelle & Partenaire IA
-- ============================================================

BEGIN;

-- ============================================================
-- 1) ROLES
-- ============================================================
INSERT INTO role (id_role, code, libelle, description)
OVERRIDING SYSTEM VALUE VALUES
    (1, 'ADMIN',        'Administrateur',          'Compte administrateur global'),
    (2, 'GESTIONNAIRE', 'Gestionnaire mobilité',   'Gestion des ordres de mission et des trajets'),
    (3, 'FORMATEUR',    'Formateur',               'Formateur intervenant en école'),
    (4, 'ECOLE',        'Référent établissement',  'Compte rattaché à un établissement scolaire');

-- ============================================================
-- 2) UTILISATEURS
--    - 1 admin
--    - 1 gestionnaire
--    - 2 formateurs
--    - 2 comptes "école"
-- ============================================================

INSERT INTO utilisateur (
    id_user, email, password_hash, prenom, nom,
    telephone, actif, adresse_l1, code_postal, ville, pays_code,
    email_verified, telephone_verified, failed_login_attempts
) OVERRIDING SYSTEM VALUE VALUES
    (1, 'admin@trajet.afci.test',       'HASHED_ADMIN',       'Alice',    'Admin',
     '+33601010101', TRUE, '10 Rue du Siège', '80000', 'Amiens', 'FR',
     TRUE, TRUE, 0),

    (2, 'gestion@trajet.afci.test',     'HASHED_GEST',        'Gérard',   'Gestion',
     '+33602020202', TRUE, '20 Rue des Missions', '80000', 'Amiens', 'FR',
     TRUE, TRUE, 0),

    (3, 'formateur1@trajet.afci.test',  'HASHED_FORM1',       'Bruno',    'Martin',
     '+33603030303', TRUE, '5 Rue des Lilas', '59100', 'Roubaix', 'FR',
     TRUE, FALSE, 0),

    (4, 'formateur2@trajet.afci.test',  'HASHED_FORM2',       'Claire',   'Durand',
     '+33604040404', TRUE, '12 Avenue des Flandres', '62000', 'Arras', 'FR',
     FALSE, FALSE, 0),

    (5, 'contact.lycee1@lycee.test',    'HASHED_ECOLE1',      'Sophie',   'Lemoine',
     '+33322000001', TRUE, '1 Rue du Lycée', '80000', 'Amiens', 'FR',
     TRUE, TRUE, 0),

    (6, 'contact.lycee2@lycee.test',    'HASHED_ECOLE2',      'Marc',     'Petit',
     '+33321000002', TRUE, '15 Rue des Étudiants', '60200', 'Compiègne', 'FR',
     TRUE, FALSE, 0);

-- ============================================================
-- 3) UTILISATEUR_ROLE
-- ============================================================
INSERT INTO utilisateur_role (id_user, id_role)
VALUES
    (1, 1), -- Alice -> ADMIN
    (2, 2), -- Gérard -> GESTIONNAIRE
    (3, 3), -- Bruno  -> FORMATEUR
    (4, 3), -- Claire -> FORMATEUR
    (5, 4), -- Sophie -> ECOLE
    (6, 4); -- Marc   -> ECOLE

-- ============================================================
-- 4) FORMATEURS
--    rattachés à leurs comptes utilisateur
-- ============================================================
INSERT INTO formateur (
    id_formateur, id_user, zone_km, vehicule_perso, permis,
    mobilite_pref_json, disponibilite_json, commentaire
) OVERRIDING SYSTEM VALUE VALUES
    (1, 3, 50, TRUE,  TRUE,
     '{"moyen_principal":"VOITURE","alternatives":["TRAIN","COVOITURAGE"]}',
     '{"lundi":["matin","apres_midi"],"mercredi":["matin"],"vendredi":["journee"]}',
     'Formateur habitué à intervenir dans la métropole lilloise'),

    (2, 4, 120, FALSE, TRUE,
     '{"moyen_principal":"TRAIN","alternatives":["METRO","MARCHE"]}',
     '{"mardi":["journee"],"jeudi":["apres_midi"]}',
     'Préfère les interventions accessibles en transports en commun');

-- ============================================================
-- 5) ECOLES
--    rattachées à leurs comptes utilisateur
-- ============================================================
INSERT INTO ecole (
    id_ecole, id_user, nom_ecole,
    adresse_l1, adresse_l2, code_postal, ville, pays_code,
    lat, lon, niveau_accessibilite, infos_acces
) OVERRIDING SYSTEM VALUE VALUES
    (1, 5, 'Lycée Technique Jean-Monnet',
     '1 Rue du Lycée', NULL, '80000', 'Amiens', 'FR',
     49.894067, 2.295753, 'FACILE',
     'Accès bus (lignes 1 et 3), stationnement réservé PMR devant l''entrée.'),

    (2, 6, 'Collège Les Horizons',
     '15 Rue des Étudiants', NULL, '60200', 'Compiègne', 'FR',
     49.417000, 2.823000, 'DIFFICILE',
     'Établissement en centre-ville, stationnement limité, accès possible par bus et train.');

-- ============================================================
-- 6) RESPONSABLES ACCESSIBILITÉ
-- ============================================================
INSERT INTO responsable_accessibilite (
    id_responsable, id_ecole, nom, prenom, fonction,
    telephone, email, plage_horaire
) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 'Leroy', 'Isabelle', 'Référente handicap',
     '+33322001122', 'isabelle.leroy@lycee-monnet.test',
     'Lun - Jeu : 9h-12h / 14h-16h'),

    (2, 2, 'Dubois', 'Franck', 'Adjoint direction',
     '+33323002233', 'franck.dubois@college-horizons.test',
     'Mar - Ven : 8h30-12h / 13h30-17h');

-- ============================================================
-- 7) ORDRES DE MISSION
--    2 missions : une validée & signée, une en attente
-- ============================================================
INSERT INTO ordre_mission (
    id_ordre_mission, code_ordre,
    id_formateur, id_ecole,
    id_user_createur, id_user_validateur,
    date_debut, date_fin,
    statut,
    cout_total_estime,
    commentaire,
    id_trajet_retenu
) OVERRIDING SYSTEM VALUE VALUES
    (1, 'OM-2025-0001',
     1, 1,
     2, 1,
     DATE '2025-02-10', DATE '2025-02-14',
     'VALIDE',
     320.50,
     'Mission d''une semaine pour animer un module CDA niveau débutant.',
     NULL),

    (2, 'OM-2025-0002',
     2, 2,
     2, NULL,
     DATE '2025-03-05', DATE '2025-03-07',
     'EN_ATTENTE_VALIDATION',
     210.00,
     'Intervention de 3 jours sur la thématique cybersécurité.',
     NULL);

-- ============================================================
-- 8) TRAJETS
--    3 trajets proposés (2 pour OM-1, 1 pour OM-2)
--    On marque un trajet "retenu = TRUE" par ordre de mission
-- ============================================================
INSERT INTO trajet (
    id_trajet, id_ordre_mission,
    moyen_principal, distance_km, duree_min, cout_estime,
    itineraire_json, propose_par, retenu
) OVERRIDING SYSTEM VALUE VALUES
    -- OM-1 : trajet voiture (retenu)
    (1, 1,
     'VOITURE', 45.80, 50, 32.50,
     '{"segments":[{"type":"VOITURE","depart":"Roubaix","arrivee":"Amiens","distance_km":45.8,"duree_min":50}]}',
     'FORMATEUR', TRUE),

    -- OM-1 : trajet train (alternative)
    (2, 1,
     'TRAIN', 48.20, 70, 45.00,
     '{"segments":[{"type":"TRAIN","depart":"Lille Europe","arrivee":"Amiens","duree_min":60},{"type":"MARCHE","depart":"Gare d''Amiens","arrivee":"Lycée Jean-Monnet","duree_min":10}]}',
     'SYSTEME', FALSE),

    -- OM-2 : trajet train (retenu)
    (3, 2,
     'TRAIN', 95.00, 90, 60.00,
     '{"segments":[{"type":"TRAIN","depart":"Arras","arrivee":"Compiègne","duree_min":75},{"type":"BUS","depart":"Gare de Compiègne","arrivee":"Collège Les Horizons","duree_min":15}]}',
     'GESTIONNAIRE', TRUE);

-- Mise à jour des id_trajet_retenu dans ordre_mission
UPDATE ordre_mission
SET id_trajet_retenu = 1
WHERE id_ordre_mission = 1;

UPDATE ordre_mission
SET id_trajet_retenu = 3
WHERE id_ordre_mission = 2;

-- ============================================================
-- 9) CORRESPONDANCES
--    On détaille les segments des trajets 2 et 3
-- ============================================================
INSERT INTO correspondance (
    id_trajet, ordre, type_transport, duree_min, cout, details
) VALUES
    -- Trajet 2 (OM-1, TRAIN)
    (2, 1, 'TRAIN', 60, 35.00, 'Lille Europe -> Amiens (train direct)'),
    (2, 2, 'MARCHE', 10, 0.00,  'Gare d''Amiens -> Lycée Jean-Monnet à pied'),

    -- Trajet 3 (OM-2, TRAIN + BUS)
    (3, 1, 'TRAIN', 75, 50.00, 'Arras -> Compiègne (TER)'),
    (3, 2, 'BUS',   15, 10.00, 'Gare de Compiègne -> Collège Les Horizons');

-- ============================================================
-- 10) SIGNATURES
--     OM-1 signée, OM-2 en attente
-- ============================================================
INSERT INTO signature (
    id_signature, id_ordre_mission, etat,
    fichier_path, horodatage, id_signataire
) OVERRIDING SYSTEM VALUE VALUES
    (1, 1, 'SIGNE',
     '/signatures/OM-2025-0001-signature-admin.pdf',
     TIMESTAMPTZ '2025-01-20 10:15:00+01',
     1),

    (2, 2, 'EN_ATTENTE',
     NULL,
     NULL,
     NULL);

-- ============================================================
-- 11) NOTIFICATIONS
--      - mail + SMS pour OM-1
--      - mail de demande de validation pour OM-2
-- ============================================================
INSERT INTO notification (
    id_notification, id_ordre_mission,
    canal, destinataire, message,
    envoye_at, provider_msg_id, etat
) OVERRIDING SYSTEM VALUE VALUES
    (1, 1,
     'EMAIL', 'formateur1@trajet.afci.test',
     'Votre ordre de mission OM-2025-0001 a été validé et signé.',
     TIMESTAMPTZ '2025-01-20 10:16:00+01',
     'mailjet-om-2025-0001',
     'SENT'),

    (2, 1,
     'SMS', '+33603030303',
     'OM-2025-0001 : trajet retenu (VOITURE, départ prévu 8h15).',
     TIMESTAMPTZ '2025-02-09 18:00:00+01',
     'twilio-sms-om-2025-0001',
     'SENT'),

    (3, 2,
     'EMAIL', 'formateur2@trajet.afci.test',
     'Un nouvel ordre de mission OM-2025-0002 est en attente de validation.',
     TIMESTAMPTZ '2025-02-15 09:00:00+01',
     'mailjet-om-2025-0002',
     'PENDING');

-- ============================================================
-- 12) REFRESH TOKENS (exemple pour ADMIN & FORMATEUR1)
-- ============================================================
INSERT INTO refresh_token (
    id_token, id_user, token,
    issued_at, expires_at, revoked,
    user_agent, ip_address, created_at, updated_at
) VALUES
    ('00000000-0000-0000-0000-000000000001', 1,
     'REFRESH_ADMIN_EXEMPLE',
     TIMESTAMPTZ '2025-01-10 08:00:00+01',
     TIMESTAMPTZ '2025-02-10 08:00:00+01',
     FALSE,
     'Mozilla/5.0 (Windows 10; x64)', '192.168.0.10',
     now(), now()),

    ('00000000-0000-0000-0000-000000000002', 3,
     'REFRESH_FORMATEUR1_EXEMPLE',
     TIMESTAMPTZ '2025-01-12 09:30:00+01',
     TIMESTAMPTZ '2025-02-12 09:30:00+01',
     FALSE,
     'Mozilla/5.0 (Android 14; Mobile)', '192.168.0.20',
     now(), now());

COMMIT;

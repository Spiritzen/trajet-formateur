-- ============================================================
-- 04_test_requetes.sql
-- Requêtes de contrôle & de démonstration
-- Projet : Trajet Formateur (PostgreSQL)
-- Auteur : Sébastien  Cantrelle & Partenaire IA
-- ============================================================

-- ------------------------------------------------------------
-- CONTEXTE :
-- Ce fichier sert à :
--   - vérifier que les données de 03_insert_data.sql
--     respectent bien les contraintes du schéma
--   - fournir des exemples de requêtes métier
--     pour le futur backend (Spring Boot / JPA / React)
-- ------------------------------------------------------------

-- ============================================================
-- 1) Vérification des utilisateurs + rôles
-- ============================================================

-- 1.1 Liste de tous les utilisateurs avec leurs rôles
SELECT
    u.id_user,
    u.email,
    u.prenom,
    u.nom,
    STRING_AGG(r.code, ', ') AS roles
FROM utilisateur u
LEFT JOIN utilisateur_role ur ON ur.id_user = u.id_user
LEFT JOIN role r ON r.id_role = ur.id_role
GROUP BY u.id_user, u.email, u.prenom, u.nom
ORDER BY u.id_user;

-- 1.2 Vérifier que chaque email "actif" est unique (soft delete)
SELECT LOWER(email) AS email_normalise, COUNT(*) AS nb
FROM utilisateur
WHERE deleted_at IS NULL
GROUP BY LOWER(email)
HAVING COUNT(*) > 1;

-- Si cette requête ne renvoie aucune ligne => OK

-- ============================================================
-- 2) Formateurs & zones d'intervention
-- ============================================================

-- 2.1 Liste des formateurs + infos de base + zone d'intervention
SELECT
    f.id_formateur,
    u.prenom,
    u.nom,
    u.email,
    f.zone_km,
    f.vehicule_perso,
    f.permis,
    f.mobilite_pref_json,
    f.disponibilite_json
FROM formateur f
JOIN utilisateur u ON u.id_user = f.id_user
ORDER BY f.id_formateur;

-- 2.2 Formateurs actifs pouvant intervenir à plus de 100 km
SELECT
    f.id_formateur,
    u.prenom,
    u.nom,
    f.zone_km
FROM formateur f
JOIN utilisateur u ON u.id_user = f.id_user
WHERE f.zone_km >= 100
  AND u.actif = TRUE
ORDER BY f.zone_km DESC;

-- ============================================================
-- 3) Écoles & accessibilité
-- ============================================================

-- 3.1 Liste des écoles avec niveau d’accessibilité et coordonnées
SELECT
    e.id_ecole,
    e.nom_ecole,
    e.ville,
    e.code_postal,
    e.niveau_accessibilite,
    e.lat,
    e.lon,
    e.infos_acces,
    u.email AS contact_principal
FROM ecole e
JOIN utilisateur u ON u.id_user = e.id_user
ORDER BY e.nom_ecole;

-- 3.2 Liste des responsables accessibilité par école
SELECT
    e.nom_ecole,
    ra.nom,
    ra.prenom,
    ra.fonction,
    ra.telephone,
    ra.email,
    ra.plage_horaire
FROM responsable_accessibilite ra
JOIN ecole e ON e.id_ecole = ra.id_ecole
ORDER BY e.nom_ecole, ra.nom, ra.prenom;

-- ============================================================
-- 4) Ordres de mission : contrôles & vue d’ensemble
-- ============================================================

-- 4.1 Liste des ordres de mission avec formateur + école + statut
SELECT
    om.id_ordre_mission,
    om.code_ordre,
    om.date_debut,
    om.date_fin,
    om.statut,
    om.cout_total_estime,
    f.id_formateur,
    uf.prenom AS formateur_prenom,
    uf.nom    AS formateur_nom,
    e.nom_ecole,
    ue.ville      AS ville_ecole,
    uc.email      AS createur_email,
    uv.email      AS validateur_email
FROM ordre_mission om
JOIN formateur f      ON f.id_formateur = om.id_formateur
JOIN utilisateur uf   ON uf.id_user = f.id_user
JOIN ecole e          ON e.id_ecole = om.id_ecole
JOIN utilisateur uc   ON uc.id_user = om.id_user_createur
LEFT JOIN utilisateur uv ON uv.id_user = om.id_user_validateur
LEFT JOIN utilisateur ue ON ue.id_user = e.id_user
ORDER BY om.date_debut, om.code_ordre;

-- 4.2 Ordres de mission en attente de validation
SELECT
    om.id_ordre_mission,
    om.code_ordre,
    om.date_debut,
    om.date_fin,
    om.statut,
    uf.prenom AS formateur_prenom,
    uf.nom    AS formateur_nom,
    e.nom_ecole
FROM ordre_mission om
JOIN formateur f    ON f.id_formateur = om.id_formateur
JOIN utilisateur uf ON uf.id_user = f.id_user
JOIN ecole e        ON e.id_ecole = om.id_ecole
WHERE om.statut IN ('EN_ATTENTE_VALIDATION', 'BROUILLON', 'PROPOSE')
ORDER BY om.date_debut;

-- 4.3 Vérifier qu'il n'y a pas d'ordres de mission incohérents
--     (date_debut > date_fin)
SELECT *
FROM ordre_mission
WHERE date_debut > date_fin;

-- Si cette requête ne renvoie aucune ligne => OK

-- ============================================================
-- 5) Trajets & correspondances
-- ============================================================

-- 5.1 Tous les trajets avec leur ordre de mission & indication retenu
SELECT
    t.id_trajet,
    t.id_ordre_mission,
    om.code_ordre,
    t.moyen_principal,
    t.distance_km,
    t.duree_min,
    t.cout_estime,
    t.retenu,
    t.propose_par
FROM trajet t
JOIN ordre_mission om ON om.id_ordre_mission = t.id_ordre_mission
ORDER BY om.code_ordre, t.id_trajet;

-- 5.2 Vérifier la règle métier :
--     un seul trajet retenu par ordre de mission
SELECT
    id_ordre_mission,
    COUNT(*) AS nb_trajets_retenus
FROM trajet
WHERE retenu = TRUE
GROUP BY id_ordre_mission
HAVING COUNT(*) > 1;

-- Si cette requête ne renvoie aucune ligne => la contrainte métier est respectée

-- 5.3 Détail d’un ordre de mission avec le trajet retenu + ses correspondances
--     (exemple : OM-2025-0001)
SELECT
    om.code_ordre,
    om.date_debut,
    om.date_fin,
    uf.prenom AS formateur_prenom,
    uf.nom    AS formateur_nom,
    e.nom_ecole,
    t.id_trajet,
    t.moyen_principal,
    t.distance_km,
    t.duree_min,
    t.cout_estime,
    c.ordre            AS corresp_ordre,
    c.type_transport   AS corresp_type_transport,
    c.duree_min        AS corresp_duree_min,
    c.cout             AS corresp_cout,
    c.details          AS corresp_details
FROM ordre_mission om
JOIN formateur f      ON f.id_formateur = om.id_formateur
JOIN utilisateur uf   ON uf.id_user = f.id_user
JOIN ecole e          ON e.id_ecole = om.id_ecole
LEFT JOIN trajet t    ON t.id_trajet = om.id_trajet_retenu
LEFT JOIN correspondance c ON c.id_trajet = t.id_trajet
WHERE om.code_ordre = 'OM-2025-0001'
ORDER BY c.ordre;

-- 5.4 Toutes les correspondances pour un trajet donné (ex : id_trajet = 3)
SELECT
    t.id_trajet,
    om.code_ordre,
    c.ordre,
    c.type_transport,
    c.duree_min,
    c.cout,
    c.details
FROM correspondance c
JOIN trajet t       ON t.id_trajet = c.id_trajet
JOIN ordre_mission om ON om.id_ordre_mission = t.id_ordre_mission
WHERE t.id_trajet = 3
ORDER BY c.ordre;

-- 5.5 Vérifier l’unicité (id_trajet, ordre) sur correspondance
SELECT
    id_trajet,
    ordre,
    COUNT(*) AS nb
FROM correspondance
GROUP BY id_trajet, ordre
HAVING COUNT(*) > 1;

-- Si aucune ligne => contrainte respectée

-- ============================================================
-- 6) Signatures & suivi de validation
-- ============================================================

-- 6.1 Liste des ordres de mission avec état de signature (JOIN à gauche)
SELECT
    om.id_ordre_mission,
    om.code_ordre,
    om.statut,
    s.etat           AS signature_etat,
    s.horodatage     AS signature_date,
    us.prenom        AS signataire_prenom,
    us.nom           AS signataire_nom
FROM ordre_mission om
LEFT JOIN signature s  ON s.id_ordre_mission = om.id_ordre_mission
LEFT JOIN utilisateur us ON us.id_user = s.id_signataire
ORDER BY om.code_ordre;

-- 6.2 Ordres de mission validés mais pas encore signés
SELECT
    om.id_ordre_mission,
    om.code_ordre,
    om.statut,
    s.etat AS signature_etat
FROM ordre_mission om
LEFT JOIN signature s ON s.id_ordre_mission = om.id_ordre_mission
WHERE om.statut = 'VALIDE'
  AND (s.id_signature IS NULL OR s.etat <> 'SIGNE');

-- ============================================================
-- 7) Notifications (SMS / EMAIL)
-- ============================================================

-- 7.1 Historique des notifications par ordre de mission
SELECT
    om.code_ordre,
    n.id_notification,
    n.canal,
    n.destinataire,
    n.message,
    n.envoye_at,
    n.etat,
    n.provider_msg_id
FROM notification n
JOIN ordre_mission om ON om.id_ordre_mission = n.id_ordre_mission
ORDER BY om.code_ordre, n.envoye_at;

-- 7.2 Notifications en erreur / non envoyées (etat != 'SENT')
SELECT
    n.id_notification,
    om.code_ordre,
    n.canal,
    n.destinataire,
    n.etat,
    n.envoye_at,
    n.provider_msg_id
FROM notification n
JOIN ordre_mission om ON om.id_ordre_mission = n.id_ordre_mission
WHERE n.etat <> 'SENT'
ORDER BY n.envoye_at;

-- ============================================================
-- 8) KPI simples (indicateurs pour un futur dashboard)
-- ============================================================

-- 8.1 Nombre d’ordres de mission par statut
SELECT
    statut,
    COUNT(*) AS nb
FROM ordre_mission
GROUP BY statut
ORDER BY statut;

-- 8.2 Coût total estimé des missions par formateur
SELECT
    f.id_formateur,
    u.prenom,
    u.nom,
    SUM(COALESCE(om.cout_total_estime, 0)) AS total_cout_estime
FROM ordre_mission om
JOIN formateur f  ON f.id_formateur = om.id_formateur
JOIN utilisateur u ON u.id_user = f.id_user
GROUP BY f.id_formateur, u.prenom, u.nom
ORDER BY total_cout_estime DESC;

-- 8.3 Distance et temps total des trajets retenus par formateur
SELECT
    f.id_formateur,
    u.prenom,
    u.nom,
    SUM(COALESCE(t.distance_km, 0)) AS distance_totale_km,
    SUM(COALESCE(t.duree_min, 0))   AS duree_totale_min
FROM ordre_mission om
JOIN formateur f  ON f.id_formateur = om.id_formateur
JOIN utilisateur u ON u.id_user = f.id_user
LEFT JOIN trajet t ON t.id_trajet = om.id_trajet_retenu
GROUP BY f.id_formateur, u.prenom, u.nom
ORDER BY distance_totale_km DESC;

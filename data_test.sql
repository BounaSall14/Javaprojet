-- ============================================================
-- SGPA Pharmacie — Script de données de test réalistes
-- Date de génération : 2026-02-19
-- IMPORTANT : Exécuter APRÈS que schema.sql ait été appliqué.
-- Ne contient que des INSERT. Pas de TRUNCATE ni DELETE.
-- ============================================================


-- ============================================================
-- SECTION 1 : UTILISATEURS (6 vendeurs)
-- password_hash identique à l'admin existant : 'admin123'
-- ============================================================

INSERT INTO users (username, password_hash, role) VALUES
    ('marie.dupont',   'admin123', 'VENDEUR'),
    ('pierre.martin',  'admin123', 'VENDEUR'),
    ('sophie.bernard', 'admin123', 'VENDEUR'),
    ('lucas.moreau',   'admin123', 'VENDEUR'),
    ('emma.leroy',     'admin123', 'VENDEUR'),
    ('thomas.petit',   'admin123', 'VENDEUR')
ON CONFLICT (username) DO NOTHING;


-- ============================================================
-- SECTION 2 : FOURNISSEURS (6 laboratoires / grossistes)
-- ============================================================

INSERT INTO fournisseurs (nom, contact, adresse) VALUES
    ('Sanofi France',              '01 53 77 40 00', '82 avenue Raspail, 94250 Gentilly'),
    ('Pierre Fabre Médicament',    '05 63 08 80 00', '45 place Abel Gance, 92100 Boulogne-Billancourt'),
    ('Laboratoires Servier',       '01 55 72 60 00', '50 rue Carnot, 92284 Suresnes'),
    ('Alliance Healthcare',        '01 71 05 90 00', '2 rue Berthelot, 94110 Arcueil'),
    ('CERP Bretagne Nord',         '02 99 21 84 00', 'ZI des Touches, 35470 Bain-de-Bretagne'),
    ('OCP Répartition',            '01 47 16 50 00', '119 rue Anatole France, 92300 Levallois-Perret');


-- ============================================================
-- SECTION 3 : MÉDICAMENTS (25 références réalistes)
-- Colonnes : nom_commercial, principe_actif, forme_galenique,
--            dosage, prix_public, necessite_ordonnance,
--            date_peremption, stock, seuil_min
--
-- [1-5]  : DÉJÀ EXPIRÉS (< aujourd'hui 2026-02-19)
-- [6-10] : EXPIRATION PROCHE (< 3 mois, avant 2026-05-19)
-- [11-25]: PÉREMPTION CORRECTE (6-24 mois)
-- ============================================================

-- --- Médicaments déjà expirés (5) ---
INSERT INTO medicaments (nom_commercial, principe_actif, forme_galenique, dosage, prix_public, necessite_ordonnance, date_peremption, stock, seuil_min) VALUES
    ('Nurofen 400mg Caps',  'Ibuprofène',                  'Gélule liquide',       '400mg',          6.30,  FALSE, '2025-12-31', 5,  5),
    ('Euphytose',           'Passiflore / Valériane',       'Comprimé enrobé',      '45mg/235mg',     13.50, FALSE, '2025-10-01', 3,  5),
    ('Dafalgan 1g',         'Paracétamol',                  'Comprimé effervescent','1g',             4.10,  FALSE, '2025-11-30', 0,  10),
    ('Pivalone crème',      'Tixocortol pivalate',          'Crème',                '10mg/g',         11.80, TRUE,  '2025-09-15', 2,  3),
    ('Transipégase',        'Argile / Gélatine alimentaire','Capsule',              '650mg',          8.70,  FALSE, '2026-01-15', 7,  5);

-- --- Médicaments expirant dans moins de 3 mois (5) ---
INSERT INTO medicaments (nom_commercial, principe_actif, forme_galenique, dosage, prix_public, necessite_ordonnance, date_peremption, stock, seuil_min) VALUES
    ('Biafine émulsion',    'Trolaminum',                   'Émulsion cutanée',     '400mg/g',        9.80,  FALSE, '2026-04-01', 8,  5),
    ('Maalox suspension',   'Algeldrate / Hydroxyde Mg',    'Suspension orale',     '460mg/400mg/5ml',7.20,  FALSE, '2026-03-15', 12, 5),
    ('Gaviscon menthe',     'Alginate de sodium',           'Suspension buvable',   '500mg/10ml',     8.60,  FALSE, '2026-05-10', 6,  5),
    ('Efferalgan Vit C',    'Paracétamol / Vitamine C',     'Comprimé effervescent','500mg/200mg',    5.40,  FALSE, '2026-03-01', 14, 8),
    ('Oscillococcinum',     'Anas Barbariae Extractum',     'Granules',             'Dose 1g',        11.20, FALSE, '2026-04-15', 20, 5);

-- --- Médicaments avec péremption correcte (15) ---
INSERT INTO medicaments (nom_commercial, principe_actif, forme_galenique, dosage, prix_public, necessite_ordonnance, date_peremption, stock, seuil_min) VALUES
    ('Doliprane 1000mg',    'Paracétamol',                  'Comprimé',             '1000mg',         3.50,  FALSE, '2027-06-01', 150, 20),
    ('Doliprane sirop',     'Paracétamol',                  'Sirop',                '30mg/ml',        5.80,  FALSE, '2027-03-01', 80,  15),
    ('Ibuprofène 400mg',    'Ibuprofène',                   'Comprimé',             '400mg',          4.20,  FALSE, '2027-09-01', 60,  10),
    ('Amoxicilline 500mg',  'Amoxicilline',                 'Gélule',               '500mg',          8.90,  TRUE,  '2026-09-01', 40,  10),
    ('Augmentin 1g',        'Amoxicilline/Ac. clavulanique','Comprimé pelliculé',   '1g/125mg',       15.30, TRUE,  '2027-01-01', 20,  5),
    ('Spasfon',             'Phloroglucinol',               'Comprimé enrobé',      '80mg',           5.20,  FALSE, '2027-04-01', 45,  10),
    ('Smecta',              'Diosmectite',                  'Poudre buvable',       '3g/sachet',      7.80,  FALSE, '2027-02-01', 35,  8),
    ('Toplexil sirop',      'Oxomémazine',                  'Sirop',                '1mg/ml',         9.40,  FALSE, '2026-11-01', 20,  5),
    ('Strepsils miel-citron','Amylmétacrésol',              'Pastille',             '0.6mg',          5.90,  FALSE, '2027-08-01', 50,  10),
    ('Voltarène 50mg',      'Diclofénac sodique',           'Comprimé gastrorésistant','50mg',        7.60,  TRUE,  '2027-05-01', 30,  8),
    ('Ventoline 100µg',     'Salbutamol',                   'Aérosol pour inhalation','100µg/dose',   35.20, TRUE,  '2026-11-01', 10,  3),
    ('Cétirizine 10mg',     'Cétirizine dichlorhydrate',    'Comprimé pelliculé',   '10mg',           4.80,  FALSE, '2027-10-01', 55,  10),
    ('Loratadine 10mg',     'Loratadine',                   'Comprimé',             '10mg',           4.50,  FALSE, '2027-11-01', 40,  10),
    ('Rhinofluimucil',      'Tuaminoheptane / Acétylcystéine','Solution nasale',    '0.5%/1%',        8.20,  FALSE, '2026-08-01', 15,  5),
    ('Cortisone Mylan 5mg', 'Cortisone acétate',            'Comprimé',             '5mg',            18.40, TRUE,  '2027-03-01', 15,  5);

-- Correspondance des IDs SERIAL (ordre d'insertion) :
-- 1=Nurofen Caps, 2=Euphytose, 3=Dafalgan 1g, 4=Pivalone, 5=Transipégase
-- 6=Biafine, 7=Maalox, 8=Gaviscon, 9=Efferalgan VitC, 10=Oscillococcinum
-- 11=Doliprane 1000mg, 12=Doliprane sirop, 13=Ibuprofène 400mg
-- 14=Amoxicilline 500mg, 15=Augmentin 1g, 16=Spasfon, 17=Smecta
-- 18=Toplexil, 19=Strepsils, 20=Voltarène, 21=Ventoline
-- 22=Cétirizine, 23=Loratadine, 24=Rhinofluimucil, 25=Cortisone


-- ============================================================
-- SECTION 4 : COMMANDES (12 commandes sur 3 mois)
-- 6 RECUE, 4 EN_ATTENTE, 2 ANNULEE — 2 à 6 lignes chacune
-- ============================================================

-- Commandes RECUE (6)
INSERT INTO commandes (fournisseur_id, date_creation, statut) VALUES
    (1, '2025-11-20 08:30:00', 'RECUE'),   -- id=1
    (2, '2025-11-25 09:00:00', 'RECUE'),   -- id=2
    (3, '2025-12-01 10:15:00', 'RECUE'),   -- id=3
    (4, '2025-12-10 08:00:00', 'RECUE'),   -- id=4
    (5, '2025-12-20 14:30:00', 'RECUE'),   -- id=5
    (6, '2026-01-05 09:45:00', 'RECUE');   -- id=6

-- Commandes EN_ATTENTE (4)
INSERT INTO commandes (fournisseur_id, date_creation, statut) VALUES
    (1, '2026-01-15 10:00:00', 'EN_ATTENTE'),  -- id=7
    (2, '2026-01-22 11:30:00', 'EN_ATTENTE'),  -- id=8
    (3, '2026-02-01 09:00:00', 'EN_ATTENTE'),  -- id=9
    (4, '2026-02-10 14:00:00', 'EN_ATTENTE');  -- id=10

-- Commandes ANNULEE (2)
INSERT INTO commandes (fournisseur_id, date_creation, statut) VALUES
    (5, '2025-12-05 08:00:00', 'ANNULEE'),  -- id=11
    (6, '2026-01-08 10:30:00', 'ANNULEE'); -- id=12

-- Lignes de commandes
INSERT INTO commande_lignes (commande_id, medicament_id, quantite) VALUES
    -- Commande 1 (RECUE, Sanofi)
    (1, 11, 200), (1, 13, 100),
    -- Commande 2 (RECUE, Pierre Fabre)
    (2, 16, 100), (2, 17, 80), (2, 19, 150),
    -- Commande 3 (RECUE, Servier)
    (3, 14, 50),  (3, 15, 30),
    -- Commande 4 (RECUE, Alliance Healthcare)
    (4, 22, 100), (4, 23, 80), (4, 24, 60), (4, 12, 100),
    -- Commande 5 (RECUE, CERP)
    (5, 18, 50),  (5, 20, 40), (5, 21, 20),
    -- Commande 6 (RECUE, OCP)
    (6, 11, 100), (6, 13, 80), (6, 22, 60),
    -- Commande 7 (EN_ATTENTE, Sanofi)
    (7, 14, 60),  (7, 15, 40), (7, 25, 30),
    -- Commande 8 (EN_ATTENTE, Pierre Fabre)
    (8, 11, 150), (8, 12, 100),
    -- Commande 9 (EN_ATTENTE, Servier)
    (9, 16, 80),  (9, 17, 60), (9, 19, 100), (9, 24, 40),
    -- Commande 10 (EN_ATTENTE, Alliance)
    (10, 21, 15), (10, 20, 50), (10, 18, 60),
    -- Commande 11 (ANNULEE, CERP)
    (11, 11, 200),(11, 13, 100),
    -- Commande 12 (ANNULEE, OCP)
    (12, 14, 100),(12, 15, 50), (12, 25, 20);


-- ============================================================
-- SECTION 5 : VENTES (40 ventes sur 60 jours)
-- Heures réalistes 08h–19h, 30% sur ordonnance (~12 ventes)
-- montant_total = somme exacte des (quantite × prix_unitaire)
-- ============================================================

-- Légende des prix utilisés (correspondant au prix_public) :
-- med11=3.50, med12=5.80, med13=4.20, med14=8.90, med15=15.30
-- med16=5.20, med17=7.80, med18=9.40, med19=5.90, med20=7.60
-- med21=35.20, med22=4.80, med23=4.50, med24=8.20, med25=18.40

INSERT INTO ventes (date_heure, sur_ordonnance, montant_total) VALUES
    ('2025-12-21 09:15:00', FALSE, 12.20),  -- v1 : med11×2(7.00) + med16×1(5.20)
    ('2025-12-22 10:30:00', FALSE, 16.00),  -- v2 : med13×1(4.20) + med19×2(11.80)
    ('2025-12-23 14:00:00', FALSE, 10.50),  -- v3 : med11×3(10.50)
    ('2025-12-24 11:20:00', TRUE,  12.60),  -- v4 : med17×1(7.80) + med22×1(4.80)     [ordo]
    ('2025-12-26 09:45:00', FALSE, 14.00),  -- v5 : med12×1(5.80) + med24×1(8.20)
    ('2025-12-27 15:30:00', TRUE,  24.20),  -- v6 : med14×1(8.90) + med15×1(15.30)    [ordo]
    ('2025-12-29 10:00:00', FALSE, 20.70),  -- v7 : med11×2(7.00) + med17×1(7.80) + med19×1(5.90)
    ('2025-12-30 16:15:00', FALSE, 13.80),  -- v8 : med22×2(9.60) + med13×1(4.20)
    ('2026-01-02 09:30:00', TRUE,  35.20),  -- v9 : med21×1(35.20)                    [ordo]
    ('2026-01-03 11:45:00', FALSE, 13.90),  -- v10: med16×2(10.40) + med11×1(3.50)
    ('2026-01-05 14:15:00', FALSE, 15.30),  -- v11: med18×1(9.40) + med19×1(5.90)
    ('2026-01-06 10:00:00', FALSE, 11.50),  -- v12: med23×1(4.50) + med11×2(7.00)
    ('2026-01-08 09:15:00', TRUE,  36.20),  -- v13: med14×2(17.80) + med25×1(18.40)   [ordo]
    ('2026-01-09 15:45:00', FALSE, 20.40),  -- v14: med17×2(15.60) + med22×1(4.80)
    ('2026-01-12 11:00:00', FALSE, 13.60),  -- v15: med13×2(8.40) + med16×1(5.20)
    ('2026-01-13 14:30:00', FALSE, 14.00),  -- v16: med11×4(14.00)
    ('2026-01-15 09:00:00', TRUE,  30.50),  -- v17: med20×2(15.20) + med15×1(15.30)   [ordo]
    ('2026-01-16 10:30:00', TRUE,  25.90),  -- v18: med19×3(17.70) + med24×1(8.20)    [ordo]
    ('2026-01-19 13:15:00', FALSE, 21.00),  -- v19: med12×2(11.60) + med18×1(9.40)
    ('2026-01-20 15:00:00', FALSE, 16.60),  -- v20: med11×2(7.00) + med22×2(9.60)
    ('2026-01-22 09:30:00', TRUE,  44.10),  -- v21: med14×1(8.90) + med21×1(35.20)    [ordo]
    ('2026-01-23 11:45:00', FALSE, 22.20),  -- v22: med16×2(10.40) + med19×2(11.80)
    ('2026-01-26 14:00:00', FALSE, 19.50),  -- v23: med23×2(9.00) + med11×3(10.50)
    ('2026-01-27 10:15:00', FALSE, 12.00),  -- v24: med17×1(7.80) + med13×1(4.20)
    ('2026-01-29 09:00:00', TRUE,  36.80),  -- v25: med25×2(36.80)                    [ordo]
    ('2026-01-30 16:30:00', FALSE, 20.00),  -- v26: med11×2(7.00) + med24×1(8.20) + med22×1(4.80)
    ('2026-02-02 10:45:00', FALSE, 11.10),  -- v27: med19×1(5.90) + med16×1(5.20)
    ('2026-02-03 14:30:00', TRUE,  38.20),  -- v28: med15×2(30.60) + med20×1(7.60)    [ordo]
    ('2026-02-05 09:15:00', FALSE, 23.30),  -- v29: med11×5(17.50) + med12×1(5.80)
    ('2026-02-06 11:00:00', FALSE, 20.40),  -- v30: med13×3(12.60) + med17×1(7.80)
    ('2026-02-09 15:45:00', FALSE, 18.90),  -- v31: med22×3(14.40) + med23×1(4.50)
    ('2026-02-10 10:00:00', TRUE,  44.10),  -- v32: med21×1(35.20) + med14×1(8.90)    [ordo]
    ('2026-02-11 09:30:00', FALSE, 16.40),  -- v33: med11×2(7.00) + med18×1(9.40)
    ('2026-02-12 13:00:00', FALSE, 17.00),  -- v34: med19×2(11.80) + med16×1(5.20)
    ('2026-02-13 14:30:00', TRUE,  33.60),  -- v35: med25×1(18.40) + med20×2(15.20)   [ordo]
    ('2026-02-14 10:30:00', FALSE, 18.70),  -- v36: med11×3(10.50) + med24×1(8.20)
    ('2026-02-16 09:15:00', FALSE, 18.00),  -- v37: med13×2(8.40) + med22×2(9.60)
    ('2026-02-17 11:45:00', TRUE,  42.00),  -- v38: med14×3(26.70) + med15×1(15.30)   [ordo]
    ('2026-02-18 14:00:00', FALSE, 21.40),  -- v39: med12×1(5.80) + med17×2(15.60)
    ('2026-02-19 10:30:00', FALSE, 24.40);  -- v40: med11×4(14.00) + med16×2(10.40)

-- Lignes de ventes (vente_id, medicament_id, quantite, prix_unitaire)
INSERT INTO vente_lignes (vente_id, medicament_id, quantite, prix_unitaire) VALUES
    (1,  11, 2, 3.50),  (1,  16, 1, 5.20),
    (2,  13, 1, 4.20),  (2,  19, 2, 5.90),
    (3,  11, 3, 3.50),
    (4,  17, 1, 7.80),  (4,  22, 1, 4.80),
    (5,  12, 1, 5.80),  (5,  24, 1, 8.20),
    (6,  14, 1, 8.90),  (6,  15, 1, 15.30),
    (7,  11, 2, 3.50),  (7,  17, 1, 7.80),  (7,  19, 1, 5.90),
    (8,  22, 2, 4.80),  (8,  13, 1, 4.20),
    (9,  21, 1, 35.20),
    (10, 16, 2, 5.20),  (10, 11, 1, 3.50),
    (11, 18, 1, 9.40),  (11, 19, 1, 5.90),
    (12, 23, 1, 4.50),  (12, 11, 2, 3.50),
    (13, 14, 2, 8.90),  (13, 25, 1, 18.40),
    (14, 17, 2, 7.80),  (14, 22, 1, 4.80),
    (15, 13, 2, 4.20),  (15, 16, 1, 5.20),
    (16, 11, 4, 3.50),
    (17, 20, 2, 7.60),  (17, 15, 1, 15.30),
    (18, 19, 3, 5.90),  (18, 24, 1, 8.20),
    (19, 12, 2, 5.80),  (19, 18, 1, 9.40),
    (20, 11, 2, 3.50),  (20, 22, 2, 4.80),
    (21, 14, 1, 8.90),  (21, 21, 1, 35.20),
    (22, 16, 2, 5.20),  (22, 19, 2, 5.90),
    (23, 23, 2, 4.50),  (23, 11, 3, 3.50),
    (24, 17, 1, 7.80),  (24, 13, 1, 4.20),
    (25, 25, 2, 18.40),
    (26, 11, 2, 3.50),  (26, 24, 1, 8.20),  (26, 22, 1, 4.80),
    (27, 19, 1, 5.90),  (27, 16, 1, 5.20),
    (28, 15, 2, 15.30), (28, 20, 1, 7.60),
    (29, 11, 5, 3.50),  (29, 12, 1, 5.80),
    (30, 13, 3, 4.20),  (30, 17, 1, 7.80),
    (31, 22, 3, 4.80),  (31, 23, 1, 4.50),
    (32, 21, 1, 35.20), (32, 14, 1, 8.90),
    (33, 11, 2, 3.50),  (33, 18, 1, 9.40),
    (34, 19, 2, 5.90),  (34, 16, 1, 5.20),
    (35, 25, 1, 18.40), (35, 20, 2, 7.60),
    (36, 11, 3, 3.50),  (36, 24, 1, 8.20),
    (37, 13, 2, 4.20),  (37, 22, 2, 4.80),
    (38, 14, 3, 8.90),  (38, 15, 1, 15.30),
    (39, 12, 1, 5.80),  (39, 17, 2, 7.80),
    (40, 11, 4, 3.50),  (40, 16, 2, 5.20);


-- ============================================================
-- SECTION 6 : VÉRIFICATIONS
-- ============================================================

SELECT '=== COMPTES ===' AS info;
SELECT 'users'          AS table_name, COUNT(*) AS nb FROM users;
SELECT 'medicaments'    AS table_name, COUNT(*) AS nb FROM medicaments;
SELECT 'fournisseurs'   AS table_name, COUNT(*) AS nb FROM fournisseurs;
SELECT 'commandes'      AS table_name, COUNT(*) AS nb FROM commandes;
SELECT 'commande_lignes'AS table_name, COUNT(*) AS nb FROM commande_lignes;
SELECT 'ventes'         AS table_name, COUNT(*) AS nb FROM ventes;
SELECT 'vente_lignes'   AS table_name, COUNT(*) AS nb FROM vente_lignes;

SELECT '=== COMMANDES PAR STATUT ===' AS info;
SELECT statut, COUNT(*) FROM commandes GROUP BY statut ORDER BY statut;

SELECT '=== VENTES SUR/SANS ORDONNANCE ===' AS info;
SELECT sur_ordonnance, COUNT(*) FROM ventes GROUP BY sur_ordonnance;

SELECT '=== COHÉRENCE TOTAUX VENTES ===' AS info;
SELECT v.id,
       v.montant_total                                      AS total_vente,
       ROUND(SUM(vl.quantite * vl.prix_unitaire)::NUMERIC, 2) AS total_calcule,
       CASE WHEN v.montant_total = ROUND(SUM(vl.quantite * vl.prix_unitaire)::NUMERIC, 2)
            THEN 'OK' ELSE '*** ERREUR ***' END             AS statut
FROM ventes v
JOIN vente_lignes vl ON vl.vente_id = v.id
GROUP BY v.id, v.montant_total
ORDER BY v.id;

SELECT '=== MÉDICAMENTS EXPIRÉS ===' AS info;
SELECT nom_commercial, date_peremption, stock
FROM medicaments
WHERE date_peremption < CURRENT_DATE
ORDER BY date_peremption;

SELECT '=== MÉDICAMENTS EXPIRANT DANS 3 MOIS ===' AS info;
SELECT nom_commercial, date_peremption, stock
FROM medicaments
WHERE date_peremption BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '3 months'
ORDER BY date_peremption;

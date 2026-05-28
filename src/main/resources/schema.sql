-- ═══════════════════════════════════════════════════════════
--  Smart Energy Manager — Schéma de base de données SQLite
-- ═══════════════════════════════════════════════════════════

-- Héritage par table unique (Single Table Inheritance) :
-- tous les sous-types de Batiment partagent une seule table.
-- Les colonnes spécifiques à chaque sous-type sont nullables.

CREATE TABLE IF NOT EXISTS batiments (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    type            TEXT    NOT NULL,   -- 'Maison' | 'Appartement' | 'Bureau' | 'BatimentUniversitaire'
    nom             TEXT    NOT NULL,
    adresse         TEXT    NOT NULL,
    surface         REAL    NOT NULL,
    -- Maison
    nb_pieces       INTEGER,
    -- Appartement
    etage           INTEGER,
    num_appartement TEXT,
    -- Bureau
    entreprise      TEXT,
    nb_postes       INTEGER,
    -- BatimentUniversitaire
    campus          TEXT
);

CREATE TABLE IF NOT EXISTS releves (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    batiment_id     INTEGER NOT NULL,
    date_heure      TEXT    NOT NULL,   -- format ISO-8601 : 2025-03-15T14:30:00
    type_energie    TEXT    NOT NULL,   -- nom de l'enum TypeEnergie
    quantite        REAL    NOT NULL,
    cout_estime     REAL    NOT NULL,
    FOREIGN KEY (batiment_id) REFERENCES batiments(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS alertes (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    batiment_id     INTEGER,            -- nullable : alerte globale possible
    message         TEXT    NOT NULL,
    date            TEXT    NOT NULL,
    severite        TEXT    NOT NULL,
    FOREIGN KEY (batiment_id) REFERENCES batiments(id) ON DELETE SET NULL
);

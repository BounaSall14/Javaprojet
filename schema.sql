-- Database schema for SGPA Pharmacie

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('ADMIN', 'VENDEUR')) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE medicaments (
    id SERIAL PRIMARY KEY,
    nom_commercial VARCHAR(255) NOT NULL,
    principe_actif VARCHAR(255),
    forme_galenique VARCHAR(100),
    dosage VARCHAR(100),
    prix_public DECIMAL(10, 2) NOT NULL,
    necessite_ordonnance BOOLEAN DEFAULT FALSE,
    date_peremption DATE,
    stock INTEGER DEFAULT 0,
    seuil_min INTEGER DEFAULT 5
);

CREATE TABLE fournisseurs (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    contact VARCHAR(255),
    adresse TEXT
);

CREATE TABLE ventes (
    id SERIAL PRIMARY KEY,
    date_heure TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sur_ordonnance BOOLEAN DEFAULT FALSE,
    montant_total DECIMAL(10, 2) NOT NULL
);

CREATE TABLE vente_lignes (
    vente_id INTEGER REFERENCES ventes(id) ON DELETE CASCADE,
    medicament_id INTEGER REFERENCES medicaments(id),
    quantite INTEGER NOT NULL,
    prix_unitaire DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (vente_id, medicament_id)
);

CREATE TABLE commandes (
    id SERIAL PRIMARY KEY,
    fournisseur_id INTEGER REFERENCES fournisseurs(id),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(20) CHECK (statut IN ('EN_ATTENTE', 'RECUE', 'ANNULEE')) DEFAULT 'EN_ATTENTE'
);

CREATE TABLE commande_lignes (
    commande_id INTEGER REFERENCES commandes(id) ON DELETE CASCADE,
    medicament_id INTEGER REFERENCES medicaments(id),
    quantite INTEGER NOT NULL,
    PRIMARY KEY (commande_id, medicament_id)
);

-- Insert a default admin user (password: admin123)
-- BCrypt hash for 'admin123' is $2a$10$Anq65xXrCdhvgtgJdm04KOrOaAU33XKzcGCDph56/xXo8GZVu.gom
INSERT INTO users (username, password_hash, role) VALUES ('admin', '$2a$10$Anq65xXrCdhvgtgJdm04KOrOaAU33XKzcGCDph56/xXo8GZVu.gom', 'ADMIN');

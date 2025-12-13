-- ============================================
-- BASE DE DONNÉES : BrasilBurgerDB (NeonDB PostgreSQL)
-- Adapté pour Brasil Burger Sénégal 🇸🇳 - VERSION EXAMEN
-- ============================================

-- ============================================
-- TABLE Zone
-- ============================================
CREATE TABLE Zone (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLE Quartier
-- ============================================
CREATE TABLE Quartier (
    id SERIAL PRIMARY KEY,
    zone_id INTEGER NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prix_livraison_base DECIMAL(10,2) NOT NULL DEFAULT 0,
    temps_estime_minutes INTEGER NOT NULL DEFAULT 30,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (zone_id) REFERENCES Zone(id) ON DELETE CASCADE
);

-- ============================================
-- TABLE Client
-- ============================================
CREATE TABLE Client (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL, -- NOTE EXAMEN: en clair pour simplification
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    est_actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLE AdresseLivraison
-- ============================================
CREATE TABLE AdresseLivraison (
    id SERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL,
    quartier_id INTEGER NOT NULL,
    rue VARCHAR(255) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES Client(id) ON DELETE CASCADE,
    FOREIGN KEY (quartier_id) REFERENCES Quartier(id) ON DELETE RESTRICT
);

-- ============================================
-- TABLE Produit
-- ============================================
CREATE TABLE Produit (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('BURGER', 'MENU', 'COMPLEMENT')),
    description TEXT,
    prix DECIMAL(10,2) NOT NULL CHECK (prix >= 0),
    url_image VARCHAR(500), -- URL Cloudinary
    disponible BOOLEAN DEFAULT TRUE,
    est_archive BOOLEAN DEFAULT FALSE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLE MenuItem (Pour composition des menus)
-- ============================================
CREATE TABLE MenuItem (
    id SERIAL PRIMARY KEY,
    menu_id INTEGER NOT NULL, -- Produit de type 'MENU'
    produit_id INTEGER NOT NULL, -- Produit inclus dans le menu
    quantite INTEGER NOT NULL DEFAULT 1 CHECK (quantite > 0),
    ordre INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_id) REFERENCES Produit(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES Produit(id) ON DELETE CASCADE,
    UNIQUE(menu_id, produit_id)
);

-- ============================================
-- TABLE Commande
-- ============================================
CREATE TABLE Commande (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE, -- Format: BRB-YYYYMMDD-XXXX
    client_id INTEGER NOT NULL,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(50) NOT NULL DEFAULT 'EN_ATTENTE' 
        CHECK (statut IN ('EN_ATTENTE', 'CONFIRMEE', 'EN_PREPARATION', 'PRETE', 'EN_LIVRAISON', 'LIVREE', 'ANNULEE')),
    mode_consommation VARCHAR(50) NOT NULL DEFAULT 'SUR_PLACE'
        CHECK (mode_consommation IN ('SUR_PLACE', 'EMPORTER', 'LIVRAISON')),
    montant_produits DECIMAL(10,2) NOT NULL DEFAULT 0,
    frais_livraison DECIMAL(10,2) NOT NULL DEFAULT 0,
    montant_total DECIMAL(10,2) NOT NULL DEFAULT 0,
    adresse_livraison_id INTEGER,
    notes TEXT,
    code_suivi VARCHAR(100) UNIQUE, -- Format: BRB-TRACK-XXXX
    temps_estimation_livraison INTEGER, -- en minutes
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES Client(id) ON DELETE RESTRICT,
    FOREIGN KEY (adresse_livraison_id) REFERENCES AdresseLivraison(id) ON DELETE SET NULL
);

-- ============================================
-- TABLE LigneCommande
-- ============================================
CREATE TABLE LigneCommande (
    id SERIAL PRIMARY KEY,
    commande_id INTEGER NOT NULL,
    produit_id INTEGER NOT NULL,
    quantite INTEGER NOT NULL CHECK (quantite > 0),
    prix_unitaire DECIMAL(10,2) NOT NULL,
    sous_total DECIMAL(10,2) NOT NULL,
    instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (commande_id) REFERENCES Commande(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES Produit(id) ON DELETE RESTRICT
);

-- ============================================
-- TABLE Paiement
-- ============================================
CREATE TABLE Paiement (
    id SERIAL PRIMARY KEY,
    commande_id INTEGER NOT NULL UNIQUE,
    date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    montant DECIMAL(10,2) NOT NULL,
    methode VARCHAR(50) NOT NULL DEFAULT 'ESPECES'
        CHECK (methode IN ('ESPECES', 'INTERNE')),
    statut_paiement VARCHAR(50) NOT NULL DEFAULT 'PAYE'
        CHECK (statut_paiement IN ('PAYE', 'ECHEC')),
    reference VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (commande_id) REFERENCES Commande(id) ON DELETE CASCADE
);

-- ============================================
-- TABLE Livreur
-- ============================================
CREATE TABLE Livreur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20) NOT NULL UNIQUE,
    est_disponible BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLE Livraison
-- ============================================
CREATE TABLE Livraison (
    id SERIAL PRIMARY KEY,
    commande_id INTEGER NOT NULL UNIQUE,
    livreur_id INTEGER NOT NULL,
    date_debut TIMESTAMP,
    date_fin TIMESTAMP,
    statut VARCHAR(50) NOT NULL DEFAULT 'ATTENTE_AFFECTATION'
        CHECK (statut IN ('ATTENTE_AFFECTATION', 'EN_COURS', 'LIVREE', 'ANNULEE')),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (commande_id) REFERENCES Commande(id) ON DELETE CASCADE,
    FOREIGN KEY (livreur_id) REFERENCES Livreur(id) ON DELETE RESTRICT
);

-- ============================================
-- TABLE Gestionnaire
-- ============================================
CREATE TABLE Gestionnaire (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL, -- NOTE EXAMEN: en clair pour simplification
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- INDEXES pour performances
-- ============================================

-- Index pour les recherches fréquentes
CREATE INDEX idx_client_email ON Client(email);
CREATE INDEX idx_client_telephone ON Client(telephone);
CREATE INDEX idx_produit_type ON Produit(type);
CREATE INDEX idx_produit_disponible ON Produit(disponible) WHERE disponible = TRUE;
CREATE INDEX idx_commande_client ON Commande(client_id);
CREATE INDEX idx_commande_statut ON Commande(statut);
CREATE INDEX idx_commande_date ON Commande(date_commande);
CREATE INDEX idx_ligne_commande_commande ON LigneCommande(commande_id);
CREATE INDEX idx_livreur_disponible ON Livreur(est_disponible) WHERE est_disponible = TRUE;
CREATE INDEX idx_quartier_zone ON Quartier(zone_id);

-- Index pour les relations
CREATE INDEX idx_adresse_client ON AdresseLivraison(client_id);
CREATE INDEX idx_menu_item_menu ON MenuItem(menu_id);
CREATE INDEX idx_menu_item_produit ON MenuItem(produit_id);
CREATE INDEX idx_livraison_commande ON Livraison(commande_id);
CREATE INDEX idx_livraison_livreur ON Livraison(livreur_id);

-- ============================================
-- TRIGGERS pour les timestamps
-- ============================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers pour les tables avec updated_at
CREATE TRIGGER update_client_updated_at BEFORE UPDATE ON Client
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_produit_updated_at BEFORE UPDATE ON Produit
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_commande_updated_at BEFORE UPDATE ON Commande
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_livreur_updated_at BEFORE UPDATE ON Livreur
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_livraison_updated_at BEFORE UPDATE ON Livraison
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_gestionnaire_updated_at BEFORE UPDATE ON Gestionnaire
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
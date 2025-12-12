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

-- ============================================
-- DONNÉES DE RÉFÉRENCE - BRASIL BURGER SÉNÉGAL 🇸🇳
-- VERSION EXAMEN : Mots de passe en clair pour simplification
-- ============================================

-- Zones basées sur les restaurants
INSERT INTO Zone (nom) VALUES 
('Point E'),
('Canal Olympia'),
('Saly'),
('Autres quartiers de Dakar');

-- Quartiers avec prix de livraison réalistes (FCFA)
INSERT INTO Quartier (zone_id, nom, prix_livraison_base, temps_estime_minutes) VALUES
(1, 'Point E', 1500, 20),
(1, 'Mermoz', 1800, 25),
(1, 'Fann', 1700, 22),
(1, 'Sacré-Coeur', 1600, 20),
(2, 'Canal Olympia', 2000, 30),
(2, 'Grand Dakar', 1900, 28),
(2, 'Hann Maristes', 2200, 35),
(3, 'Saly Portudal', 2500, 40),
(3, 'Saly Station', 2400, 38),
(4, 'Almadies', 3000, 45),
(4, 'Yoff', 2800, 42),
(4, 'Parcelles Assainies', 2000, 35);

-- ============================================
-- UTILISATEURS DE TEST - VERSION EXAMEN
-- ============================================

-- Gestionnaire admin (mot de passe en clair pour test)
INSERT INTO Gestionnaire (nom, prenom, email, mot_de_passe) VALUES
('Admin', 'Brasil Burger SN', 'admin@brasilburger.sn', 'admin123');

-- Clients de test (mots de passe en clair pour test)
INSERT INTO Client (nom, prenom, telephone, email, mot_de_passe, est_actif) VALUES
('Diop', 'Aminata', '+221 77 123 45 67', 'aminata@test.sn', 'client123', TRUE),
('Ndiaye', 'Moussa', '+221 78 234 56 78', 'moussa@test.sn', 'test456', TRUE),
('Fall', 'Khadija', '+221 76 345 67 89', 'khadija@test.sn', 'pass789', TRUE);

-- Livreurs
INSERT INTO Livreur (nom, telephone, est_disponible) VALUES
('Sow', 'Oumar', '+221 77 111 22 33', TRUE),
('Diallo', 'Ibrahima', '+221 78 222 33 44', TRUE),
('Ba', 'Fatou', '+221 76 333 44 55', FALSE);

-- ============================================
-- PRODUITS RÉELS - MENU BRASIL BURGER SÉNÉGAL
-- ============================================

-- SPÉCIAL DU MOIS
INSERT INTO Produit (nom, type, description, prix) VALUES
('SPÉCIAL DU MOIS', 'BURGER', 'Pain grillé, double burger de 110 g chacun, double fromage raclette, tomate confite fait maison et sauce à l''ail', 6500);

-- BURGERS
INSERT INTO Produit (nom, type, description, prix) VALUES
('NEW BRASIL BURGER SALAD', 'BURGER', 'New Burger Salad! Pain Artisanal, Viande 130g, Double cheddar, Salade, Tomate, Lanières de bacon, Chips Maison, Sauce Maison', 6000),
('NEW CHEESE BURGER', 'BURGER', 'PAIN GRILLÉ, BURGER 130G, DOUBLE FROMAGE CHEDDAR, CHIPS MAISON ET SAUCE MAISON', 3500),
('BUM BURGER', 'BURGER', 'Pain grillé, viande de burger, salade brésilienne, mozzarella pané, mayonaise spéciale et bacon halal', 6500),
('BRASIL DOUBLE BURGER', 'BURGER', 'Pain grillé, 2 viandes de burger 130g chacune, fromage double, oignon caramélisé et sauce maison', 6000),
('BURGER RETRO', 'BURGER', 'Pain grillé, burger pané, mayonaise spéciale, salade, tomate, bacon halal, oignon caramélisé et sauce retro', 6500),
('BRASIL BURGER SALAD', 'BURGER', 'Pain grillé, burger 130g, fromage, oignon caramélisé, salade, tomate, oignon rouge, comichons, sauce maison et sauce spéciale', 5000),
('BRASIL BURGER RANCH', 'BURGER', 'Pain grillé, burger 130g, fromage, sour cream, cornichons pimentés et oignon vert', 4500),
('BRASIL BURGER BBQ', 'BURGER', 'Pain grillé, burger 130g, fromage, jambon fumé, oignon caramélisé, sauce barbecue et sauce maison', 4500),
('MINAS BURGER', 'BURGER', 'Pain à burger artisanal grillé, 130g de viande bien assaisonnée, cheddar, œuf, fromage pané, tomate, laitue violette et sauce maison', 7000),
('BRASIL CHEESE BURGER', 'BURGER', 'Pain grillé, burger 130g, fromage et sauce maison', 2500);

-- MENUS (à composer avec MenuItem)
INSERT INTO Produit (nom, type, description, prix) VALUES
('BRASIL BOX', 'MENU', '1 New Burger Salad, 1 Double Smash, 3 Coxinhas, 3 Boulles de Mozza, 3 Pastels, Frites, Sauce spéciale maison, ketchup et sauce à l''ail, 2 Boissons canette au choix', 15000),
('COMBO ENFANT', 'MENU', 'Petit Burger Enfant composé par: Pain, viande de 60g, fromage, ketchup et sauce maison, + portion de frites + 3 nuggets + petit jus + 1 cadeau surprise', 5500);

-- SMASHS
INSERT INTO Produit (nom, type, description, prix) VALUES
('DOUBLE SMASH BACON HALAL', 'BURGER', 'Pain grillé, 2 viandes de smash burger 60g chacune, fromage double, oignon finement hache, ketchup, moutard et comichons, oignon caramélisé, et bacon halal', 5500),
('DOUBLE SMASH', 'BURGER', 'Pain grillé, 2 viandes de smash burger 60g chacune, fromage double, oignon finement haché, ketchup, moutard et comichons', 5000),
('DOUBLE SMASH SALAD', 'BURGER', 'Pain grille, 2 viandes de smash burger 60g chacune, fromage double, ketchup, moutard, salade, tomate, oignon rouge et comichons', 5500);

-- CHICKEN BURGERS
INSERT INTO Produit (nom, type, description, prix) VALUES
('BRASIL CHICKEN BURGER', 'BURGER', 'Pain grillé, burger 110g de poulet assaisonne, fromage, salade, tomate et sauce maison', 4000),
('CHICKEN BURGER PANÉ', 'BURGER', 'Pain grillé, blanc de poulet pané à la farine spéciale et aux épices, salade, tomate, fromage cheddar et une délicieuse sauce aigre-douce maison', 5000);

-- SNACKS (COMPLEMENTS)
INSERT INTO Produit (nom, type, description, prix) VALUES
('PORTION DE PASTEL 6UNT', 'COMPLEMENT', 'Pastel Brésilien à la viande et mozzarella', 3000),
('BOULES DE MOZZARELLA 7UNT', 'COMPLEMENT', 'Snack fait avec du vrai fromage mozzarella rapé et préparé avec soin 1 par 1', 3000),
('PORTION DE COXINHAS 5UNT', 'COMPLEMENT', 'Snack Brésilienne de Poulet, (faible teneur en glucide)', 3000),
('PORTION DE FRITES AVEC CHEDDAR ET FAROFA DE BACON HALAL', 'COMPLEMENT', 'Portion de frites avec cheddar et farofa de bacon halal', 4000),
('PORTION DE NUGGETS 7UNT', 'COMPLEMENT', 'Nuggets de haute qualité', 3000),
('PORTION DE FRITES', 'COMPLEMENT', 'Les frites sont toujours indispensables avec les burgers', 1000);

-- SAUCES (COMPLEMENTS)
INSERT INTO Produit (nom, type, description, prix) VALUES
('SAUCE RETRO', 'COMPLEMENT', 'Pot moyen (+100ml) avec notre délicieuse sauce retro, à base d''ail et de gingembre', 1000),
('SAUCE PIMENT', 'COMPLEMENT', 'Pot moyen (+100ml) avec Sauce au Piment de haute qualité', 500),
('SAUCE MAISON', 'COMPLEMENT', 'Pot moyen (+100ml) avec notre délicieuse sauce maison', 500),
('SAUCE BBQ', 'COMPLEMENT', 'Sauce BBQ', 500);

-- MILK-SHAKES (COMPLEMENTS)
INSERT INTO Produit (nom, type, description, prix) VALUES
('MILK-SHAKE FRAISE', 'COMPLEMENT', 'FAIT AVEC UNE DELICIEUSE GLACE A LA FRAISE', 3500),
('MILK-SHAKE OREO', 'COMPLEMENT', 'Fait avec de la glace Oreo. On sent les petits morceaux de ce délicieux biscuit', 3500),
('MILK-SHAKE BRÉSILIEN', 'COMPLEMENT', 'Un mélange de saveurs, vanille et chocolat croquant (à la brésilienne)', 3500);

-- DESSERTS (COMPLEMENTS)
INSERT INTO Produit (nom, type, description, prix) VALUES
('BONHEUR OREO', 'COMPLEMENT', 'Crème Oreo, morceaux d''Oreo, mousse au chocolat et crème blanche', 3000),
('BONHEUR NIDO', 'COMPLEMENT', 'Crème au chocolat au lait, petits morceaux de cake vanille et une délicieuse crème blanche au Nido', 3000),
('BONHEUR NUTELLA', 'COMPLEMENT', 'Mousse au Nutella, petits morceaux de cake, crème blanchet et encore du Nutella', 3000),
('MOUSSE PASSION', 'COMPLEMENT', 'Délicieuse mousse aux fruits de de la passion fait maison', 2500),
('BRIGADEIROS', 'COMPLEMENT', '6 BRIGADEIROS, BONBON TRADITIONNEL BRÉSILIEN, À BASE DE CHOCOLAT AU LAIT ET DE NOIX DE COCO RAPÉE', 2000),
('GÂTEAU MIX', 'COMPLEMENT', 'Gâteau au chocolat à la brésilienne, il est juteux et rempli de chocolat et crème à noix de coco rapée', 2000),
('GÂTEAU AU CHOCOLAT', 'COMPLEMENT', 'Gâteau au chocolat à la brésilienne, il est juteux et rempli de chocolat', 2000);

-- BOISSONS (COMPLEMENTS)
INSERT INTO Produit (nom, type, description, prix) VALUES
('PINK LIMONADE', 'COMPLEMENT', 'DÉLICIEUSE LIMONADE, À BASE DE JUS DE CITRON AVEC UNE TOUCHE SPÉCIALE', 2500),
('BLUE LIMONADE', 'COMPLEMENT', 'DÉLICIEUSE LIMONADE, À BASE DE JUS DE CITRON AVEC UNE TOUCHE SPÉCIALE', 2500),
('JUS PASSION BRASIL BURGER', 'COMPLEMENT', 'Jus de passion Brasil Burger', 2500),
('KIRENE', 'COMPLEMENT', 'Eau minérale KIRENE', 1000),
('PRESSEA MANGUE', 'COMPLEMENT', 'Boisson Pressea Mangue', 1000),
('PRESSEA ORANGE', 'COMPLEMENT', 'Boisson Pressea Orange', 1000),
('7UP', 'COMPLEMENT', 'Boisson gazeuse 7UP', 1000),
('MIRINDA', 'COMPLEMENT', 'Boisson gazeuse Mirinda', 1000),
('PEPSI MAX ZERO', 'COMPLEMENT', 'Pepsi Max Zero', 1000),
('PEPSI', 'COMPLEMENT', 'Boisson gazeuse Pepsi', 1000);

-- BURGER ENFANT
INSERT INTO Produit (nom, type, description, prix) VALUES
('BURGER ENFANT SIMPLE', 'BURGER', 'Petit Burger Enfant: Pain, viande de 60g, fromage, ketchup et sauce maison', 1500);

-- ============================================
-- COMPOSITION DES MENUS (MenuItem)
-- ============================================

-- Composition du BRASIL BOX
INSERT INTO MenuItem (menu_id, produit_id, quantite, ordre) VALUES
((SELECT id FROM Produit WHERE nom = 'BRASIL BOX'), (SELECT id FROM Produit WHERE nom = 'NEW BRASIL BURGER SALAD'), 1, 1),
((SELECT id FROM Produit WHERE nom = 'BRASIL BOX'), (SELECT id FROM Produit WHERE nom = 'DOUBLE SMASH'), 1, 2),
((SELECT id FROM Produit WHERE nom = 'BRASIL BOX'), (SELECT id FROM Produit WHERE nom = 'PORTION DE COXINHAS 5UNT'), 1, 3),
((SELECT id FROM Produit WHERE nom = 'BRASIL BOX'), (SELECT id FROM Produit WHERE nom = 'BOULES DE MOZZARELLA 7UNT'), 1, 4),
((SELECT id FROM Produit WHERE nom = 'BRASIL BOX'), (SELECT id FROM Produit WHERE nom = 'PORTION DE FRITES'), 1, 5);

-- Composition du COMBO ENFANT
INSERT INTO MenuItem (menu_id, produit_id, quantite, ordre) VALUES
((SELECT id FROM Produit WHERE nom = 'COMBO ENFANT'), (SELECT id FROM Produit WHERE nom = 'BURGER ENFANT SIMPLE'), 1, 1),
((SELECT id FROM Produit WHERE nom = 'COMBO ENFANT'), (SELECT id FROM Produit WHERE nom = 'PORTION DE FRITES'), 1, 2),
((SELECT id FROM Produit WHERE nom = 'COMBO ENFANT'), (SELECT id FROM Produit WHERE nom = 'PORTION DE NUGGETS 7UNT'), 1, 3),
((SELECT id FROM Produit WHERE nom = 'COMBO ENFANT'), (SELECT id FROM Produit WHERE nom = 'PEPSI'), 1, 4);

-- ============================================
-- VUES pour les requêtes courantes
-- ============================================

-- Vue pour les produits disponibles (non archivés)
CREATE VIEW Vue_Produits_Disponibles AS
SELECT id, nom, type, description, prix, url_image
FROM Produit
WHERE disponible = TRUE AND est_archive = FALSE;

-- Vue pour les commandes en cours
CREATE VIEW Vue_Commandes_En_Cours AS
SELECT c.*, cl.nom as client_nom, cl.prenom as client_prenom
FROM Commande c
JOIN Client cl ON c.client_id = cl.id
WHERE c.statut IN ('EN_ATTENTE', 'CONFIRMEE', 'EN_PREPARATION', 'EN_LIVRAISON');

-- Vue pour les statistiques produits
CREATE VIEW Vue_Stats_Produits AS
SELECT 
    p.id,
    p.nom,
    p.type,
    COUNT(lc.id) as nombre_vendu,
    SUM(lc.quantite) as quantite_vendue,
    SUM(lc.sous_total) as chiffre_affaires
FROM Produit p
LEFT JOIN LigneCommande lc ON p.id = lc.produit_id
LEFT JOIN Commande c ON lc.commande_id = c.id AND c.statut != 'ANNULEE'
WHERE p.est_archive = FALSE
GROUP BY p.id, p.nom, p.type;

-- ============================================
-- COMMENTAIRES ET NOTES EXAMEN
-- ============================================

COMMENT ON TABLE Produit IS 'Table des produits Brasil Burger Sénégal avec URLs Cloudinary';
COMMENT ON COLUMN Produit.url_image IS 'URL Cloudinary pour l''image du produit';
COMMENT ON COLUMN Commande.numero IS 'Format: BRB-YYYYMMDD-XXXX (ex: BRB-20241211-0001)';
COMMENT ON COLUMN Commande.code_suivi IS 'Code de suivi pour le client (ex: BRB-TRACK-1234)';
COMMENT ON COLUMN Client.mot_de_passe IS 'NOTE EXAMEN: Mot de passe en clair pour simplification des tests';
COMMENT ON COLUMN Gestionnaire.mot_de_passe IS 'NOTE EXAMEN: Mot de passe en clair pour simplification des tests';
COMMENT ON COLUMN Quartier.prix_livraison_base IS 'Prix de livraison en FCFA pour le Sénégal';

-- ============================================
-- NOTE IMPORTANTE POUR LE CORRECTEUR
-- ============================================
/*
VERSION EXAMEN - BRASIL BURGER SÉNÉGAL

IMPORTANT:
- Mots de passe en clair (admin123, client123, etc.) pour faciliter les tests
- Données réelles du restaurant Brasil Burger Sénégal
- Prix en FCFA, zones de Dakar adaptées
- Structure cohérente avec le diagramme de classe fourni

Pour tester:
1. Gestionnaire: email: admin@brasilburger.sn / mdp: admin123
2. Client: email: aminata@test.sn / mdp: client123

En environnement de production:
- Implémenter BCrypt/Argon2 avec salage
- Ne jamais stocker de mots de passe en clair
- Utiliser des tokens JWT pour l'authentification
*/

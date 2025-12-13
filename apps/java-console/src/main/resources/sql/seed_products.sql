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
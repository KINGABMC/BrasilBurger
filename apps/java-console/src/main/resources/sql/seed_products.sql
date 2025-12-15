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
('Oumar Sow', '+221 77 111 22 33', TRUE),
('Ibrahima Diallo', '+221 78 222 33 44', TRUE),
('Fatou Ba', '+221 76 333 44 55', FALSE);

-- ============================================
-- PRODUITS RÉELS - MENU BRASIL BURGER SÉNÉGAL
-- ============================================

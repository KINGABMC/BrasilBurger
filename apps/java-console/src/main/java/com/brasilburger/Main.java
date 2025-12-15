package com.brasilburger;

import com.brasilburger.config.DatabaseConfig;
import com.brasilburger.repository.DatabaseConnection;
import com.brasilburger.repository.MenuItemRepository;
import com.brasilburger.repository.ProduitRepository;
import com.brasilburger.console.MenuPrincipal;

public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   BRASIL BURGER - CONSOLE ADMIN");
        System.out.println("=========================================");
        System.out.println();
        
        try {
            DatabaseConfig.printConfig();
            
            if (!DatabaseConfig.testConnection()) {
                System.err.println("❌ ERREUR: Impossible de se connecter à la base de données");
                System.err.println("Vérifiez:");
                System.err.println("1. Que la base de données est accessible");
                System.err.println("2. Les paramètres dans application.properties");
                System.err.println("3. La variable NEONDB_CONNECTION_STRING sur Render");
                System.exit(1);
            }
            
            System.out.println("✅ Connexion à la base de données réussie");
            System.out.println();
            
            DatabaseConnection.getInstance();
            
            ProduitRepository produitRepository = new ProduitRepository();
            MenuItemRepository menuItemRepository = new MenuItemRepository();
            
            if (!produitRepository.testConnection()) {
                System.err.println("❌ ERREUR: Le repository produit ne peut pas accéder aux données");
                System.exit(1);
            }
            
            System.out.println("✅ Tous les composants initialisés avec succès");
            System.out.println();
            
            MenuPrincipal menu = new MenuPrincipal(produitRepository, menuItemRepository);
            menu.afficher();
            
        } catch (Exception e) {
            System.err.println("❌ ERREUR CRITIQUE: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
            System.out.println("L'application va s'arrêter.");
            System.exit(1);
        }
        
        System.out.println();
        System.out.println("=========================================");
        System.out.println("   Merci d'avoir utilisé Brasil Burger!");
        System.out.println("   À bientôt! 🇸🇳🍔");
        System.out.println("=========================================");
    }
}
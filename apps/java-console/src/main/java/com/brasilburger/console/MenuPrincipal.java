package com.brasilburger.console;

import com.brasilburger.controller.JavaProduitController;
import com.brasilburger.controller.MenuController;
import com.brasilburger.repository.MenuItemRepository;
import com.brasilburger.repository.ProduitRepository;
import com.brasilburger.service.ImageService;
import com.brasilburger.service.JavaProduitService;
import com.brasilburger.service.MenuCompositionService;

public class MenuPrincipal {
    private final ProduitMenu produitMenu;
    private final MenuCompositionUI menuCompositionUI;
    private final ImageUploadUI imageUploadUI;
    private final JavaProduitController controller;
    private boolean enExecution = true;
    
    public MenuPrincipal(ProduitRepository produitRepository, MenuItemRepository menuItemRepository) {
        // Initialisation des services
        JavaProduitService produitService = new JavaProduitService(produitRepository);
        MenuCompositionService menuService = new MenuCompositionService(menuItemRepository, produitRepository);
        ImageService imageService = new ImageService();
        
        // Initialisation des contrôleurs
        this.controller = new JavaProduitController(produitService, imageService);
        MenuController menuController = new MenuController(menuService);
        
        // Initialisation des vues
        this.produitMenu = new ProduitMenu(controller);
        this.menuCompositionUI = new MenuCompositionUI(menuController);
        this.imageUploadUI = new ImageUploadUI(imageService);
    }
    
    public void afficher() {
        while (enExecution) {
            ConsoleUtils.afficherEnTete("Menu Principal - Brasil Burger");
            afficherOptions();
            
            try {
                int choix = ConsoleUtils.lireEntier("Votre choix (1-6): ");
                traiterChoix(choix);
            } catch (Exception e) {
                ConsoleUtils.afficherErreur("Erreur: " + e.getMessage());
                ConsoleUtils.attendreEntree();
            }
        }
    }
    
    private void afficherOptions() {
        System.out.println();
        System.out.println("1. 🍔 Gérer les produits");
        System.out.println("2. 🧾 Composition des menus");
        System.out.println("3. 🖼️  Gestion des images");
        System.out.println("4. 📊 Voir les statistiques");
        System.out.println("5. ⚙️  Configuration");
        System.out.println("6. ❌ Quitter");
        System.out.println();
    }
    
    private void traiterChoix(int choix) {
        switch (choix) {
            case 1:
                produitMenu.afficher();
                break;
            case 2:
                menuCompositionUI.afficher();
                break;
            case 3:
                imageUploadUI.afficher();
                break;
            case 4:
                afficherStatistiques();
                break;
            case 5:
                afficherConfiguration();
                break;
            case 6:
                quitter();
                break;
            default:
                ConsoleUtils.afficherAvertissement("Choix invalide. Veuillez entrer un nombre entre 1 et 6.");
                ConsoleUtils.attendreEntree();
        }
    }
    
    private void afficherStatistiques() {
        ConsoleUtils.afficherEnTete("Statistiques");
        
        try {
            int totalProduits = controller.compterProduits();
            int produitsDisponibles = controller.compterProduitsDisponibles();
            int produitsArchives = controller.listerProduitsArchives().size();
            
            System.out.println("📊 Statistiques des produits:");
            System.out.println("   • Total produits: " + totalProduits);
            System.out.println("   • Produits disponibles: " + produitsDisponibles);
            System.out.println("   • Produits archivés: " + produitsArchives);
            System.out.println("   • Taux de disponibilité: " + 
                (totalProduits > 0 ? String.format("%.1f%%", (produitsDisponibles * 100.0 / totalProduits)) : "0%"));
            
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("Impossible de récupérer les statistiques: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    private void afficherConfiguration() {
        ConsoleUtils.afficherEnTete("Configuration");
        
        System.out.println("Application Brasil Burger Console");
        System.out.println("Version: 1.0.0");
        System.out.println("Développé pour l'examen");
        System.out.println();
        System.out.println("Cette application gère:");
        System.out.println("• Les produits (burgers, menus, compléments)");
        System.out.println("• L'archivage et restauration des produits");
        System.out.println("• Les statistiques de vente");
        System.out.println("• Upload d'images via Cloudinary");
        
        System.out.println();
        System.out.println("🔍 Test de connexion:");
        System.out.println("   • Base de données: " + (controller.testerConnexion() ? "✅ Connecté" : "❌ Erreur"));
        
        ConsoleUtils.attendreEntree();
    }
    
    private void quitter() {
        System.out.println();
        if (ConsoleUtils.demanderConfirmation("Êtes-vous sûr de vouloir quitter?")) {
            enExecution = false;
            ConsoleUtils.afficherSucces("Fermeture de l'application...");
        } else {
            ConsoleUtils.afficherInfo("Retour au menu principal");
        }
    }
}
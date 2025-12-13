package com.brasilburger.console;

import com.brasilburger.repository.ProduitRepository;
import com.brasilburger.service.JavaProduitService;

/**
 * Menu principal de l'application console
 */
public class MenuPrincipal {
    private final ProduitMenu produitMenu;
    private boolean enExecution = true;
    
    public MenuPrincipal(ProduitRepository produitRepository) {
        JavaProduitService produitService = new JavaProduitService(produitRepository);
        this.produitMenu = new ProduitMenu(produitService);
    }
    
    /**
     * Affiche le menu principal et gère la navigation
     */
    public void afficher() {
        while (enExecution) {
            ConsoleUtils.afficherEnTete("Menu Principal - Brasil Burger");
            afficherOptions();
            
            try {
                int choix = ConsoleUtils.lireEntier("Votre choix (1-4): ");
                traiterChoix(choix);
            } catch (Exception e) {
                ConsoleUtils.afficherErreur("Erreur: " + e.getMessage());
                ConsoleUtils.attendreEntree();
            }
        }
    }
    
    /**
     * Affiche les options du menu
     */
    private void afficherOptions() {
        System.out.println();
        System.out.println("1. 🍔 Gérer les produits");
        System.out.println("2. 📊 Voir les statistiques");
        System.out.println("3. ⚙️  Configuration");
        System.out.println("4. ❌ Quitter");
        System.out.println();
    }
    
    /**
     * Traite le choix de l'utilisateur
     */
    private void traiterChoix(int choix) {
        switch (choix) {
            case 1:
                produitMenu.afficher();
                break;
                
            case 2:
                afficherStatistiques();
                break;
                
            case 3:
                afficherConfiguration();
                break;
                
            case 4:
                quitter();
                break;
                
            default:
                ConsoleUtils.afficherAvertissement("Choix invalide. Veuillez entrer un nombre entre 1 et 4.");
                ConsoleUtils.attendreEntree();
        }
    }
    
    /**
     * Affiche les statistiques de l'application
     */
    private void afficherStatistiques() {
        ConsoleUtils.afficherEnTete("Statistiques");
        
        try {
            int totalProduits = produitMenu.getProduitService().compterProduits();
            int produitsDisponibles = produitMenu.getProduitService().compterProduitsDisponibles();
            
            System.out.println("📊 Statistiques des produits:");
            System.out.println("   • Total produits: " + totalProduits);
            System.out.println("   • Produits disponibles: " + produitsDisponibles);
            System.out.println("   • Produits archivés: " + (totalProduits - produitsDisponibles));
            
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("Impossible de récupérer les statistiques: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    /**
     * Affiche la configuration actuelle
     */
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
        
        ConsoleUtils.attendreEntree();
    }
    
    /**
     * Quitte l'application
     */
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
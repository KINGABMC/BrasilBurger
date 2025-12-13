package com.brasilburger.console;

import com.brasilburger.controller.MenuController;
import com.brasilburger.model.MenuItem;
import java.util.List;

public class MenuCompositionUI {
    private final MenuController controller;
    
    public MenuCompositionUI(MenuController controller) {
        this.controller = controller;
    }
    
    public void afficher() {
        boolean enExecution = true;
        
        while (enExecution) {
            ConsoleUtils.afficherEnTete("Composition des Menus");
            afficherOptions();
            
            try {
                int choix = ConsoleUtils.lireEntier("Votre choix (1-4): ");
                
                switch (choix) {
                    case 1:
                        ajouterProduitAuMenu();
                        break;
                    case 2:
                        listerProduitsDuMenu();
                        break;
                    case 3:
                        supprimerProduitDuMenu();
                        break;
                    case 4:
                        enExecution = false;
                        ConsoleUtils.afficherInfo("Retour au menu précédent...");
                        break;
                    default:
                        ConsoleUtils.afficherAvertissement("Choix invalide (1-4).");
                }
            } catch (Exception e) {
                ConsoleUtils.afficherErreur("Erreur: " + e.getMessage());
                ConsoleUtils.attendreEntree();
            }
        }
    }
    
    private void afficherOptions() {
        System.out.println();
        System.out.println("1. ➕ Ajouter un produit à un menu");
        System.out.println("2. 📋 Lister les produits d'un menu");
        System.out.println("3. 🗑️  Supprimer un produit d'un menu");
        System.out.println("4. ↩️  Retour");
        System.out.println();
    }
    
    private void ajouterProduitAuMenu() {
        ConsoleUtils.afficherEnTete("Ajouter un produit à un menu");
        
        try {
            long menuId = ConsoleUtils.lireEntier("ID du menu: ");
            long produitId = ConsoleUtils.lireEntier("ID du produit: ");
            int quantite = ConsoleUtils.lireEntier("Quantité: ");
            
            MenuItem item = controller.ajouterProduitAuMenu(menuId, produitId, quantite);
            ConsoleUtils.afficherSucces("✅ Produit ajouté au menu: " + item);
            
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("❌ Erreur: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    private void listerProduitsDuMenu() {
        ConsoleUtils.afficherEnTete("Produits d'un menu");
        
        try {
            long menuId = ConsoleUtils.lireEntier("ID du menu: ");
            List<MenuItem> items = controller.listerProduitsDuMenu(menuId);
            
            if (items.isEmpty()) {
                System.out.println("📭 Aucun produit dans ce menu.");
            } else {
                System.out.println("📦 Total: " + items.size() + " produit(s) dans le menu");
                System.out.println();
                
                for (MenuItem item : items) {
                    System.out.printf("• Produit ID: %d | Quantité: %d | Ordre: %d%n",
                        item.getProduitId(), item.getQuantite(), item.getOrdre());
                }
            }
            
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("❌ Erreur: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    private void supprimerProduitDuMenu() {
        ConsoleUtils.afficherEnTete("Supprimer un produit d'un menu");
        
        try {
            long menuItemId = ConsoleUtils.lireEntier("ID de l'item menu à supprimer: ");
            
            if (ConsoleUtils.demanderConfirmation("Êtes-vous sûr?")) {
                boolean supprime = controller.supprimerProduitDuMenu(menuItemId);
                if (supprime) {
                    ConsoleUtils.afficherSucces("✅ Produit supprimé du menu.");
                }
            }
            
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("❌ Erreur: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
}
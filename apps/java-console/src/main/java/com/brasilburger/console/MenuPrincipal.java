package com.brasilburger.console;

import com.brasilburger.repository.ProduitRepository;
import com.brasilburger.service.JavaProduitService;

import java.util.Scanner;

/**
 * Menu principal de l'application console
 */
public class MenuPrincipal {
    private final Scanner scanner;
    private final ProduitMenu produitMenu;
    private boolean enExecution = true;
    
    public MenuPrincipal(ProduitRepository produitRepository) {
        this.scanner = new Scanner(System.in);
        JavaProduitService produitService = new JavaProduitService(produitRepository);
        this.produitMenu = new ProduitMenu(scanner, produitService);
    }
    
    /**
     * Affiche le menu principal et gère la navigation
     */
    public void afficher() {
        while (enExecution) {
            afficherEnTete();
            afficherOptions();
            
            try {
                int choix = lireChoix();
                traiterChoix(choix);
            } catch (Exception e) {
                System.err.println("❌ Erreur: " + e.getMessage());
                attendreEntree();
            }
        }
        
        scanner.close();
    }
    
    /**
     * Affiche l'en-tête du menu
     */
    private void afficherEnTete() {
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println("        MENU PRINCIPAL - BRASIL BURGER");
        System.out.println("════════════════════════════════════════════");
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
        System.out.print("Votre choix (1-4): ");
    }
    
    /**
     * Lit le choix de l'utilisateur
     */
    private int lireChoix() {
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return -1;
            }
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
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
                System.out.println("⚠️  Choix invalide. Veuillez entrer un nombre entre 1 et 4.");
                attendreEntree();
        }
    }
    
    /**
     * Affiche les statistiques de l'application
     */
    private void afficherStatistiques() {
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println("            STATISTIQUES");
        System.out.println("════════════════════════════════════════════");
        System.out.println();
        
        try {
            int totalProduits = produitMenu.getProduitService().compterProduits();
            int produitsDisponibles = produitMenu.getProduitService().compterProduitsDisponibles();
            
            System.out.println("📊 Statistiques des produits:");
            System.out.println("   • Total produits: " + totalProduits);
            System.out.println("   • Produits disponibles: " + produitsDisponibles);
            System.out.println("   • Produits archivés: " + (totalProduits - produitsDisponibles));
            
        } catch (Exception e) {
            System.out.println("❌ Impossible de récupérer les statistiques: " + e.getMessage());
        }
        
        attendreEntree();
    }
    
    /**
     * Affiche la configuration actuelle
     */
    private void afficherConfiguration() {
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println("            CONFIGURATION");
        System.out.println("════════════════════════════════════════════");
        System.out.println();
        
        System.out.println("Application Brasil Burger Console");
        System.out.println("Version: 1.0.0");
        System.out.println("Développé pour l'examen");
        System.out.println();
        System.out.println("Cette application gère:");
        System.out.println("• Les produits (burgers, menus, compléments)");
        System.out.println("• L'archivage et restauration des produits");
        System.out.println("• Les statistiques de vente");
        
        attendreEntree();
    }
    
    /**
     * Quitte l'application
     */
    private void quitter() {
        System.out.println();
        System.out.print("Êtes-vous sûr de vouloir quitter? (O/N): ");
        String confirmation = scanner.nextLine().trim().toUpperCase();
        
        if (confirmation.equals("O") || confirmation.equals("OUI")) {
            enExecution = false;
            System.out.println("✅ Fermeture de l'application...");
        } else {
            System.out.println("⏸️  Retour au menu principal");
        }
    }
    
    /**
     * Attend que l'utilisateur appuie sur Entrée
     */
    private void attendreEntree() {
        System.out.println();
        System.out.print("Appuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }
}
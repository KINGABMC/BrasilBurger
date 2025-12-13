package com.brasilburger.console;

import com.brasilburger.model.Produit;
import com.brasilburger.model.TypeProduit;
import com.brasilburger.service.JavaProduitService;

import java.util.List;
import java.util.Scanner;

/**
 * Menu de gestion des produits
 */
public class ProduitMenu {
    private final Scanner scanner;
    private final JavaProduitService produitService;
    private boolean enExecution = true;
    
    public ProduitMenu(Scanner scanner, JavaProduitService produitService) {
        this.scanner = scanner;
        this.produitService = produitService;
    }
    
    /**
     * Affiche le menu des produits
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
    }
    
    /**
     * Affiche l'en-tête du menu
     */
    private void afficherEnTete() {
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println("        GESTION DES PRODUITS");
        System.out.println("════════════════════════════════════════════");
    }
    
    /**
     * Affiche les options du menu
     */
    private void afficherOptions() {
        System.out.println();
        System.out.println("1. 📋 Lister tous les produits");
        System.out.println("2. 🔍 Rechercher un produit");
        System.out.println("3. ➕ Créer un nouveau produit");
        System.out.println("4. ✏️  Modifier un produit");
        System.out.println("5. 📁 Archiver un produit");
        System.out.println("6. 📤 Restaurer un produit archivé");
        System.out.println("7. 🗑️  Supprimer définitivement");
        System.out.println("8. ↩️  Retour au menu principal");
        System.out.println();
        System.out.print("Votre choix (1-8): ");
    }
    
    /**
     * Lit le choix de l'utilisateur
     */
    private int lireChoix() {
        try {
            String input = scanner.nextLine().trim();
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
                listerTousProduits();
                break;
                
            case 2:
                rechercherProduit();
                break;
                
            case 3:
                creerProduit();
                break;
                
            case 4:
                modifierProduit();
                break;
                
            case 5:
                archiverProduit();
                break;
                
            case 6:
                restaurerProduit();
                break;
                
            case 7:
                supprimerProduit();
                break;
                
            case 8:
                retourMenuPrincipal();
                break;
                
            default:
                System.out.println("⚠️  Choix invalide. Veuillez entrer un nombre entre 1 et 8.");
                attendreEntree();
        }
    }
    
    /**
     * Liste tous les produits
     */
    private void listerTousProduits() {
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println("            LISTE DES PRODUITS");
        System.out.println("════════════════════════════════════════════");
        System.out.println();
        
        try {
            List<Produit> produits = produitService.listerTousProduits();
            
            if (produits.isEmpty()) {
                System.out.println("📭 Aucun produit trouvé.");
            } else {
                System.out.println("📦 Total: " + produits.size() + " produit(s)");
                System.out.println();
                
                for (Produit produit : produits) {
                    String statut = produit.getEstArchive() ? "📁 ARCHIVÉ" : 
                                   (produit.getDisponible() ? "✅ DISPONIBLE" : "❌ INDISPONIBLE");
                    
                    System.out.printf("[%d] %s - %s - %.2f FCFA - %s%n",
                        produit.getId(),
                        produit.getNom(),
                        produit.getType().getValeurDB(),
                        produit.getPrix(),
                        statut);
                    
                    if (produit.getDescription() != null && !produit.getDescription().isEmpty()) {
                        System.out.println("   📝 " + produit.getDescription());
                    }
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
        
        attendreEntree();
    }
    
    /**
     * Recherche un produit par nom
     */
    private void rechercherProduit() {
        System.out.println();
        System.out.print("🔍 Entrez le nom ou une partie du nom à rechercher: ");
        String recherche = scanner.nextLine().trim();
        
        if (recherche.isEmpty()) {
            System.out.println("⚠️  Veuillez entrer un terme de recherche.");
            attendreEntree();
            return;
        }
        
        try {
            List<Produit> resultats = produitService.rechercherProduitsParNom(recherche);
            
            System.out.println();
            System.out.println("Résultats pour \"" + recherche + "\":");
            System.out.println();
            
            if (resultats.isEmpty()) {
                System.out.println("🔍 Aucun produit trouvé.");
            } else {
                System.out.println("📊 " + resultats.size() + " produit(s) trouvé(s):");
                System.out.println();
                
                for (Produit produit : resultats) {
                    System.out.printf("• [%d] %s - %s - %.2f FCFA%n",
                        produit.getId(),
                        produit.getNom(),
                        produit.getType().getValeurDB(),
                        produit.getPrix());
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
        
        attendreEntree();
    }
    
    /**
     * Crée un nouveau produit
     */
    private void creerProduit() {
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println("        CRÉATION D'UN PRODUIT");
        System.out.println("════════════════════════════════════════════");
        System.out.println();
        
        try {
            System.out.print("Nom du produit: ");
            String nom = scanner.nextLine().trim();
            
            System.out.println("Types disponibles: " + TypeProduit.getTypesDisponibles());
            System.out.print("Type (BURGER/MENU/COMPLEMENT): ");
            String typeStr = scanner.nextLine().trim().toUpperCase();
            TypeProduit type = TypeProduit.depuisValeurDB(typeStr);
            
            System.out.print("Description (optionnel): ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) description = null;
            
            System.out.print("Prix (en FCFA): ");
            String prixStr = scanner.nextLine().trim();
            Double prix = Double.parseDouble(prixStr);
            
            Produit produit = produitService.creerProduit(nom, type, description, prix);
            System.out.println("✅ Produit créé avec succès: " + produit);
            
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la création: " + e.getMessage());
        }
        
        attendreEntree();
    }
    
    /**
     * Modifie un produit existant
     */
    private void modifierProduit() {
        System.out.println();
        System.out.print("Entrez l'ID du produit à modifier: ");
        String idStr = scanner.nextLine().trim();
        
        try {
            Long id = Long.parseLong(idStr);
            Produit produit = produitService.obtenirProduitParId(id);
            
            System.out.println();
            System.out.println("Produit actuel: " + produit);
            System.out.println();
            
            System.out.print("Nouveau nom (laissez vide pour ne pas modifier): ");
            String nom = scanner.nextLine().trim();
            if (nom.isEmpty()) nom = null;
            
            System.out.print("Nouveau type (BURGER/MENU/COMPLEMENT, laissez vide pour ne pas modifier): ");
            String typeStr = scanner.nextLine().trim().toUpperCase();
            TypeProduit type = typeStr.isEmpty() ? null : TypeProduit.depuisValeurDB(typeStr);
            
            System.out.print("Nouvelle description (laissez vide pour ne pas modifier): ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) description = null;
            
            System.out.print("Nouveau prix (laissez vide pour ne pas modifier): ");
            String prixStr = scanner.nextLine().trim();
            Double prix = prixStr.isEmpty() ? null : Double.parseDouble(prixStr);
            
            Produit produitModifie = produitService.modifierProduit(id, nom, type, description, prix);
            System.out.println("✅ Produit modifié avec succès: " + produitModifie);
            
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la modification: " + e.getMessage());
        }
        
        attendreEntree();
    }
    
    /**
     * Archive un produit
     */
    private void archiverProduit() {
        System.out.println();
        System.out.print("Entrez l'ID du produit à archiver: ");
        String idStr = scanner.nextLine().trim();
        
        try {
            Long id = Long.parseLong(idStr);
            
            System.out.print("Êtes-vous sûr de vouloir archiver ce produit? (O/N): ");
            String confirmation = scanner.nextLine().trim().toUpperCase();
            
            if (confirmation.equals("O") || confirmation.equals("OUI")) {
                produitService.archiverProduit(id);
            } else {
                System.out.println("⏸️  Archivage annulé.");
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'archivage: " + e.getMessage());
        }
        
        attendreEntree();
    }
    
    /**
     * Restaure un produit archivé
     */
    private void restaurerProduit() {
        System.out.println();
        System.out.print("Entrez l'ID du produit à restaurer: ");
        String idStr = scanner.nextLine().trim();
        
        try {
            Long id = Long.parseLong(idStr);
            produitService.restaurerProduit(id);
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la restauration: " + e.getMessage());
        }
        
        attendreEntree();
    }
    
    /**
     * Supprime définitivement un produit
     */
    private void supprimerProduit() {
        System.out.println();
        System.out.println("⚠️  ATTENTION: Cette action est irréversible!");
        System.out.print("Entrez l'ID du produit à supprimer définitivement: ");
        String idStr = scanner.nextLine().trim();
        
        try {
            Long id = Long.parseLong(idStr);
            
            System.out.print("Êtes-vous ABSOLUMENT sûr? (tapez 'SUPPRIMER' pour confirmer): ");
            String confirmation = scanner.nextLine().trim();
            
            if (confirmation.equals("SUPPRIMER")) {
                boolean supprime = produitService.supprimerProduitDefinitivement(id);
                if (supprime) {
                    System.out.println("✅ Produit supprimé définitivement.");
                }
            } else {
                System.out.println("⏸️  Suppression annulée.");
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de la suppression: " + e.getMessage());
        }
        
        attendreEntree();
    }
    
    /**
     * Retourne au menu principal
     */
    private void retourMenuPrincipal() {
        enExecution = false;
        System.out.println("↩️  Retour au menu principal...");
    }
    
    /**
     * Attend que l'utilisateur appuie sur Entrée
     */
    private void attendreEntree() {
        System.out.println();
        System.out.print("Appuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }
    
    /**
     * Getter pour le service produit (utilisé par MenuPrincipal)
     */
    public JavaProduitService getProduitService() {
        return produitService;
    }
}
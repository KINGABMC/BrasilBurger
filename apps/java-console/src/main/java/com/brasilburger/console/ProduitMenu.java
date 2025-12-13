package com.brasilburger.console;

import com.brasilburger.controller.JavaProduitController;
import com.brasilburger.model.Produit;
import com.brasilburger.model.TypeProduit;
import java.io.File;
import java.util.List;

public class ProduitMenu {
    private final JavaProduitController controller;
    private boolean enExecution = true;
    
    public ProduitMenu(JavaProduitController controller) {
        this.controller = controller;
    }
    
    public void afficher() {
        while (enExecution) {
            afficherMenuPrincipal();
            int choix = saisirChoixUtilisateur();
            traiterChoix(choix);
        }
    }
    
    private void afficherMenuPrincipal() {
        ConsoleUtils.afficherEnTete("Gestion des Produits");
        System.out.println();
        System.out.println("1. 📋 Lister tous les produits");
        System.out.println("2. 🔍 Rechercher un produit");
        System.out.println("3. ➕ Créer un nouveau produit");
        System.out.println("4. ✏️  Modifier un produit");
        System.out.println("5. 📁 Archiver un produit");
        System.out.println("6. 📤 Restaurer un produit archivé");
        System.out.println("7. 🗑️  Supprimer définitivement");
        System.out.println("8. 🏷️  Lister par type");
        System.out.println("9. 📊 Produits archivés");
        System.out.println("10. ↩️  Retour au menu principal");
        System.out.println();
    }
    
    private int saisirChoixUtilisateur() {
        try {
            return ConsoleUtils.lireEntier("Votre choix (1-10): ");
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("Erreur: " + e.getMessage());
            return -1;
        }
    }
    
    private void traiterChoix(int choix) {
        switch (choix) {
            case 1: afficherListeProduits(controller.listerTousProduits(), "Liste des Produits"); break;
            case 2: rechercherProduit(); break;
            case 3: creerProduit(); break;
            case 4: modifierProduit(); break;
            case 5: archiverProduit(); break;
            case 6: restaurerProduit(); break;
            case 7: supprimerProduit(); break;
            case 8: listerParType(); break;
            case 9: afficherListeProduits(controller.listerProduitsArchives(), "Produits Archivés"); break;
            case 10: enExecution = false; ConsoleUtils.afficherInfo("Retour au menu principal..."); break;
            default: ConsoleUtils.afficherAvertissement("Choix invalide."); ConsoleUtils.attendreEntree();
        }
    }
    
    private void afficherListeProduits(List<Produit> produits, String titre) {
        ConsoleUtils.afficherEnTete(titre);
        
        if (produits.isEmpty()) {
            System.out.println("📭 Aucun produit trouvé.");
        } else {
            System.out.println("📦 Total: " + produits.size() + " produit(s)");
            System.out.println();
            
            for (Produit produit : produits) {
                String statut = produit.getEstArchive() ? "📁 ARCHIVÉ" : 
                               (produit.getDisponible() ? "✅ DISPONIBLE" : "❌ INDISPONIBLE");
                
                System.out.printf("[%d] %s - %s - %.2f FCFA - %s%n",
                    produit.getId(), produit.getNom(), produit.getType().getValeurDB(),
                    produit.getPrix(), statut);
                
                if (produit.getDescription() != null && !produit.getDescription().isEmpty()) {
                    System.out.println("   📝 " + produit.getDescription());
                }
                if (produit.getUrlImage() != null) {
                    System.out.println("   🖼️  Image: OUI");
                }
                System.out.println();
            }
        }
        ConsoleUtils.attendreEntree();
    }
    
    private void rechercherProduit() {
        System.out.println();
        String recherche = ConsoleUtils.lireStringObligatoire("🔍 Entrez le nom ou une partie du nom à rechercher: ");
        List<Produit> resultats = controller.rechercherProduits(recherche);
        afficherListeProduits(resultats, "Résultats pour \"" + recherche + "\"");
    }
    
    private void creerProduit() {
        ConsoleUtils.afficherEnTete("Création d'un Produit");
        
        try {
            String nom = ConsoleUtils.lireStringObligatoire("Nom du produit: ");
            
            System.out.println("Types disponibles: " + TypeProduit.getTypesDisponibles());
            String typeStr = ConsoleUtils.lireStringObligatoire("Type (BURGER/MENU/COMPLEMENT): ").toUpperCase();
            TypeProduit type = TypeProduit.depuisValeurDB(typeStr);
            
            String description = ConsoleUtils.lireStringOptionnelle("Description (optionnel): ");
            double prix = ConsoleUtils.lireDouble("Prix (en FCFA): ");
            
            File imageFile = null;
            System.out.print("Chemin de l'image (optionnel): ");
            String imagePath = ConsoleUtils.lireStringOptionnelle("");
            
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                imageFile = new File(imagePath.trim());
            }
            
            Produit produit = controller.creerProduit(nom, type, description, prix, imageFile);
            ConsoleUtils.afficherSucces("✅ Produit créé avec succès: " + produit);
            
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("❌ Erreur lors de la création: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    private void modifierProduit() {
        ConsoleUtils.afficherEnTete("Modification d'un Produit");
        
        try {
            long id = ConsoleUtils.lireEntier("Entrez l'ID du produit à modifier: ");
            Produit produit = controller.obtenirProduit(id);
            
            System.out.println();
            ConsoleUtils.afficherInfo("Produit actuel: " + produit);
            System.out.println();
            
            String nom = ConsoleUtils.lireStringOptionnelle("Nouveau nom (laissez vide pour ne pas modifier): ");
            
            System.out.print("Nouveau type (BURGER/MENU/COMPLEMENT, laissez vide pour ne pas modifier): ");
            String typeStr = ConsoleUtils.lireStringOptionnelle("");
            TypeProduit type = typeStr == null ? null : TypeProduit.depuisValeurDB(typeStr.toUpperCase());
            
            String description = ConsoleUtils.lireStringOptionnelle("Nouvelle description (laissez vide pour ne pas modifier): ");
            
            System.out.print("Nouveau prix (laissez vide pour ne pas modifier): ");
            String prixStr = ConsoleUtils.lireStringOptionnelle("");
            Double prix = prixStr == null ? null : Double.parseDouble(prixStr);
            
            Produit produitModifie = controller.modifierProduit(id, nom, type, description, prix);
            ConsoleUtils.afficherSucces("✅ Produit modifié avec succès: " + produitModifie);
            
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("❌ Erreur lors de la modification: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    private void archiverProduit() {
        ConsoleUtils.afficherEnTete("Archivage d'un Produit");
        
        try {
            long id = ConsoleUtils.lireEntier("Entrez l'ID du produit à archiver: ");
            
            if (ConsoleUtils.demanderConfirmation("Êtes-vous sûr de vouloir archiver ce produit?")) {
                controller.archiverProduit(id);
            } else {
                ConsoleUtils.afficherInfo("Archivage annulé.");
            }
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("❌ Erreur lors de l'archivage: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    private void restaurerProduit() {
        ConsoleUtils.afficherEnTete("Restauration d'un Produit Archivé");
        
        try {
            long id = ConsoleUtils.lireEntier("Entrez l'ID du produit à restaurer: ");
            controller.restaurerProduit(id);
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("❌ Erreur lors de la restauration: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    private void supprimerProduit() {
        ConsoleUtils.afficherEnTete("Suppression Définitive d'un Produit");
        
        System.out.println("⚠️  ATTENTION: Cette action est irréversible!");
        System.out.println();
        
        try {
            long id = ConsoleUtils.lireEntier("Entrez l'ID du produit à supprimer définitivement: ");
            
            System.out.print("Êtes-vous ABSOLUMENT sûr? (tapez 'SUPPRIMER' pour confirmer): ");
            String confirmation = ConsoleUtils.lireStringObligatoire("");
            
            if (confirmation.equals("SUPPRIMER")) {
                boolean supprime = controller.supprimerProduit(id);
                if (supprime) {
                    ConsoleUtils.afficherSucces("✅ Produit supprimé définitivement.");
                }
            } else {
                ConsoleUtils.afficherInfo("Suppression annulée.");
            }
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("❌ Erreur lors de la suppression: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    private void listerParType() {
        ConsoleUtils.afficherEnTete("Produits par Type");
        
        try {
            System.out.println("Types disponibles: " + TypeProduit.getTypesDisponibles());
            String typeStr = ConsoleUtils.lireStringObligatoire("Entrez le type: ").toUpperCase();
            TypeProduit type = TypeProduit.depuisValeurDB(typeStr);
            
            List<Produit> produits = controller.listerProduitsParType(type);
            afficherListeProduits(produits, "Produits de type " + type);
            
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("❌ Erreur: " + e.getMessage());
            ConsoleUtils.attendreEntree();
        }
    }
}
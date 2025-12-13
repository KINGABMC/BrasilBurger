package com.brasilburger.console;

import com.brasilburger.model.Produit;
import com.brasilburger.model.TypeProduit;
import com.brasilburger.service.JavaProduitService;

import java.util.List;

/**
 * Menu de gestion des produits
 */
public class ProduitMenu {
    private final JavaProduitService produitService;
    private boolean enExecution = true;
    
    public ProduitMenu(JavaProduitService produitService) {
        this.produitService = produitService;
    }
    
    /**
     * Affiche le menu des produits
     */
    public void afficher() {
        while (enExecution) {
            ConsoleUtils.afficherEnTete("Gestion des Produits");
            afficherOptions();
            
            try {
                int choix = ConsoleUtils.lireEntier("Votre choix (1-8): ");
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
        System.out.println("1. 📋 Lister tous les produits");
        System.out.println("2. 🔍 Rechercher un produit");
        System.out.println("3. ➕ Créer un nouveau produit");
        System.out.println("4. ✏️  Modifier un produit");
        System.out.println("5. 📁 Archiver un produit");
        System.out.println("6. 📤 Restaurer un produit archivé");
        System.out.println("7. 🗑️  Supprimer définitivement");
        System.out.println("8. ↩️  Retour au menu principal");
        System.out.println();
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
                ConsoleUtils.afficherAvertissement("Choix invalide. Veuillez entrer un nombre entre 1 et 8.");
                ConsoleUtils.attendreEntree();
        }
    }
    
    /**
     * Liste tous les produits
     */
    private void listerTousProduits() {
        ConsoleUtils.afficherEnTete("Liste des Produits");
        
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
            ConsoleUtils.afficherErreur("Erreur: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    /**
     * Recherche un produit par nom
     */
    private void rechercherProduit() {
        System.out.println();
        String recherche = ConsoleUtils.lireStringObligatoire("🔍 Entrez le nom ou une partie du nom à rechercher: ");
        
        try {
            List<Produit> resultats = produitService.rechercherProduitsParNom(recherche);
            
            System.out.println();
            ConsoleUtils.afficherSousTitre("Résultats pour \"" + recherche + "\"");
            System.out.println();
            
            if (resultats.isEmpty()) {
                System.out.println("🔍 Aucun produit trouvé.");
            } else {
                ConsoleUtils.afficherInfo(resultats.size() + " produit(s) trouvé(s):");
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
            ConsoleUtils.afficherErreur("Erreur: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    /**
     * Crée un nouveau produit
     */
    private void creerProduit() {
        ConsoleUtils.afficherEnTete("Création d'un Produit");
        
        try {
            String nom = ConsoleUtils.lireStringObligatoire("Nom du produit: ");
            
            System.out.println("Types disponibles: " + TypeProduit.getTypesDisponibles());
            String typeStr = ConsoleUtils.lireStringObligatoire("Type (BURGER/MENU/COMPLEMENT): ").toUpperCase();
            TypeProduit type = TypeProduit.depuisValeurDB(typeStr);
            
            String description = ConsoleUtils.lireStringOptionnelle("Description (optionnel): ");
            double prix = ConsoleUtils.lireDouble("Prix (en FCFA): ");
            
            Produit produit = produitService.creerProduit(nom, type, description, prix);
            ConsoleUtils.afficherSucces("Produit créé avec succès: " + produit);
            
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("Erreur lors de la création: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    /**
     * Modifie un produit existant
     */
    private void modifierProduit() {
        ConsoleUtils.afficherEnTete("Modification d'un Produit");
        
        try {
            long id = ConsoleUtils.lireEntier("Entrez l'ID du produit à modifier: ");
            Produit produit = produitService.obtenirProduitParId(id);
            
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
            
            Produit produitModifie = produitService.modifierProduit(id, nom, type, description, prix);
            ConsoleUtils.afficherSucces("Produit modifié avec succès: " + produitModifie);
            
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("Erreur lors de la modification: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    /**
     * Archive un produit
     */
    private void archiverProduit() {
        ConsoleUtils.afficherEnTete("Archivage d'un Produit");
        
        try {
            long id = ConsoleUtils.lireEntier("Entrez l'ID du produit à archiver: ");
            
            if (ConsoleUtils.demanderConfirmation("Êtes-vous sûr de vouloir archiver ce produit?")) {
                produitService.archiverProduit(id);
            } else {
                ConsoleUtils.afficherInfo("Archivage annulé.");
            }
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("Erreur lors de l'archivage: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    /**
     * Restaure un produit archivé
     */
    private void restaurerProduit() {
        ConsoleUtils.afficherEnTete("Restauration d'un Produit Archivé");
        
        try {
            long id = ConsoleUtils.lireEntier("Entrez l'ID du produit à restaurer: ");
            produitService.restaurerProduit(id);
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("Erreur lors de la restauration: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    /**
     * Supprime définitivement un produit
     */
    private void supprimerProduit() {
        ConsoleUtils.afficherEnTete("Suppression Définitive d'un Produit");
        
        System.out.println("⚠️  ATTENTION: Cette action est irréversible!");
        System.out.println();
        
        try {
            long id = ConsoleUtils.lireEntier("Entrez l'ID du produit à supprimer définitivement: ");
            
            System.out.print("Êtes-vous ABSOLUMENT sûr? (tapez 'SUPPRIMER' pour confirmer): ");
            String confirmation = ConsoleUtils.lireStringObligatoire("");
            
            if (confirmation.equals("SUPPRIMER")) {
                boolean supprime = produitService.supprimerProduitDefinitivement(id);
                if (supprime) {
                    ConsoleUtils.afficherSucces("Produit supprimé définitivement.");
                }
            } else {
                ConsoleUtils.afficherInfo("Suppression annulée.");
            }
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("Erreur lors de la suppression: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    /**
     * Retourne au menu principal
     */
    private void retourMenuPrincipal() {
        enExecution = false;
        ConsoleUtils.afficherInfo("Retour au menu principal...");
    }
    
    /**
     * Getter pour le service produit (utilisé par MenuPrincipal)
     */
    public JavaProduitService getProduitService() {
        return produitService;
    }
}
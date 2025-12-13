package com.brasilburger.service;

import com.brasilburger.model.Produit;
import com.brasilburger.model.TypeProduit;
import com.brasilburger.repository.ProduitRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service pour la gestion des produits
 * Contient la logique métier des opérations sur les produits
 */
public class JavaProduitService {
    private final ProduitRepository produitRepository;
    
    public JavaProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }
    
    /**
     * Crée un nouveau produit
     */
    public Produit creerProduit(String nom, TypeProduit type, String description, Double prix) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Le type de produit est obligatoire");
        }
        
        if (prix == null || prix < 0) {
            throw new IllegalArgumentException("Le prix doit être un nombre positif");
        }
        
        Produit produit = new Produit(nom.trim(), type, description, prix);
        
        try {
            return produitRepository.save(produit);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère un produit par son ID
     */
    public Produit obtenirProduitParId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }
        
        try {
            Produit produit = produitRepository.findById(id);
            if (produit == null) {
                throw new RuntimeException("Produit non trouvé avec l'ID: " + id);
            }
            return produit;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Liste tous les produits
     */
    public List<Produit> listerTousProduits() {
        try {
            return produitRepository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des produits: " + e.getMessage(), e);
        }
    }
    
    /**
     * Liste les produits disponibles (non archivés et en stock)
     */
    public List<Produit> listerProduitsDisponibles() {
        try {
            List<Produit> tousProduits = produitRepository.findAll();
            return tousProduits.stream()
                .filter(Produit::getDisponible)
                .filter(p -> !p.getEstArchive())
                .toList();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des produits disponibles: " + e.getMessage(), e);
        }
    }
    
    /**
     * Liste les produits par type
     */
    public List<Produit> listerProduitsParType(TypeProduit type) {
        if (type == null) {
            throw new IllegalArgumentException("Le type est obligatoire");
        }
        
        try {
            List<Produit> tousProduits = produitRepository.findAll();
            return tousProduits.stream()
                .filter(p -> type.equals(p.getType()))
                .filter(p -> !p.getEstArchive()) // Exclure les archivés
                .toList();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des produits par type: " + e.getMessage(), e);
        }
    }
    
    /**
     * Met à jour un produit existant
     */
    public Produit modifierProduit(Long id, String nom, TypeProduit type, String description, Double prix) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }
        
        Produit produit = obtenirProduitParId(id);
        
        // Mise à jour des champs modifiés
        if (nom != null && !nom.trim().isEmpty()) {
            produit.setNom(nom.trim());
        }
        
        if (type != null) {
            produit.setType(type);
        }
        
        if (description != null) {
            produit.setDescription(description);
        }
        
        if (prix != null && prix >= 0) {
            produit.setPrix(prix);
        }
        
        produit.setUpdatedAt(LocalDateTime.now());
        
        try {
            return produitRepository.update(produit);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Archive un produit (soft delete)
     */
    public void archiverProduit(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }
        
        Produit produit = obtenirProduitParId(id);
        
        if (produit.getEstArchive()) {
            System.out.println("⚠️  Le produit ID " + id + " est déjà archivé");
            return;
        }
        
        produit.setEstArchive(true);
        produit.setDisponible(false);
        produit.setUpdatedAt(LocalDateTime.now());
        
        try {
            produitRepository.update(produit);
            System.out.println("✅ Produit ID " + id + " archivé avec succès");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'archivage du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Restaure un produit archivé
     */
    public void restaurerProduit(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }
        
        Produit produit = obtenirProduitParId(id);
        
        if (!produit.getEstArchive()) {
            System.out.println("⚠️  Le produit ID " + id + " n'est pas archivé");
            return;
        }
        
        produit.setEstArchive(false);
        produit.setDisponible(true);
        produit.setUpdatedAt(LocalDateTime.now());
        
        try {
            produitRepository.update(produit);
            System.out.println("✅ Produit ID " + id + " restauré avec succès");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la restauration du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Supprime définitivement un produit
     */
    public boolean supprimerProduitDefinitivement(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID invalide");
        }
        
        try {
            // Vérifie d'abord que le produit existe
            Produit produit = produitRepository.findById(id);
            if (produit == null) {
                System.out.println("⚠️  Produit ID " + id + " non trouvé");
                return false;
            }
            
            // Suppression physique
            return produitRepository.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche des produits par nom (contient)
     */
    public List<Produit> rechercherProduitsParNom(String recherche) {
        if (recherche == null || recherche.trim().isEmpty()) {
            return listerTousProduits();
        }
        
        String terme = recherche.trim().toLowerCase();
        
        try {
            List<Produit> tousProduits = produitRepository.findAll();
            return tousProduits.stream()
                .filter(p -> p.getNom().toLowerCase().contains(terme))
                .filter(p -> !p.getEstArchive())
                .toList();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de produits: " + e.getMessage(), e);
        }
    }
    
    /**
     * Vérifie si un produit existe
     */
    public boolean produitExiste(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        
        try {
            return produitRepository.findById(id) != null;
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Compte le nombre total de produits
     */
    public int compterProduits() {
        try {
            return produitRepository.count();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des produits: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compte les produits disponibles
     */
    public int compterProduitsDisponibles() {
        return listerProduitsDisponibles().size();
    }
    
    /**
     * Teste la connexion au service
     */
    public boolean testerConnexion() {
        try {
            return produitRepository.testConnection();
        } catch (Exception e) {
            return false;
        }
    }
}
package com.brasilburger.controller;

import com.brasilburger.model.Produit;
import com.brasilburger.model.TypeProduit;
import com.brasilburger.service.JavaProduitService;
import com.brasilburger.service.ImageService;
import java.io.File;
import java.util.List;

public class JavaProduitController {
    private final JavaProduitService produitService;
    private final ImageService imageService;
    
    public JavaProduitController(JavaProduitService produitService, ImageService imageService) {
        this.produitService = produitService;
        this.imageService = imageService;
    }
    
    public Produit creerProduit(String nom, TypeProduit type, String description, 
                                Double prix, File imageFile) throws Exception {
        String imageUrl = null;
        if (imageFile != null && imageFile.exists()) {
            imageUrl = imageService.uploadImage(imageFile);
        }
        return produitService.creerProduit(nom, type, description, prix, imageUrl);
    }
    
    public Produit obtenirProduit(Long id) {
        return produitService.obtenirProduitParId(id);
    }
    
    public List<Produit> listerTousProduits() {
        return produitService.listerTousProduits();
    }
    
    public List<Produit> listerProduitsParType(TypeProduit type) {
        return produitService.listerProduitsParTypeRepository(type);
    }
    
    public Produit modifierProduit(Long id, String nom, TypeProduit type, 
                                   String description, Double prix) {
        return produitService.modifierProduit(id, nom, type, description, prix);
    }
    
    public void archiverProduit(Long id) {
        produitService.archiverProduit(id);
    }
    
    public void restaurerProduit(Long id) {
        produitService.restaurerProduit(id);
    }
    
    public boolean supprimerProduit(Long id) {
        return produitService.supprimerProduitDefinitivement(id);
    }
    
    public List<Produit> rechercherProduits(String recherche) {
        return produitService.rechercherProduitsParNom(recherche);
    }
    
    public List<Produit> listerProduitsArchives() {
        return produitService.listerProduitsArchives();
    }
    
    public int compterProduits() {
        return produitService.compterProduits();
    }
    
    public int compterProduitsDisponibles() {
        return produitService.compterProduitsDisponibles();
    }
    
    public boolean testerConnexion() {
        return produitService.testerConnexion();
    }
}
package com.brasilburger.model;

import java.time.LocalDateTime;

/**
 * Entité Produit correspondant à la table "produit" du MLD
 */
public class Produit {
    private Long id;
    private String nom;
    private TypeProduit type;
    private String description;
    private Double prix;
    private String urlImage;
    private Boolean disponible;
    private Boolean estArchive;
    private LocalDateTime dateCreation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Produit() {
        this.disponible = true;
        this.estArchive = false;
        this.dateCreation = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Produit(String nom, TypeProduit type, String description, Double prix) {
        this();
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.prix = prix;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public TypeProduit getType() { return type; }
    public void setType(TypeProduit type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }
    
    public String getUrlImage() { return urlImage; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
    
    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
    
    public Boolean getEstArchive() { return estArchive; }
    public void setEstArchive(Boolean estArchive) { this.estArchive = estArchive; }
    
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    

    @Override
    public String toString() {
        return String.format("Produit{id=%d, nom='%s', type=%s, prix=%.2f}", 
            id, nom, type, prix);
    }
}
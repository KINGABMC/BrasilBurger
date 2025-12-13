package com.brasilburger.model;

/**
 * Entité MenuItem correspondant à la table "menu_item du MLD
 * " 
 */
public class MenuItem {
    private Long id;
    private Long menuId;
    private Long produitId;
    private Integer quantite;
    private Integer ordre;
    
    public MenuItem() {
        this.quantite = 1;
        this.ordre = 1;
    }
    
    public MenuItem(Long menuId, Long produitId, Integer quantite, Integer ordre) {
        this();
        this.menuId = menuId;
        this.produitId = produitId;
        this.quantite = quantite != null ? quantite : 1;
        this.ordre = ordre != null ? ordre : 1;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getMenuId() { return menuId; }
    public void setMenuId(Long menuId) { this.menuId = menuId; }
    
    public Long getProduitId() { return produitId; }
    public void setProduitId(Long produitId) { this.produitId = produitId; }
    
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { 
        this.quantite = (quantite != null && quantite > 0) ? quantite : 1; 
    }
    
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { 
        this.ordre = (ordre != null && ordre > 0) ? ordre : 1; 
    }
    
    @Override
    public String toString() {
        return String.format("MenuItem{id=%d, menuId=%d, produitId=%d, quantite=%d}", 
            id, menuId, produitId, quantite);
    }
}
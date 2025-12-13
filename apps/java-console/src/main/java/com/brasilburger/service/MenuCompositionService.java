package com.brasilburger.service;

import com.brasilburger.model.MenuItem;
import com.brasilburger.repository.MenuItemRepository;
import com.brasilburger.repository.ProduitRepository;
import java.sql.SQLException;
import java.util.List;

public class MenuCompositionService {
    private final MenuItemRepository menuItemRepository;
    private final ProduitRepository produitRepository;
    
    public MenuCompositionService(MenuItemRepository menuItemRepository, ProduitRepository produitRepository) {
        this.menuItemRepository = menuItemRepository;
        this.produitRepository = produitRepository;
    }
    
    public MenuItem ajouterProduitAuMenu(Long menuId, Long produitId, Integer quantite) {
        try {
            if (produitRepository.findById(produitId) == null) {
                throw new IllegalArgumentException("Produit non trouvé avec ID: " + produitId);
            }
            
            MenuItem menuItem = new MenuItem(menuId, produitId, quantite, 1);
            return menuItemRepository.addToMenu(menuItem);
            
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout au menu: " + e.getMessage(), e);
        }
    }
    
    public List<MenuItem> listerProduitsDuMenu(Long menuId) {
        try {
            return menuItemRepository.getMenuItems(menuId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du menu: " + e.getMessage(), e);
        }
    }
    
    public boolean supprimerProduitDuMenu(Long menuItemId) {
        try {
            return menuItemRepository.removeFromMenu(menuItemId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression: " + e.getMessage(), e);
        }
    }
}
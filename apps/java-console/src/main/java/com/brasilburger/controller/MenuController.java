package com.brasilburger.controller;

import com.brasilburger.model.MenuItem;
import com.brasilburger.service.MenuCompositionService;
import java.util.List;

public class MenuController {
    private final MenuCompositionService menuService;
    
    public MenuController(MenuCompositionService menuService) {
        this.menuService = menuService;
    }
    
    public MenuItem ajouterProduitAuMenu(Long menuId, Long produitId, Integer quantite) {
        return menuService.ajouterProduitAuMenu(menuId, produitId, quantite);
    }
    
    public List<MenuItem> listerProduitsDuMenu(Long menuId) {
        return menuService.listerProduitsDuMenu(menuId);
    }
    
    public boolean supprimerProduitDuMenu(Long menuItemId) {
        return menuService.supprimerProduitDuMenu(menuItemId);
    }
    
    public String getInfoMenu() {
        return "Gestion de la composition des menus\n" +
               "Les menus sont composés de produits via la table menu_item";
    }
}
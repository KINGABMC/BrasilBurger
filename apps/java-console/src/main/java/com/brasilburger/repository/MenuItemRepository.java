package com.brasilburger.repository;

import com.brasilburger.model.MenuItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemRepository {
    private final DatabaseConnection dbConnection;
    
    public MenuItemRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public MenuItem addToMenu(MenuItem menuItem) throws SQLException {
        String sql = "INSERT INTO menuitem (menu_id, produit_id, quantite, ordre) VALUES (?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, menuItem.getMenuId());
            stmt.setLong(2, menuItem.getProduitId());
            stmt.setInt(3, menuItem.getQuantite());
            stmt.setInt(4, menuItem.getOrdre());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    menuItem.setId(rs.getLong("id"));
                    return menuItem;
                }
            }
        }
        throw new SQLException("Échec de l'ajout au menu");
    }
    
    public List<MenuItem> getMenuItems(Long menuId) throws SQLException {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menuitem WHERE menu_id = ? ORDER BY ordre";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, menuId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem();
                    item.setId(rs.getLong("id"));
                    item.setMenuId(rs.getLong("menu_id"));
                    item.setProduitId(rs.getLong("produit_id"));
                    item.setQuantite(rs.getInt("quantite"));
                    item.setOrdre(rs.getInt("ordre"));
                    items.add(item);
                }
            }
        }
        return items;
    }
    
    public boolean removeFromMenu(Long menuItemId) throws SQLException {
        String sql = "DELETE FROM menuitem WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, menuItemId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }
}
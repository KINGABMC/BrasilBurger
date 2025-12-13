package com.brasilburger.repository;

import com.brasilburger.model.Produit;
import com.brasilburger.model.TypeProduit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository pour l'entité Produit
 * Implémente les opérations CRUD de base
 */
public class ProduitRepository {
    private final DatabaseConnection dbConnection;
    
    public ProduitRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Sauvegarde un nouveau produit
     */
    public Produit save(Produit produit) throws SQLException {
        String sql = "INSERT INTO produit (nom, type, description, prix, url_image, disponible, " +
                    "est_archive, date_creation, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "RETURNING id";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Conversion enum → string pour la DB
            String typeDB = produit.getType() != null ? 
                produit.getType().getValeurDB() : TypeProduit.BURGER.getValeurDB();
            
            stmt.setString(1, produit.getNom());
            stmt.setString(2, typeDB);
            stmt.setString(3, produit.getDescription());
            stmt.setDouble(4, produit.getPrix());
            stmt.setString(5, produit.getUrlImage());
            stmt.setBoolean(6, produit.getDisponible());
            stmt.setBoolean(7, produit.getEstArchive());
            stmt.setTimestamp(8, Timestamp.valueOf(produit.getDateCreation()));
            stmt.setTimestamp(9, Timestamp.valueOf(produit.getCreatedAt()));
            stmt.setTimestamp(10, Timestamp.valueOf(produit.getUpdatedAt()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    produit.setId(rs.getLong("id"));
                    System.out.println("✅ Produit créé avec ID: " + produit.getId());
                    return produit;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la sauvegarde du produit: " + e.getMessage());
            throw e;
        }
        
        throw new SQLException("Échec de la création du produit, aucun ID retourné");
    }
    
    /**
     * Trouve un produit par son ID
     */
    public Produit findById(Long id) throws SQLException {
        String sql = "SELECT * FROM produit WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduit(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche du produit ID " + id + ": " + e.getMessage());
            throw e;
        }
        
        return null; // Produit non trouvé
    }
    
    /**
     * Récupère tous les produits
     */
    public List<Produit> findAll() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit ORDER BY id";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
            
            System.out.println("✅ " + produits.size() + " produit(s) trouvé(s)");
            return produits;
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des produits: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Met à jour un produit existant
     */
    public Produit update(Produit produit) throws SQLException {
        String sql = "UPDATE produit SET nom = ?, type = ?, description = ?, prix = ?, " +
                    "url_image = ?, disponible = ?, est_archive = ?, updated_at = ? " +
                    "WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Conversion enum → string
            String typeDB = produit.getType() != null ? 
                produit.getType().getValeurDB() : TypeProduit.BURGER.getValeurDB();
            
            stmt.setString(1, produit.getNom());
            stmt.setString(2, typeDB);
            stmt.setString(3, produit.getDescription());
            stmt.setDouble(4, produit.getPrix());
            stmt.setString(5, produit.getUrlImage());
            stmt.setBoolean(6, produit.getDisponible());
            stmt.setBoolean(7, produit.getEstArchive());
            stmt.setTimestamp(8, Timestamp.valueOf(produit.getUpdatedAt()));
            stmt.setLong(9, produit.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Produit ID " + produit.getId() + " mis à jour");
                return produit;
            } else {
                throw new SQLException("Aucun produit trouvé avec l'ID: " + produit.getId());
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du produit: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Supprime un produit par son ID
     */
    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM produit WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Produit ID " + id + " supprimé");
                return true;
            } else {
                System.out.println("⚠️  Aucun produit trouvé avec l'ID: " + id);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du produit: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Convertit un ResultSet en objet Produit
     */
    private Produit mapResultSetToProduit(ResultSet rs) throws SQLException {
        Produit produit = new Produit();
        
        produit.setId(rs.getLong("id"));
        produit.setNom(rs.getString("nom"));
        
        // Conversion string → enum
        String typeDB = rs.getString("type");
        produit.setType(TypeProduit.depuisValeurDB(typeDB));
        
        produit.setDescription(rs.getString("description"));
        produit.setPrix(rs.getDouble("prix"));
        produit.setUrlImage(rs.getString("url_image"));
        produit.setDisponible(rs.getBoolean("disponible"));
        produit.setEstArchive(rs.getBoolean("est_archive"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            produit.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            produit.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            produit.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return produit;
    }
    
    /**
     * Teste la connexion et la validité du repository
     */
    public boolean testConnection() {
        try {
            return dbConnection.testConnection();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Compte le nombre total de produits
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM produit";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du comptage des produits: " + e.getMessage());
            throw e;
        }
    }
}
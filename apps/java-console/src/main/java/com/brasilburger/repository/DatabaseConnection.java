package com.brasilburger.repository;

import com.brasilburger.config.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestionnaire de connexion à la base de données avec pool HikariCP
 * Singleton pour assurer une seule instance de pool dans l'application
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;
    
    private DatabaseConnection() {
        initializeDataSource();
    }
    
    /**
     * Obtient l'instance singleton
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Initialise le pool de connexions HikariCP
     */
    private void initializeDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            
            // Configuration de base
            config.setJdbcUrl(DatabaseConfig.getDatabaseUrl());
            config.setUsername(DatabaseConfig.getUsername());
            config.setPassword(DatabaseConfig.getPassword());
            
            // Configuration du pool
            config.setMaximumPoolSize(DatabaseConfig.getPoolSize());
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000); // 30 secondes
            config.setIdleTimeout(600000); // 10 minutes
            config.setMaxLifetime(1800000); // 30 minutes
            
            // Optimisations PostgreSQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            // Validation des connexions
            config.setConnectionTestQuery("SELECT 1");
            config.setValidationTimeout(5000);
            
            dataSource = new HikariDataSource(config);
            System.out.println("✅ Pool de connexions HikariCP initialisé");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation du pool de connexions: " + e.getMessage());
            throw new RuntimeException("Impossible d'initialiser la connexion à la base de données", e);
        }
    }
    
    /**
     * Obtient une connexion depuis le pool
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = dataSource.getConnection();
            if (conn == null || conn.isClosed()) {
                throw new SQLException("La connexion obtenue est nulle ou fermée");
            }
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'obtention d'une connexion: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Teste la connexion à la base de données
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && conn.isValid(2);
        } catch (SQLException e) {
            System.err.println("❌ Test de connexion échoué: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ferme le pool de connexions
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("✅ Pool de connexions fermé");
        }
    }
    
    /**
     * Vérifie si le pool est actif
     */
    public boolean isPoolActive() {
        return dataSource != null && !dataSource.isClosed();
    }
    
    /**
     * Statistiques du pool (pour monitoring)
     */
    public void printPoolStats() {
        if (dataSource != null) {
            System.out.println("=== Statistiques Pool HikariCP ===");
            System.out.println("Connexions actives: " + dataSource.getHikariPoolMXBean().getActiveConnections());
            System.out.println("Connexions inactives: " + dataSource.getHikariPoolMXBean().getIdleConnections());
            System.out.println("Connexions totales: " + dataSource.getHikariPoolMXBean().getTotalConnections());
            System.out.println("Threads en attente: " + dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
            System.out.println("===================================");
        }
    }
}
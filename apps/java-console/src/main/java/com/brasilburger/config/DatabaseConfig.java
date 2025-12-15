package com.brasilburger.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Configuration de la connexion à NeonDB PostgreSQL
 * Gère à la fois le développement local et le déploiement Render
 */
public class DatabaseConfig {
    private static final String PROPERTIES_FILE = "application.properties";
    private static Properties properties = new Properties();
    
    static {
        loadProperties();
    }
    
    /**
     * Charge les propriétés depuis le fichier application.properties
     */
    private static void loadProperties() {
    System.out.println("🔍 DEBUG: Chargement " + PROPERTIES_FILE);
    
    try (InputStream input = DatabaseConfig.class.getClassLoader()
            .getResourceAsStream(PROPERTIES_FILE)) {
        
        if (input != null) {
            System.out.println("✅ Fichier trouvé");
            properties.load(input);
            System.out.println("📄 db.url = " + properties.getProperty("db.url"));
            System.out.println("📄 db.username = " + properties.getProperty("db.username"));
        } else {
            System.out.println("❌ Fichier NON TROUVÉ");
            setDefaultProperties();
        }
    } catch (IOException e) {
        System.err.println("Erreur: " + e.getMessage());
        setDefaultProperties();
    }
}
    
    /**
     * Définit les propriétés par défaut pour le développement
     */
    private static void setDefaultProperties() {
        properties.setProperty("db.url", "jdbc:postgresql://localhost:5432/brasilburger_local");
        properties.setProperty("db.username", "postgres");
        properties.setProperty("db.password", "postgres");
        properties.setProperty("db.pool.size", "5");
    }
    
    /**
     * Obtient l'URL de connexion à la base de données
     * Priorité: variable d'environnement > fichier properties > valeur par défaut
     */
    public static String getDatabaseUrl() {
        // 1. Vérifie la variable d'environnement Render
        String renderConnection = System.getenv("NEONDB_CONNECTION_STRING");
        if (renderConnection != null && !renderConnection.trim().isEmpty()) {
            System.out.println("✅ Utilisation de la connexion NeonDB Render");
            return renderConnection;
        }
        
        // 2. Sinon utilise le fichier properties
        String url = properties.getProperty("db.url");
        System.out.println("🔧 Mode développement local");
        return url;
    }
    
    /**
     * Obtient le nom d'utilisateur
     */
    public static String getUsername() {
        return properties.getProperty("db.username", "postgres");
    }
    
    /**
     * Obtient le mot de passe
     */
    public static String getPassword() {
        return properties.getProperty("db.password", "postgres");
    }
    
    /**
     * Obtient la taille du pool de connexions
     */
    public static int getPoolSize() {
        try {
            return Integer.parseInt(properties.getProperty("db.pool.size", "5"));
        } catch (NumberFormatException e) {
            return 5;
        }
    }
    
    /**
     * Crée une connexion à la base de données
     */
    // In your DatabaseConfig.java file, replace the getConnection() method
public static Connection getConnection() throws SQLException {
    String url = getDatabaseUrl();
    System.out.println("🔗 Tentative de connexion à: " + maskPassword(url));

    // Get the username and password from properties
    String username = getUsername();
    String password = getPassword();

    Properties props = new Properties();
    props.setProperty("user", username);
    props.setProperty("password", password);

    try {
        // Connect with the username and password properties
        Connection conn = DriverManager.getConnection(url, props);
        System.out.println("✅ Connexion à la base de données établie");
        return conn;
    } catch (SQLException e) {
        System.err.println("❌ Échec de connexion à la base de données: " + e.getMessage());
        throw e;
    }
}
    /**
     * Teste la connexion à la base de données
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Masque le mot de passe dans l'URL pour les logs
     */
    private static String maskPassword(String url) {
        if (url == null) return "null";
        return url.replaceAll("password=[^&]*", "password=****");
    }
    
    /**
     * Affiche la configuration actuelle (sans mot de passe)
     */
    public static void printConfig() {
        System.out.println("=== Configuration Base de Données ===");
        System.out.println("URL: " + maskPassword(getDatabaseUrl()));
        System.out.println("Utilisateur: " + getUsername());
        System.out.println("Taille pool: " + getPoolSize());
        System.out.println("Mode: " + (System.getenv("NEONDB_CONNECTION_STRING") != null ? "Render" : "Local"));
        System.out.println("=====================================");
    }
}
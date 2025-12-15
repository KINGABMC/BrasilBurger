package com.brasilburger.config;

import com.cloudinary.Cloudinary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class CloudinaryConfig {

    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = CloudinaryConfig.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input != null) {
                properties.load(input);
                System.out.println("☁️  Cloudinary config chargée depuis fichier");
            } else {
                throw new IllegalStateException("❌ application.properties introuvable");
            }

        } catch (IOException e) {
            throw new RuntimeException("❌ Erreur chargement Cloudinary config", e);
        }
    }

    public static Cloudinary getInstance() {

        Map<String, Object> config = new HashMap<>();

        // Environnement applicatif : development | production
        String environment = properties.getProperty("app.environment", "development");

        String cloudName = properties.getProperty("cloudinary.cloud_name");
        String apiKey = properties.getProperty("cloudinary.api_key");
        String apiSecret = properties.getProperty("cloudinary.api_secret");

        System.out.println("☁️  DEBUG - app.environment = " + environment);
        System.out.println("☁️  DEBUG - cloudinary.cloud_name = " + cloudName);

        if ("development".equalsIgnoreCase(environment)) {

            // MODE DEV → simulation
            System.out.println("⚠️  Cloudinary en MODE DÉVELOPPEMENT (simulation)");
            cloudName = "dev";
            apiKey = "dev_key";
            apiSecret = "dev_secret";

        } 
        else if ("production".equalsIgnoreCase(environment)) {

            // MODE PROD → configuration réelle obligatoire
            if (cloudName == null || apiKey == null || apiSecret == null) {
                throw new IllegalStateException(
                    "❌ Configuration Cloudinary INCOMPLÈTE pour l'environnement PRODUCTION"
                );
            }

            System.out.println("✅ Cloudinary en MODE PRODUCTION : " + cloudName);
        } 
        else {
            throw new IllegalStateException(
                "❌ Environnement applicatif inconnu : " + environment
            );
        }

        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        config.put("secure", true);

        return new Cloudinary(config);
    }
}

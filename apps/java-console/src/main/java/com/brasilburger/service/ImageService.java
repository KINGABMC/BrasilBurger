package com.brasilburger.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.brasilburger.config.CloudinaryConfig;
import java.io.File;
import java.util.Map;

/**
 * Service pour la gestion des images avec Cloudinary
 */
public class ImageService {
    private final Cloudinary cloudinary;
    
    public ImageService() {
        this.cloudinary = CloudinaryConfig.getInstance();
    }
    
    /**
     * Upload une image vers Cloudinary
     * @param imageFile Fichier image à uploader
     * @return URL de l'image sur Cloudinary
     * @throws Exception Si l'upload échoue
     */
    public String uploadImage(File imageFile) throws Exception {
        if (imageFile == null || !imageFile.exists()) {
            throw new IllegalArgumentException("Fichier image introuvable");
        }
        
        if (!imageFile.isFile()) {
            throw new IllegalArgumentException("Le chemin ne correspond pas à un fichier");
        }
        
        // Vérifier l'extension
        String fileName = imageFile.getName().toLowerCase();
        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && 
            !fileName.endsWith(".png") && !fileName.endsWith(".gif")) {
            throw new IllegalArgumentException("Format d'image non supporté. Utilisez .jpg, .jpeg, .png ou .gif");
        }
        
        try {
            // Upload vers Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(imageFile, 
                ObjectUtils.asMap(
                    "folder", "brasilburger/products",
                    "public_id", "produit_" + System.currentTimeMillis()
                ));
            
            // Retourner l'URL sécurisée
            return (String) uploadResult.get("secure_url");
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload vers Cloudinary: " + e.getMessage(), e);
        }
    }
    
    /**
     * Supprime une image de Cloudinary
     * @param imageUrl URL de l'image à supprimer
     * @return true si supprimé avec succès
     */
    public boolean deleteImage(String imageUrl) throws Exception {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }
        
        try {
            // Extraire le public_id de l'URL
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId == null) {
                return false;
            }
            
            // Supprimer de Cloudinary
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de l'image: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extrait le public_id d'une URL Cloudinary
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        // Format: https://res.cloudinary.com/cloudname/image/upload/v1234567/folder/filename.jpg
        String[] parts = imageUrl.split("/");
        if (parts.length < 9) return null;
        
        // Récupérer le public_id (dernière partie sans extension)
        String lastPart = parts[parts.length - 1];
        return lastPart.substring(0, lastPart.lastIndexOf('.'));
    }
    
    /**
     * Vérifie si le service Cloudinary est configuré
     */
    public boolean isConfigured() {
        return cloudinary != null;
    }
}
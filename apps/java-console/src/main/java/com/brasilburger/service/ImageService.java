package com.brasilburger.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.brasilburger.config.CloudinaryConfig;
import java.io.File;
import java.util.Map;

public class ImageService {
    private final Cloudinary cloudinary;
    
    public ImageService() {
        this.cloudinary = CloudinaryConfig.getInstance();
    }
    
    public String uploadImage(File imageFile) throws Exception {
        if (imageFile == null || !imageFile.exists()) {
            throw new IllegalArgumentException("Fichier image introuvable");
        }
        
        if (!imageFile.isFile()) {
            throw new IllegalArgumentException("Le chemin ne correspond pas à un fichier");
        }
        
        String fileName = imageFile.getName().toLowerCase();
        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && 
            !fileName.endsWith(".png") && !fileName.endsWith(".gif")) {
            throw new IllegalArgumentException("Format d'image non supporté");
        }
        
        try {
            System.out.println("📤 Upload vers Cloudinary...");
            Map<?, ?> uploadResult = cloudinary.uploader().upload(imageFile, 
                ObjectUtils.asMap(
                    "folder", "brasilburger/products",
                    "public_id", "produit_" + System.currentTimeMillis()
                    
                ));
             String secureUrl = (String) uploadResult.get("secure_url");
             System.out.println("✅ URL Cloudinary générée : " + secureUrl);
             System.out.println("📁 Dossier : brasilburger/products");
             System.out.println("🔧 Resource type : " + uploadResult.get("resource_type"));
            return secureUrl;
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload vers Cloudinary: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteImage(String imageUrl) throws Exception {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }
        
        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId == null) {
                return false;
            }
            
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de l'image: " + e.getMessage(), e);
        }
    }
    
    private String extractPublicIdFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("/");
        if (parts.length < 9) return null;
        String lastPart = parts[parts.length - 1];
        return lastPart.substring(0, lastPart.lastIndexOf('.'));
    }
    
    public boolean isConfigured() {
        return cloudinary != null;
    }
}
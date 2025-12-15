package com.brasilburger.console;

import com.brasilburger.service.ImageService;
import java.io.File;

public class ImageUploadUI {
    private final ImageService imageService;
    
    public ImageUploadUI(ImageService imageService) {
        this.imageService = imageService;
    }
    
    public void afficher() {
        boolean enExecution = true;
        
        while (enExecution) {
            ConsoleUtils.afficherEnTete("Gestion des Images");
            afficherOptions();
            
            try {
                int choix = ConsoleUtils.lireEntier("Votre choix (1-3): ");
                
                switch (choix) {
                    case 1:
                        uploaderImage();
                        break;
                    case 2:
                        testerService();
                        break;
                    case 3:
                        enExecution = false;
                        ConsoleUtils.afficherInfo("Retour...");
                        break;
                    default:
                        ConsoleUtils.afficherAvertissement("Choix invalide (1-3).");
                }
            } catch (Exception e) {
                ConsoleUtils.afficherErreur("Erreur: " + e.getMessage());
                ConsoleUtils.attendreEntree();
            }
        }
    }
    
    private void afficherOptions() {
        System.out.println();
        System.out.println("1. 📤 Uploader une image");
        System.out.println("2. 🔍 Tester le service Cloudinary");
        System.out.println("3. ↩️  Retour");
        System.out.println();
    }
    
    private void uploaderImage() {
        ConsoleUtils.afficherEnTete("Upload d'Image");
        
        try {
            System.out.print("Chemin de l'image: ");
            String chemin = ConsoleUtils.lireStringObligatoire("");
            
            File imageFile = new File(chemin);
            
            if (!imageFile.exists()) {
                ConsoleUtils.afficherErreur("❌ Fichier non trouvé: " + chemin);
                return;
            }
            
            System.out.println("📁 Fichier: " + imageFile.getName());
            System.out.println("📏 Taille: " + (imageFile.length() / 1024) + " KB");
            
            if (ConsoleUtils.demanderConfirmation("Uploader cette image vers Cloudinary?")) {
                String url = imageService.uploadImage(imageFile);
                ConsoleUtils.afficherSucces("✅ Image uploadée avec succès!");
                System.out.println("🔗 URL: " + url);
            }
            
        } catch (Exception e) {
            ConsoleUtils.afficherErreur("❌ Erreur lors de l'upload: " + e.getMessage());
        }
        
        ConsoleUtils.attendreEntree();
    }
    
    private void testerService() {
        ConsoleUtils.afficherEnTete("Test Cloudinary");
        
        boolean configOk = imageService.isConfigured();
        
        if (configOk) {
            ConsoleUtils.afficherSucces("✅ Cloudinary configuré correctement");
            System.out.println("📌 Prêt pour l'upload d'images");
        } else {
            ConsoleUtils.afficherAvertissement("⚠️  Cloudinary non configuré - Mode simulation");
            System.out.println("📌 Les images seront simulées pour l'examen");
        }
        
        ConsoleUtils.attendreEntree();
    }
}
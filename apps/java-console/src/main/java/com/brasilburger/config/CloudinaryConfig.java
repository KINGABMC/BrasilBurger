package com.brasilburger.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfig {
    
    public static Cloudinary getInstance() {
        // Crée une Map explicitement typée
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"));
        config.put("api_key", System.getenv("CLOUDINARY_API_KEY"));
        config.put("api_secret", System.getenv("CLOUDINARY_API_SECRET"));
        config.put("secure", true);
        
        return new Cloudinary(config);
    }
}
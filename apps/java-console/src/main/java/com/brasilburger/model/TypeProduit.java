package com.brasilburger.model;

/**
 * Enumération des types de produits selon le MLD
 * Correspond à : CHECK (type IN ('BURGER', 'MENU', 'COMPLEMENT'))
 */
public enum TypeProduit {
    BURGER("BURGER"),
    MENU("MENU"), 
    COMPLEMENT("COMPLEMENT");
    
    private final String valeurDB;
    
    TypeProduit(String valeurDB) {
        this.valeurDB = valeurDB;
    }
    
    /**
     * Retourne la valeur pour la base de données
     */
    public String getValeurDB() {
        return valeurDB;
    }
    
    /**
     * Convertit une valeur DB en enum
     */
    public static TypeProduit depuisValeurDB(String valeurDB) {
        if (valeurDB == null) {
            throw new IllegalArgumentException("La valeur DB ne peut pas être null");
        }
        for (TypeProduit type : values()) {
            if (type.valeurDB.equals(valeurDB.toUpperCase())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type de produit invalide: " + valeurDB);
    }
    
    /**
     * Vérifie si une valeur est valide pour la DB
     */
    public static boolean estValide(String valeurDB) {
        if (valeurDB == null) return false;
        for (TypeProduit type : values()) {
            if (type.valeurDB.equals(valeurDB.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Affiche les types disponibles (pour menus console)
     */
    public static String getTypesDisponibles() {
        StringBuilder sb = new StringBuilder();
        for (TypeProduit type : values()) {
            sb.append(type.valeurDB).append(" ");
        }
        return sb.toString().trim();
    }
}
package com.brasilburger.console;

import java.util.Scanner;

/**
 * Utilitaires pour l'interface console
 * Centralise les fonctions communes d'affichage et de saisie
 */
public class ConsoleUtils {
    private static final Scanner scanner = new Scanner(System.in);
    
    // Couleurs ANSI pour une meilleure lisibilité (optionnel)
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    
    /**
     * Affiche un en-tête avec un titre
     */
    public static void afficherEnTete(String titre) {
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println("        " + titre.toUpperCase());
        System.out.println("════════════════════════════════════════════");
    }
    
    /**
     * Affiche un sous-titre
     */
    public static void afficherSousTitre(String sousTitre) {
        System.out.println();
        System.out.println("――――――――――――――――――――――――――――――――――――――――――");
        System.out.println("  " + sousTitre);
        System.out.println("――――――――――――――――――――――――――――――――――――――――――");
    }
    
    /**
     * Affiche un message de succès
     */
    public static void afficherSucces(String message) {
        System.out.println(GREEN + "✅ " + message + RESET);
    }
    
    /**
     * Affiche un message d'erreur
     */
    public static void afficherErreur(String message) {
        System.out.println(RED + "❌ " + message + RESET);
    }
    
    /**
     * Affiche un message d'avertissement
     */
    public static void afficherAvertissement(String message) {
        System.out.println(YELLOW + "⚠️  " + message + RESET);
    }
    
    /**
     * Affiche un message d'information
     */
    public static void afficherInfo(String message) {
        System.out.println(BLUE + "ℹ️  " + message + RESET);
    }
    
    /**
     * Attend que l'utilisateur appuie sur Entrée
     */
    public static void attendreEntree() {
        System.out.println();
        System.out.print("Appuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }
    
    /**
     * Demande une confirmation à l'utilisateur
     */
    public static boolean demanderConfirmation(String message) {
        System.out.print(message + " (O/N): ");
        String reponse = scanner.nextLine().trim().toUpperCase();
        return reponse.equals("O") || reponse.equals("OUI");
    }
    
    /**
     * Lit une entrée numérique
     */
    public static int lireEntier(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                afficherErreur("Veuillez entrer un nombre valide.");
            }
        }
    }
    
    /**
     * Lit une entrée décimale
     */
    public static double lireDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                afficherErreur("Veuillez entrer un nombre valide.");
            }
        }
    }
    
    /**
     * Lit une chaîne de caractères (obligatoire)
     */
    public static String lireStringObligatoire(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            afficherErreur("Ce champ est obligatoire.");
        }
    }
    
    /**
     * Lit une chaîne de caractères (optionnelle)
     */
    public static String lireStringOptionnelle(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? null : input;
    }
    
    /**
     * Efface la console (approximation)
     */
    public static void effacerConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
    
    /**
     * Affiche une ligne séparatrice
     */
    public static void afficherSeparateur() {
        System.out.println("――――――――――――――――――――――――――――――――――――――――――");
    }
    
    /**
     * Ferme le scanner (à appeler à la fin de l'application)
     */
    public static void fermerScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
# Brasil Burger - Application Web C# MVC

Application web ASP.NET Core MVC pour la gestion des commandes Brasil Burger.

## 🎯 Compatibilité avec le projet Java
- Même base de données NeonDB PostgreSQL
- Mêmes images Cloudinary
- Mêmes produits (51 produits existants)
- Même logique métier

## 🚀 Structure du projet
BrasilBurger.Web/
├── Controllers/ # Contrôleurs MVC
├── Models/ # Entités EF Core
├── Services/ # Logique métier
├── Repositories/ # Accès aux données
├── Views/ # Vues Razor
├── ViewModels/ # Modèles de vue
├── Helpers/ # Classes utilitaires
└── wwwroot/ # Fichiers statiques


## 📦 Packages installés
- Microsoft.EntityFrameworkCore (8.0.0)
- Npgsql.EntityFrameworkCore.PostgreSQL (8.0.0)
- CloudinaryDotNet (1.27.9)
- Newtonsoft.Json (13.0.4)

## 🔗 Base de données
Connection à NeonDB PostgreSQL (même DB que l'application Java).

## 🎨 Interface
- ASP.NET Core MVC avec Razor Views
- Bootstrap 5
- Session ASP.NET pour le panier
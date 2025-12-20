using BrasilBurger.Web.Data;
using BrasilBurger.Web.Models.Entities;
using BrasilBurger.web.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Diagnostics;
using System.Text.Json;

namespace BrasilBurger.Web.Controllers
{
    public class HomeController : Controller
    {
        private readonly BrasilBurgerContext _context;

        public HomeController(BrasilBurgerContext context)
        {
            _context = context;
        }

        // ✅ INDEX - Page d'accueil avec produits
        public async Task<IActionResult> Index()
        {
            try
            {
                var burgers = await _context.produit
                    .Where(p => p.Type == "BURGER" && p.Disponible == true && !string.IsNullOrEmpty(p.UrlImage))
                    .OrderBy(p => p.Nom)
                    .Take(12)
                    .ToListAsync();

                var menus = await _context.produit
                    .Where(p => p.Type == "MENU" && p.Disponible == true && !string.IsNullOrEmpty(p.UrlImage))
                    .OrderBy(p => p.Nom)
                    .Take(12)
                    .ToListAsync();

                var complements = await _context.produit
                    .Where(p => p.Type == "COMPLEMENT" && p.Disponible == true && !string.IsNullOrEmpty(p.UrlImage))
                    .OrderBy(p => p.Nom)
                    .Take(12)
                    .ToListAsync();

                ViewBag.Burgers = burgers;
                ViewBag.Menus = menus;
                ViewBag.Complements = complements;
                ViewBag.PanierCount = GetSessionPanierCount();

                return View();
            }
            catch
            {
                return View();
            }
        }

        // ✅ DASHBOARD - Page personnalisée du client
        [HttpGet]
        public async Task<IActionResult> Dashboard()
        {
            var clientId = HttpContext.Session.GetInt32("ClientId");
            if (clientId == null)
            {
                return RedirectToAction("Login", "Client", new { returnUrl = "/Home/Dashboard" });
            }

            var client = await _context.client.FindAsync(clientId);
            if (client == null)
            {
                HttpContext.Session.Remove("ClientId");
                return RedirectToAction("Login", "Client");
            }

            // Récupérer les 5 dernières commandes
            var commandesRecentes = await _context.commande
                .Where(c => c.ClientId == clientId)
                .OrderByDescending(c => c.DateCommande)
                .Take(5)
                .ToListAsync();

            // Statistiques
            var totalCommandes = await _context.commande
                .Where(c => c.ClientId == clientId)
                .CountAsync();

            var totalDepense = await _context.commande
                .Where(c => c.ClientId == clientId && c.Statut != "ANNULE")
                .SumAsync(c => (decimal?)c.MontantTotal) ?? 0;

            ViewBag.Client = client;
            ViewBag.CommandesRecentes = commandesRecentes;
            ViewBag.TotalCommandes = totalCommandes;
            ViewBag.TotalDepense = totalDepense;
            ViewBag.PanierCount = GetSessionPanierCount();

            return View();
        }

        // ✅ HELPER - Compteur panier session OPTIMISÉ
        private int GetSessionPanierCount()
        {
            // 1. Essayer d'abord le count stocké séparément (optimisation Render)
            var countStored = HttpContext.Session.GetInt32("PanierCount");
            if (countStored.HasValue)
            {
                return countStored.Value;
            }

            // 2. Fallback: calculer depuis le panier JSON
            var panierJson = HttpContext.Session.GetString("Panier");
            if (!string.IsNullOrEmpty(panierJson))
            {
                try
                {
                    var panier = JsonSerializer.Deserialize<List<SessionPanierItem>>(panierJson);
                    var count = panier?.Sum(item => item.Quantite) ?? 0;
                    // Stocker pour la prochaine fois
                    HttpContext.Session.SetInt32("PanierCount", count);
                    return count;
                }
                catch
                {
                    return 0;
                }
            }
            return 0;
        }

        // ✅ METTRE À JOUR LE COUNT DANS LA SESSION
        private void UpdatePanierCountInSession(List<SessionPanierItem> panier)
        {
            var count = panier.Sum(p => p.Quantite);
            HttpContext.Session.SetInt32("PanierCount", count);
        }

        // ✅ AJOUTER AU PANIER
        [HttpPost]
        public IActionResult AjouterAuPanier(int produitId, int quantite = 1)
        {
            try
            {
                var produit = _context.produit.Find(produitId);
                if (produit == null) return Json(new { success = false });

                var panierJson = HttpContext.Session.GetString("Panier");
                var panier = string.IsNullOrEmpty(panierJson) 
                    ? new List<SessionPanierItem>() 
                    : JsonSerializer.Deserialize<List<SessionPanierItem>>(panierJson) ?? new List<SessionPanierItem>();

                // ⬇️ CORRECTION SIMPLE : Prix est déjà decimal
                decimal prixDecimal = produit.Prix; // Direct assignment

                var itemExist = panier.FirstOrDefault(p => p.ProduitId == produitId);
                if (itemExist != null)
                {
                    itemExist.Quantite += quantite;
                }
                else
                {
                    panier.Add(new SessionPanierItem
                    {
                        ProduitId = produit.Id,
                        Nom = produit.Nom ?? "",
                        Prix = prixDecimal, // ⬅️ Utilise directement
                        ImageUrl = produit.UrlImage ?? "",
                        Type = produit.Type ?? "",
                        Quantite = quantite
                    });
                }

                HttpContext.Session.SetString("Panier", JsonSerializer.Serialize(panier));
                UpdatePanierCountInSession(panier);
                
                return Json(new { 
                    success = true, 
                    count = panier.Sum(p => p.Quantite),
                    message = $"✅ {produit.Nom ?? "Produit"} ajouté au panier !"
                });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"ERREUR AjouterAuPanier: {ex.Message}");
                return Json(new { success = false });
            }
        }

        // ✅ API - Compteur panier (optimisé pour Render)
        [HttpGet]
        public IActionResult GetPanierCountApi()
        {
            // Retourne directement le count pré-calculé
            return Json(new { count = GetSessionPanierCount() });
        }

        // ✅ VOIR PANIER
        [HttpGet]
        public IActionResult VoirPanier()
        {
            var panierJson = HttpContext.Session.GetString("Panier");
            var panier = string.IsNullOrEmpty(panierJson) 
                ? new List<SessionPanierItem>() 
                : JsonSerializer.Deserialize<List<SessionPanierItem>>(panierJson) ?? new List<SessionPanierItem>();

            ViewBag.Panier = panier;
            ViewBag.Total = panier.Sum(p => p.Prix * p.Quantite);
            ViewBag.PanierCount = GetSessionPanierCount();

            return View();
        }

        // ✅ METTRE À JOUR PANIER
        [HttpPost]
        public IActionResult MettreAJourPanier(int produitId, int quantite)
        {
            var panierJson = HttpContext.Session.GetString("Panier");
            if (string.IsNullOrEmpty(panierJson)) return Json(new { success = false });

            var panier = JsonSerializer.Deserialize<List<SessionPanierItem>>(panierJson) ?? new List<SessionPanierItem>();
            var item = panier.FirstOrDefault(p => p.ProduitId == produitId);

            if (item != null)
            {
                if (quantite <= 0)
                {
                    panier.Remove(item);
                }
                else
                {
                    item.Quantite = quantite;
                }
            }

            HttpContext.Session.SetString("Panier", JsonSerializer.Serialize(panier));
            UpdatePanierCountInSession(panier);
            
            return Json(new { 
                success = true, 
                count = panier.Sum(p => p.Quantite),
                total = panier.Sum(p => p.Prix * p.Quantite)
            });
        }

        // ✅ VIDER PANIER (optimisé)
        [HttpPost]
        public IActionResult ViderPanier()
        {
            HttpContext.Session.Remove("Panier");
            HttpContext.Session.Remove("PanierCount");
            return Json(new { success = true, count = 0 });
        }

        // ✅ PAGE COMMANDER
        [HttpGet]
        public IActionResult Commander()
        {
            var panierJson = HttpContext.Session.GetString("Panier");
            var panier = string.IsNullOrEmpty(panierJson) 
                ? new List<SessionPanierItem>() 
                : JsonSerializer.Deserialize<List<SessionPanierItem>>(panierJson) ?? new List<SessionPanierItem>();

            if (!panier.Any())
            {
                return RedirectToAction("VoirPanier");
            }

            var clientId = HttpContext.Session.GetInt32("ClientId");
            if (clientId == null)
            {
                return RedirectToAction("Login", "Client", new { returnUrl = "/Home/Commander" });
            }

            var total = panier.Sum(p => p.Prix * p.Quantite);
            var zones = _context.zone.ToList();
            var quartiers = _context.quartier.ToList();

            ViewBag.Panier = panier;
            ViewBag.Total = total;
            ViewBag.Zones = zones;
            ViewBag.Quartiers = quartiers;

            return View();
        }

        // ✅ PASSER COMMANDE
        [HttpPost]
        public async Task<IActionResult> PasserCommande(
            [FromForm] string modeLivraison, 
            [FromForm] string modePaiement,
            [FromForm] string telephone = "",
            [FromForm] string quartierId = "",
            [FromForm] string rue = "",
            [FromForm] string details = "",
            [FromForm] string notes = "")
        {
            try
            {
                // ⬇️ DEBUG LOG - DÉBUT
                Console.WriteLine("=== DEBUG PASSER COMMANDE ===");
                Console.WriteLine($"Heure: {DateTime.Now:HH:mm:ss}");
                Console.WriteLine($"Paramètres reçus:");
                Console.WriteLine($"  modeLivraison: '{modeLivraison}'");
                Console.WriteLine($"  modePaiement: '{modePaiement}'");
                Console.WriteLine($"  telephone: '{telephone}'");
                Console.WriteLine($"  quartierId: '{quartierId}'");
                Console.WriteLine($"  rue: '{rue}'");
                Console.WriteLine($"  details: '{details}'");
                Console.WriteLine($"  notes: '{notes}'");

                var clientIdNullable = HttpContext.Session.GetInt32("ClientId");
                Console.WriteLine($"ClientId en session: '{clientIdNullable}'");
                
                if (!clientIdNullable.HasValue)
                {
                    Console.WriteLine("❌ ERREUR: ClientId manquant");
                    return Json(new { success = false, message = "Veuillez vous connecter pour commander" });
                }

                int clientId = clientIdNullable.Value;
                Console.WriteLine($"ClientId: {clientId}");

                var panierJson = HttpContext.Session.GetString("Panier");
                Console.WriteLine($"Panier JSON existe: {!string.IsNullOrEmpty(panierJson)}");
                
                if (string.IsNullOrEmpty(panierJson))
                {
                    Console.WriteLine("❌ ERREUR: Panier vide (JSON manquant)");
                    return Json(new { success = false, message = "Panier vide" });
                }

                Console.WriteLine($"Longueur JSON: {panierJson.Length} caractères");
                Console.WriteLine($"JSON (premiers 200 chars): {panierJson.Substring(0, Math.Min(200, panierJson.Length))}...");
                
                List<SessionPanierItem> panier;
                try
                {
                    panier = JsonSerializer.Deserialize<List<SessionPanierItem>>(panierJson) ?? new List<SessionPanierItem>();
                    Console.WriteLine($"✅ Panier désérialisé: {panier.Count} items");
                    
                    if (panier.Any())
                    {
                        Console.WriteLine("Détail panier:");
                        foreach (var item in panier)
                        {
                            Console.WriteLine($"  - {item.Nom} (ID:{item.ProduitId}): {item.Quantite} × {item.Prix} = {item.SousTotal}");
                            Console.WriteLine($"    Type Prix: {item.Prix.GetType()}, SousTotal: {item.SousTotal.GetType()}");
                        }
                    }
                }
                catch (JsonException jex)
                {
                    Console.WriteLine($"❌ ERREUR JSON: {jex.Message}");
                    Console.WriteLine($"JSON corrompu, nettoyage session...");
                    HttpContext.Session.Remove("Panier");
                    HttpContext.Session.Remove("PanierCount");
                    return Json(new { 
                        success = false, 
                        message = "Erreur dans votre panier. Veuillez réajouter vos articles." 
                    });
                }
                
                if (!panier.Any())
                {
                    Console.WriteLine("❌ ERREUR: Panier vide (0 items)");
                    return Json(new { success = false, message = "Panier vide" });
                }
                // ⬆️ DEBUG LOG - FIN

                Console.WriteLine($"ClientId: {clientId}");
                
                var client = await _context.client.FindAsync(clientId);
                if (client == null)
                {
                    Console.WriteLine($"❌ ERREUR: Client {clientId} non trouvé en DB");
                    HttpContext.Session.Remove("ClientId");
                    return Json(new { success = false, message = "Client non trouvé" });
                }
                Console.WriteLine($"✅ Client trouvé: {client.Nom} {client.Prenom}");

                var montantProduits = panier.Sum(p => p.Prix * p.Quantite);
                Console.WriteLine($"Montant produits: {montantProduits}");
                
                decimal fraisLivraison = 0;
                int? adresseLivraisonId = null;

                if (modeLivraison == "LIVRAISON_DOMICILE")
                {
                    Console.WriteLine("Mode: LIVRAISON_DOMICILE détecté");
                    
                    if (string.IsNullOrEmpty(rue))
                    {
                        Console.WriteLine("❌ ERREUR: Rue manquante pour livraison");
                        return Json(new { success = false, message = "Veuillez entrer une adresse de livraison" });
                    }

                    int? quartierIdParsed = null;
                    if (!string.IsNullOrWhiteSpace(quartierId) && int.TryParse(quartierId, out int parsedId))
                    {
                        quartierIdParsed = parsedId;
                        Console.WriteLine($"Quartier ID parsé: {quartierIdParsed}");
                    }
                    else
                    {
                        Console.WriteLine($"Quartier ID non parsé ou vide: '{quartierId}'");
                    }

                    if (quartierIdParsed.HasValue)
                    {
                        var quartier = await _context.quartier.FindAsync(quartierIdParsed.Value);
                        fraisLivraison = quartier?.PrixLivraisonBase ?? 2000;
                        Console.WriteLine($"Frais livraison (quartier): {fraisLivraison}");
                    }
                    else
                    {
                        fraisLivraison = 2000;
                        Console.WriteLine($"Frais livraison (défaut): {fraisLivraison}");
                    }

                    var adresse = new AdresseLivraison
                    {
                        ClientId = clientId,
                        QuartierId = quartierIdParsed ?? 0,
                        Rue = rue,
                        Details = details ?? "",
                        CreatedAt = DateTime.UtcNow
                    };

                    Console.WriteLine($"Création adresse: Rue='{rue}', Details='{details}'");
                    
                    _context.adresselivraison.Add(adresse);
                    await _context.SaveChangesAsync();
                    adresseLivraisonId = adresse.Id;
                    Console.WriteLine($"✅ Adresse créée ID: {adresseLivraisonId}");
                }
                else
                {
                    Console.WriteLine($"Mode: {modeLivraison} - Pas de livraison");
                }

                var montantTotal = montantProduits + fraisLivraison;
                Console.WriteLine($"Montant total: {montantTotal} (produits: {montantProduits} + livraison: {fraisLivraison})");
                
                string modeConsommationDB = modeLivraison switch
                {
                    "SUR_PLACE" => "SUR_PLACE",
                    "A_EMPORTER" => "EMPORTER",
                    "LIVRAISON_DOMICILE" => "LIVRAISON",
                    _ => "SUR_PLACE"
                };
                Console.WriteLine($"ModeConsommation DB: {modeConsommationDB}");

                string telephoneLivraison = string.IsNullOrWhiteSpace(telephone) 
                    ? client.Telephone ?? "" 
                    : telephone;

                if (!string.IsNullOrWhiteSpace(telephone) && telephone != (client.Telephone ?? ""))
                {
                    notes += $" (Tél livraison: {telephone})";
                }

                Console.WriteLine($"Création commande...");
                var commande = new Commande
                {
                    ClientId = clientId,
                    Numero = $"CMD{DateTime.Now:yyyyMMddHHmmss}{new Random().Next(1000, 9999)}",
                    ModeConsommation = modeConsommationDB,
                    AdresseLivraisonId = adresseLivraisonId,
                    MontantProduits = montantProduits,
                    FraisLivraison = fraisLivraison,
                    MontantTotal = montantTotal,
                    Statut = "EN_ATTENTE",
                    DateCommande = DateTime.UtcNow,
                    Notes = notes ?? "",
                    CreatedAt = DateTime.UtcNow,
                    CodeSuivi = $"SUIVI{DateTime.Now:yyyyMMddHHmmss}{new Random().Next(1000, 9999)}"
                };

                Console.WriteLine($"Commande à créer:");
                Console.WriteLine($"  Numéro: {commande.Numero}");
                Console.WriteLine($"  MontantProduits: {commande.MontantProduits}");
                Console.WriteLine($"  FraisLivraison: {commande.FraisLivraison}");
                Console.WriteLine($"  MontantTotal: {commande.MontantTotal}");
                Console.WriteLine($"  Type MontantTotal: {commande.MontantTotal.GetType()}");

                _context.commande.Add(commande);
                Console.WriteLine("Sauvegarde commande...");
                await _context.SaveChangesAsync();
                Console.WriteLine($"✅ Commande créée ID: {commande.Id}");

                Console.WriteLine($"Ajout {panier.Count} lignes commande...");
                foreach (var item in panier)
                {
                    var ligne = new LigneCommande
                    {
                        CommandeId = commande.Id,
                        ProduitId = item.ProduitId,
                        Quantite = item.Quantite,
                        PrixUnitaire = item.Prix,
                        SousTotal = item.Prix * item.Quantite,
                        CreatedAt = DateTime.UtcNow
                    };
                    Console.WriteLine($"  Ligne: Produit {item.ProduitId}, Qty {item.Quantite}, Prix {item.Prix}");
                    _context.lignecommande.Add(ligne);
                }

                Console.WriteLine($"Création paiement: {modePaiement}");

                // ⬇️ CORRECTION ICI : Validation des valeurs selon les contraintes CHECK
                // Contrainte methode_check: 'ESPECES', 'INTERNE', 'WAVE', 'ORANGE_MONEY'
                // Contrainte statut_paiement_check: 'PAYE', 'ECHEC'

                string methodeValide = modePaiement switch
                {
                    "ESPECES" => "ESPECES",
                    "WAVE" => "WAVE",
                    "ORANGE_MONEY" => "ORANGE_MONEY",
                    _ => "ESPECES"
                };

                string statutValide = modePaiement == "ESPECES" ? "ECHEC" : "PAYE";

                var paiement = new Paiement
                {
                    CommandeId = commande.Id,
                    Methode = methodeValide,
                    Montant = montantTotal,
                    StatutPaiement = statutValide,
                    DatePaiement = modePaiement == "ESPECES" ? (DateTime?)null : DateTime.UtcNow,
                    Reference = modePaiement == "ESPECES" ? null : $"REF{DateTime.Now:yyyyMMddHHmmss}",
                    CreatedAt = DateTime.UtcNow
                };

                _context.paiement.Add(paiement);
                await _context.SaveChangesAsync();
                Console.WriteLine($"✅ Paiement créé");

                // Vider le panier APRÈS validation de la commande
                Console.WriteLine("Vidage panier session...");
                HttpContext.Session.Remove("Panier");
                HttpContext.Session.Remove("PanierCount");

                Console.WriteLine($"✅ COMMANDE RÉUSSIE: {commande.Numero}");
                Console.WriteLine("=== FIN PASSER COMMANDE ===");
                
                return Json(new { 
                    success = true, 
                    message = "Commande passée avec succès !",
                    commandeId = commande.Id,
                    commandeNumero = commande.Numero,
                    total = montantTotal
                });
            }
            catch (Exception ex)
            {
                Console.WriteLine("=== ERREUR CRITIQUE PASSER COMMANDE ===");
                Console.WriteLine($"Type: {ex.GetType().FullName}");
                Console.WriteLine($"Message: {ex.Message}");
                Console.WriteLine($"StackTrace: {ex.StackTrace}");
                
                if (ex.InnerException != null)
                {
                    Console.WriteLine($"Inner Exception:");
                    Console.WriteLine($"  Type: {ex.InnerException.GetType().FullName}");
                    Console.WriteLine($"  Message: {ex.InnerException.Message}");
                }
                
                Console.WriteLine("=== FIN ERREUR ===");
                
                return Json(new { 
                    success = false, 
                    message = $"Erreur: {ex.Message}" 
                });
            }
        }
         
        // ✅ DÉTAILS D'UNE COMMANDE
[HttpGet]
public async Task<IActionResult> DetailsCommande(int id)
{
    var clientId = HttpContext.Session.GetInt32("ClientId");
    if (clientId == null)
    {
        return RedirectToAction("Login", "Client", new { returnUrl = $"/Home/DetailsCommande/{id}" });
    }

    // Récupère la commande avec tous les détails
    var commande = await _context.commande
        .Include(c => c.Client)
        .Include(c => c.AdresseLivraison)
            .ThenInclude(a => a.Quartier)
        .Include(c => c.LignesCommande)
            .ThenInclude(l => l.Produit)
        .Include(c => c.Paiement)
        .Include(c => c.Livraison)
            .ThenInclude(l => l.Livreur)
        .FirstOrDefaultAsync(c => c.Id == id && c.ClientId == clientId);

    if (commande == null)
    {
        TempData["ErrorMessage"] = "Commande non trouvée ou vous n'avez pas l'autorisation de la consulter.";
        return RedirectToAction("MesCommandes");
    }

    ViewBag.PanierCount = GetSessionPanierCount();
    return View(commande);
}




        // ✅ SUIVI COMMANDE
        public async Task<IActionResult> SuiviCommande(int id)
        {
            var clientId = HttpContext.Session.GetInt32("ClientId");
            if (clientId == null)
            {
                return RedirectToAction("Login", "Client",
                    new { returnUrl = $"/Home/SuiviCommande/{id}" });
            }

            var commande = await _context.commande
                .Include(c => c.LignesCommande)
                    .ThenInclude(l => l.Produit)
                .Include(c => c.AdresseLivraison)
                    .ThenInclude(a => a.Quartier)
                .Include(c => c.Paiement)
                .Include(c => c.Livraison)
                    .ThenInclude(l => l.Livreur)
                .FirstOrDefaultAsync(c => c.Id == id && c.ClientId == clientId);

            if (commande == null)
            {
                return NotFound(); 
            }

            ViewBag.PanierCount = GetSessionPanierCount();

            return View(commande);
        }

        // ✅ MES COMMANDES
        public async Task<IActionResult> MesCommandes()
        {
            var clientId = HttpContext.Session.GetInt32("ClientId");
            if (clientId == null)
            {
                return RedirectToAction("Login", "Client", new { returnUrl = "/Home/MesCommandes" });
            }

            var commandes = await _context.commande
                .Where(c => c.ClientId == clientId)
                .OrderByDescending(c => c.DateCommande)
                .ToListAsync();

            ViewBag.PanierCount = GetSessionPanierCount();

            return View(commandes);
        }

        // ✅ PAGES STATIQUES
        public IActionResult About() => View();
        public IActionResult Contact() => View();
        public IActionResult Privacy() => View();

        // ✅ PAGE ERREUR (obligatoire pour Render)
        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }

    // ✅ MODÈLE SESSION PANIER (doit rester dans le même namespace)
    public class SessionPanierItem
    {
        public int ProduitId { get; set; }
        public string Nom { get; set; } = "";
        public decimal Prix { get; set; }
        public string ImageUrl { get; set; } = "";
        public string Type { get; set; } = "";
        public int Quantite { get; set; }
        public decimal SousTotal => Prix * Quantite;
    }
}
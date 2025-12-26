using BrasilBurger.Web.Data;
using BrasilBurger.Web.Models.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BrasilBurger.Web.Controllers
{
    public class ClientController : Controller
    {
        private readonly BrasilBurgerContext _context;
        private readonly ILogger<ClientController> _logger;

        public ClientController(BrasilBurgerContext context, ILogger<ClientController> logger)
        {
            _context = context;
            _logger = logger;
        }

        public IActionResult Login(string? returnUrl = null)
        {
            ViewData["ReturnUrl"] = returnUrl;
            return View();
        }

       [HttpPost]
[ValidateAntiForgeryToken]
public async Task<IActionResult> Login(string email, string motDePasse, string? returnUrl = null)
{
    try
    {
        var client = await _context.client
            .FirstOrDefaultAsync(c => c.Email == email && c.MotDePasse == motDePasse && (c.EstActif == true));

        if (client == null)
        {
            ModelState.AddModelError("", "Email ou mot de passe incorrect.");
            return View();
        }

        // ✅ STOCKER SÉPARÉMENT
        HttpContext.Session.SetInt32("ClientId", client.Id);
        HttpContext.Session.SetString("ClientPrenom", client.Prenom ?? "");  // Ajoute cette ligne
        HttpContext.Session.SetString("ClientNom", client.Nom ?? "");       // Modifie cette ligne
        HttpContext.Session.SetString("ClientEmail", client.Email ?? "");   // Optionnel mais utile

        _logger.LogInformation("Client {ClientId} connecté.", client.Id);
        
        if (!string.IsNullOrEmpty(returnUrl) && Url.IsLocalUrl(returnUrl))
        {
            return Redirect(returnUrl);
        }
        return RedirectToAction("Dashboard", "Home"); // ← Redirige vers Dashboard, pas Index
    }
    catch (Exception ex)
    {
        _logger.LogError(ex, "Erreur lors de la connexion.");
        ModelState.AddModelError("", "Une erreur est survenue.");
        return View();
    }
}

        public IActionResult Register()
        {
            return View();
        }

        [HttpPost]
[ValidateAntiForgeryToken]
public async Task<IActionResult> Register(Client client)
{
    try
    {
        if (await _context.client.AnyAsync(c => c.Email == client.Email))
        {
            ModelState.AddModelError("Email", "Cet email est déjà utilisé.");
            return View(client);
        }

        client.DateInscription = DateTime.UtcNow;
        client.EstActif = true;
        client.CreatedAt = DateTime.UtcNow;

        _context.client.Add(client);
        await _context.SaveChangesAsync();

        // ✅ MÊME STRUCTURE QUE LE LOGIN
        HttpContext.Session.SetInt32("ClientId", client.Id);
        HttpContext.Session.SetString("ClientPrenom", client.Prenom ?? "");  // Ajoute
        HttpContext.Session.SetString("ClientNom", client.Nom ?? "");       // Modifie
        HttpContext.Session.SetString("ClientEmail", client.Email ?? "");   // Optionnel

        _logger.LogInformation("Nouveau client inscrit: {ClientId}", client.Id);
        
        return RedirectToAction("Dashboard", "Home"); // ← Dashboard, pas Commander
    }
    catch (Exception ex)
    {
        _logger.LogError(ex, "Erreur lors de l'inscription.");
        ModelState.AddModelError("", "Une erreur est survenue lors de l'inscription.");
        return View(client);
    }
}

        [HttpGet]
        public IActionResult Logout()
        {
            // Nettoyer TOUTE la session
            HttpContext.Session.Clear();
            
            // Supprimer le cookie de session
            Response.Cookies.Delete(".AspNetCore.Session");
            
            return RedirectToAction("Index", "Home");
        }

        public async Task<IActionResult> Profil()
        {
            var clientId = HttpContext.Session.GetInt32("ClientId");
            if (clientId == null)
            {
                return RedirectToAction("Login", new { returnUrl = "/Client/Profil" });
            }

            var client = await _context.client.FindAsync(clientId);
            if (client == null)
            {
                HttpContext.Session.Clear();
                return RedirectToAction("Login");
            }

            return View(client);
        }
    }
}
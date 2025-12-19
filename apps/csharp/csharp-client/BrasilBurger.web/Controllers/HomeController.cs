using BrasilBurger.Web.Data;
using BrasilBurger.Web.Models.Entities;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BrasilBurger.Web.Controllers
{
    public class HomeController : Controller
    {
        private readonly BrasilBurgerContext _context;
        private readonly ILogger<HomeController> _logger;

        public HomeController(BrasilBurgerContext context, ILogger<HomeController> logger)
        {
            _context = context;
            _logger = logger;
        }

        public async Task<IActionResult> Index()
        {
            try
            {
                // UTILISE LES NOMS EN MINUSCULES comme dans ton DbContext !
                var produitCount = await _context.produit.CountAsync();
                var clientCount = await _context.client.CountAsync();
                
                ViewBag.ProduitCount = produitCount;
                ViewBag.ClientCount = clientCount;
                ViewBag.DatabaseStatus = "Connected ✅";
                
                _logger.LogInformation($"Home page loaded - Products: {produitCount}, Clients: {clientCount}");
            }
            catch (Exception ex)
            {
                ViewBag.DatabaseStatus = "Disconnected ❌";
                ViewBag.ErrorMessage = ex.Message;
                _logger.LogError(ex, "Database connection failed");
            }
            
            return View(); // ← Doit rendre la vue Index.cshtml
        }

        public async Task<IActionResult> TestDB()
        {
            try
            {
                await _context.Database.OpenConnectionAsync();
                var canConnect = await _context.Database.CanConnectAsync();
                var productCount = await _context.produit.CountAsync();
                
                return Content($"OpenConnection: OK\nCanConnect: {canConnect}\nProducts: {productCount}");
            }
            catch (Exception ex)
            {
                return Content($"ERREUR: {ex.ToString()}");
            }
        }
    }
}
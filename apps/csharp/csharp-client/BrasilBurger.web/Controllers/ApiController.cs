// Controllers/ApiController.cs
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using BrasilBurger.Web.Data;

namespace BrasilBurger.Web.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class QuartiersController : ControllerBase
    {
        private readonly BrasilBurgerContext _context;

        public QuartiersController(BrasilBurgerContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<IActionResult> GetQuartiersByZone(int zoneId)
        {
            var quartiers = await _context.quartier
                .Where(q => q.ZoneId == zoneId)
                .Select(q => new {
                    q.Id,
                    q.Nom,
                    q.PrixLivraisonBase,
                    q.TempsEstimeMinutes
                })
                .ToListAsync();

            return Ok(quartiers);
        }
    }
}
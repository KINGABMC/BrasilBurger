namespace BrasilBurger.Web.Models.Entities
{
    public class Quartier
    {
        public int Id { get; set; }
        public int ZoneId { get; set; }
        public string Nom { get; set; } = string.Empty;
        public decimal PrixLivraisonBase { get; set; }
        public int TempsEstimeMinutes { get; set; } = 30;
        public DateTime? CreatedAt { get; set; }
        
        // Navigation properties
        public Zone? Zone { get; set; }
        public ICollection<AdresseLivraison> Adresses { get; set; } = new List<AdresseLivraison>();
    }
}
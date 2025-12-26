namespace BrasilBurger.Web.Models.Entities
{
    public class Avis
    {
        public int Id { get; set; }
        public int ClientId { get; set; }
        public int ProduitId { get; set; }
        public int Note { get; set; }
        public string? Commentaire { get; set; }
        public DateTime? DateAvis { get; set; }
        public DateTime? CreatedAt { get; set; }
        
        // Navigation properties
        public Client? Client { get; set; }
        public Produit? Produit { get; set; }
    }
}
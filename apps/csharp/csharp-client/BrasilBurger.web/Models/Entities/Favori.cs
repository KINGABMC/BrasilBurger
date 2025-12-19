namespace BrasilBurger.Web.Models.Entities
{
    public class Favori
    {
        public int Id { get; set; }
        public int ClientId { get; set; }
        public int ProduitId { get; set; }
        public DateTime? CreatedAt { get; set; }
        
        // Navigation properties
        public Client? Client { get; set; }
        public Produit? Produit { get; set; }
    }
}
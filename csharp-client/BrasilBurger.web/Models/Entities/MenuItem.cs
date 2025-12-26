namespace BrasilBurger.Web.Models.Entities
{
    public class MenuItem
    {
        public int Id { get; set; }
        public int MenuId { get; set; }
        public int ProduitId { get; set; }
        public int Quantite { get; set; } = 1;
        public int Ordre { get; set; } = 1;
        public DateTime? CreatedAt { get; set; }
        
        // Navigation properties
        public Produit? Produit { get; set; }
    }
}
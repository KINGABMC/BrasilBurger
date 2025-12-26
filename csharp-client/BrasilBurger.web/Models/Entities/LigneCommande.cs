namespace BrasilBurger.Web.Models.Entities
{
    public class LigneCommande
    {
        public int Id { get; set; }
        public int CommandeId { get; set; }
        public int ProduitId { get; set; }
        public int Quantite { get; set; }
        public decimal PrixUnitaire { get; set; }
        public decimal SousTotal { get; set; }
        public string? Instructions { get; set; }
        public DateTime? CreatedAt { get; set; }
        
        // Navigation properties
        public Commande? Commande { get; set; }
        public Produit? Produit { get; set; }
    }
}
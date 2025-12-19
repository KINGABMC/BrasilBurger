namespace BrasilBurger.Web.Models.Entities
{
    public class Commande
    {
        public int Id { get; set; }
        public string Numero { get; set; } = string.Empty;
        public DateTime? DateCommande { get; set; }
        public string Statut { get; set; } = "EN_ATTENTE";
        public string ModeConsommation { get; set; } = "SUR_PLACE";
        public decimal MontantProduits { get; set; }
        public decimal FraisLivraison { get; set; }
        public decimal MontantTotal { get; set; }
        public int? AdresseLivraisonId { get; set; }
        public string Notes { get; set; } = string.Empty;
        public string CodeSuivi { get; set; } = string.Empty;
        public int? TempsEstimationLivraison { get; set; }
        public DateTime? CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        public int ClientId { get; set; }
        
        // Navigation properties
        public Client? Client { get; set; }
        public AdresseLivraison? AdresseLivraison { get; set; }
        public ICollection<LigneCommande> LignesCommande { get; set; } = new List<LigneCommande>();
        public Paiement? Paiement { get; set; }
        public Livraison? Livraison { get; set; }
    }
}
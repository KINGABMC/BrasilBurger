namespace BrasilBurger.Web.Models.Entities
{
    public class Paiement
    {
        public int Id { get; set; }
        public int CommandeId { get; set; }
        public DateTime? DatePaiement { get; set; }
        public decimal Montant { get; set; }
        public string Methode { get; set; } = "ESPECES";
        public string StatutPaiement { get; set; } = "PAYE";
        public string? Reference { get; set; }
        public DateTime? CreatedAt { get; set; }
        
        // Navigation properties
        public Commande? Commande { get; set; }
    }
}
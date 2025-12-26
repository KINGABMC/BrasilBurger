namespace BrasilBurger.Web.Models.Entities
{
    public class Livraison
    {
        public int Id { get; set; }
        public int CommandeId { get; set; }
        public int LivreurId { get; set; }
        public DateTime? DateDebut { get; set; }
        public DateTime? DateFin { get; set; }
        public string Statut { get; set; } = "ATTENTE_AFFECTATION";
        public string? Notes { get; set; }
        public DateTime? CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        
        // Navigation properties
        public Commande? Commande { get; set; }
        public Livreur? Livreur { get; set; }
    }
}
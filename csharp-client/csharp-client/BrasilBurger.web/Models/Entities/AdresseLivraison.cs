namespace BrasilBurger.Web.Models.Entities
{
    public class AdresseLivraison
    {
        public int Id { get; set; }
        public int ClientId { get; set; }
        public int QuartierId { get; set; }
        public string Rue { get; set; } = string.Empty;
        public string Numero { get; set; } = string.Empty;
        public string? Details { get; set; }
        public DateTime? CreatedAt { get; set; }
        
        // Navigation properties
        public Client? Client { get; set; }
        public Quartier? Quartier { get; set; }
    }
}
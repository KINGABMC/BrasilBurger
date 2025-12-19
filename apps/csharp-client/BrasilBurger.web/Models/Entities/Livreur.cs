namespace BrasilBurger.Web.Models.Entities
{
    public class Livreur
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public string Telephone { get; set; } = string.Empty;
        public bool? EstDisponible { get; set; } = true;
        public DateTime? CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        
        // Navigation properties
        public ICollection<Livraison> Livraisons { get; set; } = new List<Livraison>();
    }
}
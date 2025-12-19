namespace BrasilBurger.Web.Models.Entities
{
    public class Zone
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public DateTime? CreatedAt { get; set; }
        
        // Navigation properties
        public ICollection<Quartier> Quartiers { get; set; } = new List<Quartier>();
    }
}
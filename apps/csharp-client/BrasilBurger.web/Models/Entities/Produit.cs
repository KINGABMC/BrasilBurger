namespace BrasilBurger.Web.Models.Entities
{
    public class Produit
    {
        public int Id { get; set; }
        public string Nom { get; set; } = string.Empty;
        public string Type { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public decimal Prix { get; set; }
        public string UrlImage { get; set; } = string.Empty;
        public bool? Disponible { get; set; } = true;
        public DateTime? DateCreation { get; set; }
        public bool? EstArchive { get; set; } = false;
        public DateTime? CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        
        // Navigation properties
        public ICollection<MenuItem> MenuItems { get; set; } = new List<MenuItem>();
        public ICollection<LigneCommande> LignesCommande { get; set; } = new List<LigneCommande>();
        public ICollection<Favori> Favoris { get; set; } = new List<Favori>();
        public ICollection<Avis> Avis { get; set; } = new List<Avis>();
    }
}
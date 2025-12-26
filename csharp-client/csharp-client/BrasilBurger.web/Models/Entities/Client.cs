using System.ComponentModel.DataAnnotations;

namespace BrasilBurger.Web.Models.Entities
{
    public class Client
    {
        public int Id { get; set; }
        
        [Required]
        public string Nom { get; set; } = string.Empty;
        
        [Required]
        public string Prenom { get; set; } = string.Empty;
        
        [Required]
        public string Telephone { get; set; } = string.Empty;
        
        [Required]
        [EmailAddress]
        public string Email { get; set; } = string.Empty;
        
        [Required]
        public string MotDePasse { get; set; } = string.Empty;
        
        public DateTime? DateInscription { get; set; }
        public bool? EstActif { get; set; } = true;
        public DateTime? CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
        
        // Navigation properties
        public ICollection<AdresseLivraison> Adresses { get; set; } = new List<AdresseLivraison>();
        public ICollection<Commande> Commandes { get; set; } = new List<Commande>();
        public ICollection<Favori> Favoris { get; set; } = new List<Favori>();
        public ICollection<Avis> Avis { get; set; } = new List<Avis>();
    }
}
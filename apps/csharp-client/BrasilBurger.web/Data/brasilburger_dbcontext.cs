using Microsoft.EntityFrameworkCore;
using BrasilBurger.Web.Models.Entities;

namespace BrasilBurger.Web.Data
{
    public class BrasilBurgerContext : DbContext
    {
        public BrasilBurgerContext(DbContextOptions<BrasilBurgerContext> options)
            : base(options)
        {
        }

        // DbSets - NOTE: les noms de table sont en minuscules et sans underscore
        public DbSet<Client> client { get; set; }
        public DbSet<Zone> zone { get; set; }
        public DbSet<Quartier> quartier { get; set; }
        public DbSet<AdresseLivraison> adresselivraison { get; set; }
        public DbSet<Produit> produit { get; set; }
        public DbSet<MenuItem> menuitem { get; set; }
        public DbSet<Commande> commande { get; set; }
        public DbSet<LigneCommande> lignecommande { get; set; }
        public DbSet<Paiement> paiement { get; set; }
        public DbSet<Favori> favori { get; set; }
        public DbSet<Avis> avis { get; set; }
        public DbSet<Livreur> livreur { get; set; }
        public DbSet<Livraison> livraison { get; set; }
        public DbSet<Gestionnaire> gestionnaire { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
            
            // Configuration Client
            modelBuilder.Entity<Client>(entity =>
            {
                entity.ToTable("client");
                entity.HasKey(e => e.Id);
                
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.Nom).HasColumnName("nom");
                entity.Property(e => e.Prenom).HasColumnName("prenom");
                entity.Property(e => e.Email).HasColumnName("email");
                entity.Property(e => e.Telephone).HasColumnName("telephone");
                entity.Property(e => e.MotDePasse).HasColumnName("mot_de_passe");
                entity.Property(e => e.EstActif).HasColumnName("est_actif");
                entity.Property(e => e.DateInscription).HasColumnName("date_inscription");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                entity.Property(e => e.UpdatedAt).HasColumnName("updated_at");
                
                entity.HasIndex(e => e.Email).IsUnique();
                entity.HasIndex(e => e.Telephone).IsUnique();
            });

            // Configuration Zone
            modelBuilder.Entity<Zone>(entity =>
            {
                entity.ToTable("zone");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.Nom).HasColumnName("nom");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                
                entity.HasIndex(e => e.Nom).IsUnique();
            });

            // Configuration Quartier
            modelBuilder.Entity<Quartier>(entity =>
            {
                entity.ToTable("quartier");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.Nom).HasColumnName("nom");
                entity.Property(e => e.ZoneId).HasColumnName("zone_id");
                entity.Property(e => e.PrixLivraisonBase).HasColumnName("prix_livraison_base");
                entity.Property(e => e.TempsEstimeMinutes).HasColumnName("temps_estime_minutes");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                
                entity.HasOne(q => q.Zone)
                      .WithMany(z => z.Quartiers)
                      .HasForeignKey(q => q.ZoneId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            // Configuration AdresseLivraison
            modelBuilder.Entity<AdresseLivraison>(entity =>
            {
                entity.ToTable("adresselivraison");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.ClientId).HasColumnName("client_id");
                entity.Property(e => e.Rue).HasColumnName("rue");
                entity.Property(e => e.Numero).HasColumnName("numero");
                entity.Property(e => e.Details).HasColumnName("details");
                entity.Property(e => e.QuartierId).HasColumnName("quartier_id");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                
                entity.HasOne(a => a.Client)
                      .WithMany(c => c.Adresses)
                      .HasForeignKey(a => a.ClientId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasOne(a => a.Quartier)
                      .WithMany(q => q.Adresses)
                      .HasForeignKey(a => a.QuartierId)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            // Configuration Produit
            modelBuilder.Entity<Produit>(entity =>
            {
                entity.ToTable("produit");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.Nom).HasColumnName("nom");
                entity.Property(e => e.Description).HasColumnName("description");
                entity.Property(e => e.Prix).HasColumnName("prix");
                entity.Property(e => e.Type).HasColumnName("type");
                entity.Property(e => e.EstArchive).HasColumnName("est_archive");
                entity.Property(e => e.Disponible).HasColumnName("disponible");
                entity.Property(e => e.UrlImage).HasColumnName("url_image");
                entity.Property(e => e.DateCreation).HasColumnName("date_creation");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                entity.Property(e => e.UpdatedAt).HasColumnName("updated_at");
                
                entity.Property(e => e.Prix).HasPrecision(10, 2);
            });

            // Configuration MenuItem
            modelBuilder.Entity<MenuItem>(entity =>
            {
                entity.ToTable("menuitem");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.MenuId).HasColumnName("menu_id");
                entity.Property(e => e.ProduitId).HasColumnName("produit_id");
                entity.Property(e => e.Quantite).HasColumnName("quantite");
                entity.Property(e => e.Ordre).HasColumnName("ordre");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                
                // NOTE: Tu n'as pas de table 'menu' dans ta DB, donc tu dois ajuster ce mapping
                // Pour l'instant je commente cette relation
                // entity.HasOne(mi => mi.Menu)
                //       .WithMany(m => m.MenuItems)
                //       .HasForeignKey(mi => mi.MenuId)
                //       .OnDelete(DeleteBehavior.Cascade);
                
                entity.HasOne(mi => mi.Produit)
                      .WithMany(p => p.MenuItems)
                      .HasForeignKey(mi => mi.ProduitId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            // Configuration Commande
            modelBuilder.Entity<Commande>(entity =>
            {
                entity.ToTable("commande");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.Numero).HasColumnName("numero");
                entity.Property(e => e.CodeSuivi).HasColumnName("code_suivi");
                entity.Property(e => e.Statut).HasColumnName("statut");
                entity.Property(e => e.ClientId).HasColumnName("client_id");
                entity.Property(e => e.ModeConsommation).HasColumnName("mode_consommation");
                entity.Property(e => e.AdresseLivraisonId).HasColumnName("adresse_livraison_id");
                entity.Property(e => e.MontantProduits).HasColumnName("montant_produits");
                entity.Property(e => e.FraisLivraison).HasColumnName("frais_livraison");
                entity.Property(e => e.MontantTotal).HasColumnName("montant_total");
                entity.Property(e => e.TempsEstimationLivraison).HasColumnName("temps_estimation_livraison");
                entity.Property(e => e.Notes).HasColumnName("notes");
                entity.Property(e => e.DateCommande).HasColumnName("date_commande");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                entity.Property(e => e.UpdatedAt).HasColumnName("updated_at");
                
                entity.HasOne(c => c.Client)
                      .WithMany(cl => cl.Commandes)
                      .HasForeignKey(c => c.ClientId)
                      .OnDelete(DeleteBehavior.Restrict);
                
                entity.HasOne(c => c.AdresseLivraison)
                      .WithMany()
                      .HasForeignKey(c => c.AdresseLivraisonId)
                      .OnDelete(DeleteBehavior.SetNull);

                entity.Property(e => e.MontantProduits).HasPrecision(10, 2);
                entity.Property(e => e.FraisLivraison).HasPrecision(10, 2);
                entity.Property(e => e.MontantTotal).HasPrecision(10, 2);
            });

            // Configuration LigneCommande
            modelBuilder.Entity<LigneCommande>(entity =>
            {
                entity.ToTable("lignecommande");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.CommandeId).HasColumnName("commande_id");
                entity.Property(e => e.ProduitId).HasColumnName("produit_id");
                entity.Property(e => e.Quantite).HasColumnName("quantite");
                entity.Property(e => e.PrixUnitaire).HasColumnName("prix_unitaire");
                entity.Property(e => e.SousTotal).HasColumnName("sous_total");
                entity.Property(e => e.Instructions).HasColumnName("instructions");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                
                entity.HasOne(lc => lc.Commande)
                      .WithMany(c => c.LignesCommande)
                      .HasForeignKey(lc => lc.CommandeId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasOne(lc => lc.Produit)
                      .WithMany(p => p.LignesCommande)
                      .HasForeignKey(lc => lc.ProduitId)
                      .OnDelete(DeleteBehavior.Restrict);
                
                entity.Property(e => e.PrixUnitaire).HasPrecision(10, 2);
                entity.Property(e => e.SousTotal).HasPrecision(10, 2);
            });

            // Configuration Paiement
            modelBuilder.Entity<Paiement>(entity =>
            {
                entity.ToTable("paiement");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.CommandeId).HasColumnName("commande_id");
                entity.Property(e => e.Methode).HasColumnName("methode");
                entity.Property(e => e.StatutPaiement).HasColumnName("statut_paiement");
                entity.Property(e => e.Montant).HasColumnName("montant");
                entity.Property(e => e.Reference).HasColumnName("reference");
                entity.Property(e => e.DatePaiement).HasColumnName("date_paiement");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                
                entity.HasOne(p => p.Commande)
                      .WithOne(c => c.Paiement)
                      .HasForeignKey<Paiement>(p => p.CommandeId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.Property(e => e.Montant).HasPrecision(10, 2);
            });

            // Configuration Favori
            modelBuilder.Entity<Favori>(entity =>
            {
                entity.ToTable("favori");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.ClientId).HasColumnName("client_id");
                entity.Property(e => e.ProduitId).HasColumnName("produit_id");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                
                entity.HasOne(f => f.Client)
                      .WithMany(c => c.Favoris)
                      .HasForeignKey(f => f.ClientId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasOne(f => f.Produit)
                      .WithMany(p => p.Favoris)
                      .HasForeignKey(f => f.ProduitId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasIndex(e => new { e.ClientId, e.ProduitId }).IsUnique();
            });

            // Configuration Avis
            modelBuilder.Entity<Avis>(entity =>
            {
                entity.ToTable("avis");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.ClientId).HasColumnName("client_id");
                entity.Property(e => e.ProduitId).HasColumnName("produit_id");
                entity.Property(e => e.Note).HasColumnName("note");
                entity.Property(e => e.Commentaire).HasColumnName("commentaire");
                entity.Property(e => e.DateAvis).HasColumnName("date_avis");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                
                entity.HasOne(a => a.Client)
                      .WithMany(c => c.Avis)
                      .HasForeignKey(a => a.ClientId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasOne(a => a.Produit)
                      .WithMany(p => p.Avis)
                      .HasForeignKey(a => a.ProduitId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasIndex(e => new { e.ClientId, e.ProduitId }).IsUnique();
            });

            // Configuration Livreur
            modelBuilder.Entity<Livreur>(entity =>
            {
                entity.ToTable("livreur");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.Nom).HasColumnName("nom");
                entity.Property(e => e.Telephone).HasColumnName("telephone");
                entity.Property(e => e.EstDisponible).HasColumnName("est_disponible");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                entity.Property(e => e.UpdatedAt).HasColumnName("updated_at");
            });

            // Configuration Livraison
            modelBuilder.Entity<Livraison>(entity =>
            {
                entity.ToTable("livraison");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.CommandeId).HasColumnName("commande_id");
                entity.Property(e => e.LivreurId).HasColumnName("livreur_id");
                entity.Property(e => e.Statut).HasColumnName("statut");
                entity.Property(e => e.DateDebut).HasColumnName("date_debut");
                entity.Property(e => e.DateFin).HasColumnName("date_fin");
                entity.Property(e => e.Notes).HasColumnName("notes");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                entity.Property(e => e.UpdatedAt).HasColumnName("updated_at");
                
                entity.HasOne(l => l.Commande)
                      .WithOne(c => c.Livraison)
                      .HasForeignKey<Livraison>(l => l.CommandeId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasOne(l => l.Livreur)
                      .WithMany(liv => liv.Livraisons)
                      .HasForeignKey(l => l.LivreurId)
                      .OnDelete(DeleteBehavior.Restrict);
            });

            // Configuration Gestionnaire
            modelBuilder.Entity<Gestionnaire>(entity =>
            {
                entity.ToTable("gestionnaire");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).HasColumnName("id");
                entity.Property(e => e.Nom).HasColumnName("nom");
                entity.Property(e => e.Prenom).HasColumnName("prenom");
                entity.Property(e => e.Email).HasColumnName("email");
                entity.Property(e => e.MotDePasse).HasColumnName("mot_de_passe");
                entity.Property(e => e.DateCreation).HasColumnName("date_creation");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at");
                entity.Property(e => e.UpdatedAt).HasColumnName("updated_at");
                
                entity.HasIndex(e => e.Email).IsUnique();
            });

            // Conversion des enums en strings
            ConfigureEnumConversions(modelBuilder);
        }

        private void ConfigureEnumConversions(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Produit>()
                .Property(p => p.Type)
                .HasConversion<string>();

            modelBuilder.Entity<Commande>()
                .Property(c => c.Statut)
                .HasConversion<string>();

            modelBuilder.Entity<Commande>()
                .Property(c => c.ModeConsommation)
                .HasConversion<string>();

            modelBuilder.Entity<Paiement>()
                .Property(p => p.Methode)
                .HasConversion<string>();

            modelBuilder.Entity<Paiement>()
                .Property(p => p.StatutPaiement)
                .HasConversion<string>();

            modelBuilder.Entity<Livraison>()
                .Property(l => l.Statut)
                .HasConversion<string>();
        }

        public override int SaveChanges()
        {
            UpdateTimestamps();
            return base.SaveChanges();
        }

        public override Task<int> SaveChangesAsync(CancellationToken cancellationToken = default)
        {
            UpdateTimestamps();
            return base.SaveChangesAsync(cancellationToken);
        }

        private void UpdateTimestamps()
        {
            var entries = ChangeTracker.Entries()
                .Where(e => e.State == EntityState.Modified);

            foreach (var entry in entries)
            {
                if (entry.Entity is Client client)
                    client.UpdatedAt = DateTime.UtcNow;
                else if (entry.Entity is Produit produit)
                    produit.UpdatedAt = DateTime.UtcNow;
                else if (entry.Entity is Commande commande)
                    commande.UpdatedAt = DateTime.UtcNow;
                else if (entry.Entity is Livreur livreur)
                    livreur.UpdatedAt = DateTime.UtcNow;
                else if (entry.Entity is Gestionnaire gestionnaire)
                    gestionnaire.UpdatedAt = DateTime.UtcNow;
                else if (entry.Entity is Livraison livraison)
                    livraison.UpdatedAt = DateTime.UtcNow;
            }
        }
    }
}
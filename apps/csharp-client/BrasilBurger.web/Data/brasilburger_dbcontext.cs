using Microsoft.EntityFrameworkCore;
using BrasilBurger.Web.Models;

namespace BrasilBurger.Web.Data
{
    public class BrasilBurgerContext : DbContext
    {
        public BrasilBurgerContext(DbContextOptions<BrasilBurgerContext> options)
            : base(options)
        {
        }

        // DbSets
        public DbSet<Client> Clients { get; set; }
        public DbSet<Zone> Zones { get; set; }
        public DbSet<Quartier> Quartiers { get; set; }
        public DbSet<AdresseLivraison> AdressesLivraison { get; set; }
        public DbSet<Produit> Produits { get; set; }
        public DbSet<MenuItem> MenuItems { get; set; }
        public DbSet<Commande> Commandes { get; set; }
        public DbSet<LigneCommande> LignesCommande { get; set; }
        public DbSet<Paiement> Paiements { get; set; }
        public DbSet<Favori> Favoris { get; set; }
        public DbSet<Avis> AvisListe { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Configuration Client
            modelBuilder.Entity<Client>(entity =>
            {
                entity.HasKey(e => e.Id);
                entity.HasIndex(e => e.Email).IsUnique();
                entity.HasIndex(e => e.Telephone).IsUnique();
                entity.Property(e => e.Email).IsRequired();
                entity.Property(e => e.Telephone).IsRequired();
            });

            // Configuration Zone
            modelBuilder.Entity<Zone>(entity =>
            {
                entity.HasKey(e => e.Id);
                entity.HasIndex(e => e.Nom).IsUnique();
            });

            // Configuration Quartier
            modelBuilder.Entity<Quartier>(entity =>
            {
                entity.HasKey(e => e.Id);
                entity.HasOne(q => q.Zone)
                      .WithMany(z => z.Quartiers)
                      .HasForeignKey(q => q.ZoneId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasIndex(e => e.ZoneId);
            });

            // Configuration AdresseLivraison
            modelBuilder.Entity<AdresseLivraison>(entity =>
            {
                entity.HasKey(e => e.Id);
                entity.HasOne(a => a.Client)
                      .WithMany(c => c.Adresses)
                      .HasForeignKey(a => a.ClientId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasOne(a => a.Quartier)
                      .WithMany(q => q.Adresses)
                      .HasForeignKey(a => a.QuartierId)
                      .OnDelete(DeleteBehavior.Restrict);
                entity.HasIndex(e => e.ClientId);
                entity.HasIndex(e => e.QuartierId);
            });

            // Configuration Produit
            modelBuilder.Entity<Produit>(entity =>
            {
                entity.HasKey(e => e.Id);
                entity.HasIndex(e => e.Type);
                entity.HasIndex(e => e.Disponible);
                entity.Property(e => e.Prix).HasPrecision(10, 2);
            });

            // Configuration MenuItem
            modelBuilder.Entity<MenuItem>(entity =>
            {
                entity.HasKey(e => e.Id);
                entity.HasOne(mi => mi.Menu)
                      .WithMany(p => p.MenuItems)
                      .HasForeignKey(mi => mi.MenuId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasOne(mi => mi.Produit)
                      .WithMany(p => p.MenusContenantCeProduit)
                      .HasForeignKey(mi => mi.ProduitId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasIndex(e => new { e.MenuId, e.ProduitId }).IsUnique();
            });

            // Configuration Commande
            modelBuilder.Entity<Commande>(entity =>
            {
                entity.HasKey(e => e.Id);
                entity.HasIndex(e => e.Numero).IsUnique();
                entity.HasIndex(e => e.CodeSuivi).IsUnique();
                entity.HasIndex(e => e.ClientId);
                entity.HasIndex(e => e.Statut);
                entity.HasIndex(e => e.DateCommande);
                
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
                entity.HasKey(e => e.Id);
                entity.HasOne(lc => lc.Commande)
                      .WithMany(c => c.Lignes)
                      .HasForeignKey(lc => lc.CommandeId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasOne(lc => lc.Produit)
                      .WithMany(p => p.LignesCommande)
                      .HasForeignKey(lc => lc.ProduitId)
                      .OnDelete(DeleteBehavior.Restrict);
                entity.HasIndex(e => e.CommandeId);
                entity.HasIndex(e => e.ProduitId);
                
                entity.Property(e => e.PrixUnitaire).HasPrecision(10, 2);
                entity.Property(e => e.SousTotal).HasPrecision(10, 2);
            });

            // Configuration Paiement
            modelBuilder.Entity<Paiement>(entity =>
            {
                entity.HasKey(e => e.Id);
                entity.HasIndex(e => e.CommandeId).IsUnique();
                entity.HasOne(p => p.Commande)
                      .WithOne(c => c.Paiement)
                      .HasForeignKey<Paiement>(p => p.CommandeId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.Property(e => e.Montant).HasPrecision(10, 2);
            });

            // Configuration Favori
            modelBuilder.Entity<Favori>(entity =>
            {
                entity.HasKey(e => e.Id);
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
                entity.HasKey(e => e.Id);
                entity.HasOne(a => a.Client)
                      .WithMany(c => c.AvisLaisses)
                      .HasForeignKey(a => a.ClientId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasOne(a => a.Produit)
                      .WithMany(p => p.Avis)
                      .HasForeignKey(a => a.ProduitId)
                      .OnDelete(DeleteBehavior.Cascade);
                entity.HasIndex(e => new { e.ClientId, e.ProduitId }).IsUnique();
                entity.HasIndex(e => e.ProduitId);
            });

            // Conversion des enums en strings
            ConfigureEnumConversions(modelBuilder);
        }

        private void ConfigureEnumConversions(ModelBuilder modelBuilder)
        {
            // Pour que les enums soient stockés en tant que strings dans la DB
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
            }
        }
    }
}

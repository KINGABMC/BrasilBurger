using BrasilBurger.Web.Data;
using BrasilBurger.Web.Models.Entities;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;

// Logging
builder.Logging.ClearProviders();
builder.Logging.AddConsole();

// Services
builder.Services.AddControllersWithViews();

// Database
var connectionString = configuration.GetConnectionString("NeonDB");
Console.WriteLine("🔗 Configuration NeonDB chargée");

builder.Services.AddDbContext<BrasilBurgerContext>(options =>
    options.UseNpgsql(connectionString));

// Session
builder.Services.AddDistributedMemoryCache();
builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromMinutes(30);
    options.Cookie.HttpOnly = true;
    options.Cookie.IsEssential = true;
    options.Cookie.Name = "BrasilBurger.Session";
});

var app = builder.Build();

// Pipeline
if (app.Environment.IsDevelopment())
{
    app.UseDeveloperExceptionPage();
}
else
{
    app.UseExceptionHandler("/Home/Error");
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseStaticFiles();
app.UseRouting();
app.UseAuthorization();
app.UseSession();

// Routes
app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Home}/{action=Index}/{id?}");

// Vérification DB SIMPLE et SÛRE
Console.WriteLine("🔍 Test connexion base de données...");

try
{
    using var scope = app.Services.CreateScope();
    var db = scope.ServiceProvider.GetRequiredService<BrasilBurgerContext>();
    
    // Test 1: Simple count
    var produits = await db.produit.CountAsync();  // Note: minuscule maintenant
    var clients = await db.client.CountAsync();    // Note: minuscule maintenant
    
    Console.WriteLine($"✅ DB OK - {produits} produits, {clients} clients");
    
    // Test 2: Récupération safe
    var topProduits = await db.produit
        .Where(p => p.Disponible == true)
        .Take(3)
        .Select(p => new { p.Nom, p.Prix, p.Type })
        .ToListAsync();
    
    if (topProduits.Any())
    {
        Console.WriteLine("🍔 Exemples produits:");
        foreach (var p in topProduits)
        {
            Console.WriteLine($"   • {p.Nom} - {p.Prix:F0} FCFA");
        }
    }
}
catch (Exception ex)
{
    Console.WriteLine($"⚠️ Note: {ex.Message.Split(':')[0]}");
    Console.WriteLine("   (L'application continue de fonctionner)");
}

// Info démarrage
Console.WriteLine($"🚀 Brasil Burger démarré");
Console.WriteLine($"🌐 {app.Environment.EnvironmentName}");
Console.WriteLine($"💰 {configuration["AppSettings:DefaultCurrency"] ?? "FCFA"}");
Console.WriteLine($"📅 {DateTime.Now:HH:mm}");

var port = Environment.GetEnvironmentVariable("PORT") ?? "8080";
app.Run($"http://0.0.0.0:{port}");
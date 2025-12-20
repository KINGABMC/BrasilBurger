using BrasilBurger.Web.Data;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;

// Logging (Render-friendly)
builder.Logging.ClearProviders();
builder.Logging.AddConsole();

// MVC
builder.Services.AddControllersWithViews();

// Database (NeonDB)
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

// ⚠️ PAS de UseHttpsRedirection sur Render
app.UseStaticFiles();
app.UseRouting();
app.UseAuthorization();
app.UseSession();

// Routes
app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Home}/{action=Index}/{id?}");

// 🔍 Test DB sécurisé
await using (var scope = app.Services.CreateAsyncScope())
{
    try
    {
        var db = scope.ServiceProvider.GetRequiredService<BrasilBurgerContext>();

        var produits = await db.produit.CountAsync();
        var clients = await db.client.CountAsync();

        Console.WriteLine($"✅ DB OK - {produits} produits, {clients} clients");
    }
    catch (Exception ex)
    {
        Console.WriteLine($"⚠️ DB warning: {ex.Message}");
        Console.WriteLine("   (L'application continue)");
    }
}

// Info démarrage
Console.WriteLine("🚀 Brasil Burger démarré");
Console.WriteLine($"🌐 Environnement: {app.Environment.EnvironmentName}");
Console.WriteLine($"📅 {DateTime.Now:HH:mm}");

// Port Render
var port = Environment.GetEnvironmentVariable("PORT") ?? "10000";
app.Run($"http://0.0.0.0:{port}");

# Étape 1 : image SDK pour compiler
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copier la solution et le projet
COPY BrasilBurger.sln .
COPY BrasilBurger.web/BrasilBurger.web.csproj BrasilBurger.web/

# Restaurer les dépendances
RUN dotnet restore

# Copier tout le reste
COPY . .

# Publier l'application
RUN dotnet publish BrasilBurger.web/BrasilBurger.web.csproj \
    -c Release \
    -o /app/publish \
    /p:UseAppHost=false

# Étape 2 : image runtime légère
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime
WORKDIR /app

# Copier le résultat du build
COPY --from=build /app/publish .

# Render écoute sur le port 10000
EXPOSE 10000

# Lancer l'application
ENTRYPOINT ["dotnet", "BrasilBurger.web.dll"]

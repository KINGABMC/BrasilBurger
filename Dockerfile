FROM php:8.2-apache

# 1. PORT pour Render
ENV PORT=10000
ENV APACHE_PORT=10000

# 2. Configurer Apache pour Render
RUN sed -i "s/Listen 80/Listen ${PORT}/g" /etc/apache2/ports.conf

WORKDIR /var/www/html

# 3. Dépendances système
RUN apt-get update && apt-get install -y \
    git curl unzip \
    libpq-dev libzip-dev libpng-dev libonig-dev libxml2-dev \
    libicu-dev libfreetype6-dev libjpeg62-turbo-dev \
    && docker-php-ext-configure gd --with-freetype --with-jpeg \
    && docker-php-ext-install -j$(nproc) \
    pdo pdo_pgsql pgsql zip gd mbstring xml intl bcmath opcache \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# 4. Installer Composer
COPY --from=composer:latest /usr/bin/composer /usr/bin/composer

# 5. Copier d'abord les fichiers de configuration
COPY composer.json composer.lock symfony.lock ./

# 6. Installer les dépendances PRODUCTION
RUN composer install --no-dev --no-scripts --no-autoloader --prefer-dist

# 7. Copier tout le code
COPY . .

# 8. Configuration Apache
COPY .docker/apache.conf /etc/apache2/sites-available/000-default.conf
RUN a2enmod rewrite

# 9. Optimiser l'autoloader
RUN composer dump-autoload --optimize --classmap-authoritative --no-dev

# 10. PERMISSIONS ET CACHE (PARTIE CRITIQUE)
# Nettoyer l'existant
RUN rm -rf var/cache/* var/log/*

# Créer les dossiers
RUN mkdir -p var/cache/prod var/log \
    && chmod -R 777 var/

# Générer le cache de production
RUN APP_ENV=prod php bin/console cache:clear --no-warmup
RUN APP_ENV=prod php bin/console cache:warmup

# Vérifier les routes
RUN APP_ENV=prod php bin/console debug:router

# 11. Health check
RUN echo '<?php http_response_code(200); echo "OK"; ?>' > public/health.php

EXPOSE ${PORT}

CMD ["apache2-foreground"]
FROM php:8.2-apache

# Port requis par Render
ENV PORT=10000
RUN sed -i "s/Listen 80/Listen ${PORT}/g" /etc/apache2/ports.conf
RUN sed -i "s/:80/:${PORT}/g" /etc/apache2/sites-available/*.conf

WORKDIR /var/www/html

# Dépendances système nécessaires à Symfony + PostgreSQL
RUN apt-get update && apt-get install -y \
    git \
    unzip \
    libpq-dev \
    libzip-dev \
    libpng-dev \
    && docker-php-ext-install \
    pdo \
    pdo_pgsql \
    zip \
    gd \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Installer Composer
COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

# Copier le projet Symfony
COPY . .

# Configuration Apache pour Symfony
RUN echo "<VirtualHost *:${PORT}>" > /etc/apache2/sites-available/000-default.conf \
 && echo "DocumentRoot /var/www/html/public" >> /etc/apache2/sites-available/000-default.conf \
 && echo "<Directory /var/www/html/public>" >> /etc/apache2/sites-available/000-default.conf \
 && echo "AllowOverride All" >> /etc/apache2/sites-available/000-default.conf \
 && echo "Require all granted" >> /etc/apache2/sites-available/000-default.conf \
 && echo "</Directory>" >> /etc/apache2/sites-available/000-default.conf \
 && echo "</VirtualHost>" >> /etc/apache2/sites-available/000-default.conf

# Activer rewrite
RUN a2enmod rewrite

# Installer dépendances Symfony (sans migrations)
RUN composer install --no-dev --optimize-autoloader --no-interaction \
 && php bin/console cache:clear --env=prod --no-debug \
 && php bin/console cache:warmup --env=prod --no-debug

# Permissions
RUN chown -R www-data:www-data var

EXPOSE 10000

# Lancer Apache
CMD ["apache2-foreground"]

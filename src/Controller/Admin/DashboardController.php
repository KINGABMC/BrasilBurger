<?php

namespace App\Controller\Admin;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;
use Doctrine\ORM\EntityManagerInterface;

#[Route('/admin')]
class DashboardController extends AbstractController
{
    #[Route('/dashboard', name: 'app_admin_dashboard')]
    public function dashboard(Request $request, EntityManagerInterface $em): Response
    {
        if (!$request->getSession()->has('admin_id')) {
            return $this->redirectToRoute('app_login');
        }
        
        $conn = $em->getConnection();
        
        // Statistiques de base
        $stats = $conn->executeQuery("
            SELECT 
                (SELECT COUNT(*) FROM client WHERE est_actif = true) as total_clients,
                (SELECT COUNT(*) FROM commande WHERE DATE(date_commande) = CURRENT_DATE) as commandes_du_jour,
                (SELECT COALESCE(SUM(montant_total), 0) FROM commande WHERE statut != 'ANNULEE') as chiffre_affaires,
                (SELECT COUNT(*) FROM livreur WHERE est_disponible = true) as livreurs_actifs
        ")->fetchAssociative();
        
        // Statistiques avancées demandées
        $statsAvancees = $conn->executeQuery("
                        SELECT 
                            -- Commandes en cours aujourd'hui (par mode)
                                (SELECT COUNT(*) FROM commande 
                                WHERE DATE(date_commande) = CURRENT_DATE 
                                AND (
                                    -- Pour LIVRAISON : tous statuts sauf terminés
                                    (mode_consommation = 'LIVRAISON' AND statut NOT IN ('LIVREE', 'ANNULEE'))
                                    OR
                                    -- Pour SUR_PLACE/EMPORTER : tous statuts sauf PRETE (car PRETE = terminé)
                                    (mode_consommation IN ('SUR_PLACE', 'EMPORTER') AND statut NOT IN ('PRETE', 'ANNULEE'))
                                )) as commandes_en_cours_jour,
                            
                            -- 2. Commandes créées AUJOURD'HUI et TERMINÉES (validées)
                            (SELECT COUNT(*) FROM commande 
                            WHERE DATE(date_commande) = CURRENT_DATE 
                            AND statut IN ('PRETE', 'LIVREE')) as commandes_terminees_jour,
                            
                            -- 3. Recettes Journalières
                            (SELECT COALESCE(SUM(montant_total), 0) FROM commande 
                            WHERE DATE(date_commande) = CURRENT_DATE 
                            AND statut != 'ANNULEE') as recettes_journalieres,
                            
                            -- 4. Commandes annulées AUJOURD'HUI
                            (SELECT COUNT(*) FROM commande 
                            WHERE DATE(updated_at) = CURRENT_DATE 
                            AND statut = 'ANNULEE') as commandes_annulees_jour
            ")->fetchAssociative();
        
        // Top produits (burgers et menus) les plus vendus du jour
        $topVentes = $conn->executeQuery("
            SELECT 
                p.nom as nom_produit,
                p.type as type_produit,
                SUM(lc.quantite) as quantite_totale
            FROM lignecommande lc
            INNER JOIN commande c ON lc.commande_id = c.id
            INNER JOIN produit p ON lc.produit_id = p.id
            WHERE DATE(c.date_commande) = CURRENT_DATE
            AND c.statut != 'ANNULEE'
            AND p.type IN ('BURGER', 'MENU')
            GROUP BY p.id, p.nom, p.type
            ORDER BY quantite_totale DESC
            LIMIT 5
        ")->fetchAllAssociative();
        
        return $this->render('admin/dashboard/index.html.twig', [
            'stats' => $stats ?: [],
            'statsAvancees' => $statsAvancees ?: [],
            'topVentes' => $topVentes ?: [],
            'page_title' => 'Dashboard Admin'
        ]);
    }
}
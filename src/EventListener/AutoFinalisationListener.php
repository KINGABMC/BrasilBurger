<?php

namespace App\EventListener;

use Symfony\Component\HttpKernel\Event\RequestEvent;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\EventDispatcher\Attribute\AsEventListener;

#[AsEventListener(event: 'kernel.request', priority: 10)]
class AutoFinalisationListener
{
    private EntityManagerInterface $em;
    private static $lastCheck = 0;

    public function __construct(EntityManagerInterface $em)
    {
        $this->em = $em;
    }

    public function onKernelRequest(RequestEvent $event): void
    {
        // Ne s'exécute que sur la requête principale
        if (!$event->isMainRequest()) {
            return;
        }
        
        // Ne s'exécute que toutes les 5 minutes (300 secondes)
        $now = time();
        if ($now - self::$lastCheck < 300) {
            return;
        }
        
        self::$lastCheck = $now;
        
        try {
            $this->finaliserCommandesAutomatiques();
        } catch (\Exception $e) {
            // Log l'erreur mais ne bloque pas la requête
            error_log('Erreur finalisation auto: ' . $e->getMessage());
        }
    }

    private function finaliserCommandesAutomatiques(): void
    {
        $conn = $this->em->getConnection();
        
        // Récupérer les commandes en livraison dont le délai est dépassé
        $commandes = $conn->executeQuery("
            SELECT c.id, c.temps_estimation_livraison, l.livreur_id, l.date_debut
            FROM commande c
            INNER JOIN livraison l ON c.id = l.commande_id
            WHERE c.statut = 'EN_LIVRAISON'
            AND c.temps_estimation_livraison IS NOT NULL
            AND l.date_debut IS NOT NULL
            AND EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - l.date_debut)) / 60 >= c.temps_estimation_livraison
        ")->fetchAllAssociative();
        
        foreach ($commandes as $commande) {
            // Finaliser la commande
            $conn->executeQuery(
                "UPDATE commande SET statut = 'LIVREE', updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                [$commande['id']]
            );
            
            // Libérer le livreur
            $conn->executeQuery(
                "UPDATE livreur SET est_disponible = true, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                [$commande['livreur_id']]
            );
            
            // Mettre à jour la livraison
            $conn->executeQuery(
                "UPDATE livraison SET statut = 'TERMINEE', date_fin = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE commande_id = ?",
                [$commande['id']]
            );
            
            // Enregistrer l'historique
            $conn->executeQuery(
                "INSERT INTO commande_statut_historique (commande_id, ancien_statut, nouveau_statut, changed_by, notes, changed_at) 
                 VALUES (?, 'EN_LIVRAISON', 'LIVREE', 'SYSTEME', 'Finalisée automatiquement après délai estimé', CURRENT_TIMESTAMP)",
                [$commande['id']]
            );
        }
    }
}
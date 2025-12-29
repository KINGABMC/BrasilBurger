<?php

namespace App\Controller\Admin;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/admin/commandes')]
class CommandeController extends AbstractController
{
    #[Route('/', name: 'admin_commandes')]
    public function index(Request $request, EntityManagerInterface $em): Response
    {
        if (!$request->getSession()->has('admin_id')) {
            return $this->redirectToRoute('app_login');
        }
        
        $conn = $em->getConnection();
        
        $filtreStatut = $request->query->get('statut', '');
        $filtreDate = $request->query->get('date', '');
        $filtreClient = $request->query->get('client', '');
        $filtreType = $request->query->get('type', '');
        
        $page = max(1, (int)$request->query->get('page', 1));
        $limit = 5;
        $offset = ($page - 1) * $limit;
        
        $sqlCount = "
            SELECT COUNT(*) as total
            FROM commande c
            LEFT JOIN client cl ON c.client_id = cl.id
            WHERE 1=1
        ";
        
        $sql = "
            SELECT c.*, 
                   cl.nom as client_nom, 
                   cl.prenom as client_prenom,
                   cl.telephone as client_telephone
            FROM commande c
            LEFT JOIN client cl ON c.client_id = cl.id
            WHERE 1=1
        ";
        
        $params = [];
        
        if ($filtreStatut) {
            $sql .= " AND c.statut = ?";
            $sqlCount .= " AND c.statut = ?";
            $params[] = strtoupper($filtreStatut);
        }
        
        if ($filtreDate) {
            $sql .= " AND DATE(c.date_commande) = ?";
            $sqlCount .= " AND DATE(c.date_commande) = ?";
            $params[] = $filtreDate;
        }
        
        if ($filtreClient) {
            $sql .= " AND (cl.nom ILIKE ? OR cl.prenom ILIKE ? OR cl.telephone LIKE ?)";
            $sqlCount .= " AND (cl.nom ILIKE ? OR cl.prenom ILIKE ? OR cl.telephone LIKE ?)";
            $searchTerm = '%' . $filtreClient . '%';
            $params[] = $searchTerm;
            $params[] = $searchTerm;
            $params[] = $searchTerm;
        }
        
        if ($filtreType) {
            $sql .= " AND EXISTS (
                SELECT 1 FROM lignecommande lc 
                INNER JOIN produit p ON lc.produit_id = p.id
                WHERE lc.commande_id = c.id AND p.type = ?
            )";
            $sqlCount .= " AND EXISTS (
                SELECT 1 FROM lignecommande lc 
                INNER JOIN produit p ON lc.produit_id = p.id
                WHERE lc.commande_id = c.id AND p.type = ?
            )";
            $params[] = $filtreType;
        }
        
        $total = $conn->executeQuery($sqlCount, $params)->fetchAssociative()['total'];
        $totalPages = ceil($total / $limit);
        
        $sql .= " ORDER BY c.date_commande DESC LIMIT ? OFFSET ?";
        $params[] = $limit;
        $params[] = $offset;
        
        $commandes = $conn->executeQuery($sql, $params)->fetchAllAssociative();
        
        return $this->render('admin/commande/index.html.twig', [
            'commandes' => $commandes,
            'filtreStatut' => $filtreStatut,
            'filtreDate' => $filtreDate,
            'filtreClient' => $filtreClient,
            'filtreType' => $filtreType,
            'page' => $page,
            'totalPages' => $totalPages,
            'total' => $total,
            'page_title' => 'Commandes'
        ]);
    }
    
    #[Route('/details/{id}', name: 'admin_commande_details')]
    public function details(int $id, Request $request, EntityManagerInterface $em): Response
    {
        if (!$request->getSession()->has('admin_id')) {
            return $this->redirectToRoute('app_login');
        }
        
        $conn = $em->getConnection();
        
        $commande = $conn->executeQuery("
            SELECT c.*, 
                   cl.nom as client_nom, 
                   cl.prenom as client_prenom,
                   cl.telephone as client_telephone,
                   cl.email as client_email,
                   a.rue, a.numero as adresse_numero, a.details as adresse_details,
                   q.nom as quartier_nom,
                   z.nom as zone_nom
            FROM commande c
            INNER JOIN client cl ON c.client_id = cl.id
            LEFT JOIN adresselivraison a ON c.adresse_livraison_id = a.id
            LEFT JOIN quartier q ON a.quartier_id = q.id
            LEFT JOIN zone z ON q.zone_id = z.id
            WHERE c.id = ?
        ", [$id])->fetchAssociative();
        
        if (!$commande) {
            $this->addFlash('error', 'Commande introuvable');
            return $this->redirectToRoute('admin_commandes');
        }
        
        $lignes = $conn->executeQuery("
            SELECT lc.*,
                   p.nom as produit_nom,
                   p.type as produit_type,
                   p.url_image as produit_image
            FROM lignecommande lc
            INNER JOIN produit p ON lc.produit_id = p.id
            WHERE lc.commande_id = ?
            ORDER BY lc.id
        ", [$id])->fetchAllAssociative();
        
        $paiement = $conn->executeQuery("
            SELECT * FROM paiement 
            WHERE commande_id = ?
            ORDER BY date_paiement DESC
            LIMIT 1
        ", [$id])->fetchAssociative();
        
        $livraison = $conn->executeQuery("
            SELECT l.*, liv.nom as livreur_nom, liv.telephone as livreur_telephone
            FROM livraison l
            INNER JOIN livreur liv ON l.livreur_id = liv.id
            WHERE l.commande_id = ?
        ", [$id])->fetchAssociative();
        
        $livreurs = $conn->executeQuery("
            SELECT * FROM livreur WHERE est_disponible = true ORDER BY nom
        ")->fetchAllAssociative();
        
        // Historique des statuts
        $historique = $conn->executeQuery("
            SELECT * FROM commande_statut_historique
            WHERE commande_id = ?
            ORDER BY changed_at DESC
        ", [$id])->fetchAllAssociative();
        
        $statutsAutorises = $this->getStatutsAutorises($commande['mode_consommation'], $commande['statut']);
        
        return $this->render('admin/commande/details.html.twig', [
            'commande' => $commande,
            'lignes' => $lignes,
            'paiement' => $paiement,
            'livraison' => $livraison,
            'livreurs' => $livreurs,
            'historique' => $historique,
            'statutsAutorises' => $statutsAutorises,
            'page_title' => 'Détails Commande #' . $commande['numero']
        ]);
    }
    
    private function getStatutsAutorises(string $modeConsommation, string $statutActuel): array
    {
        $tousLesStatuts = [
            'EN_ATTENTE' => '⏳ En attente',
            'CONFIRMEE' => '✅ Confirmée',
            'EN_PREPARATION' => '👨‍🍳 En préparation',
            'PRETE' => '✔️ Prête',
            'EN_LIVRAISON' => '🚗 En livraison',
            'LIVREE' => '📬 Livrée',
            'ANNULEE' => '❌ Annulée'
        ];
        
        $modeConsommation = strtoupper($modeConsommation);
        $statutActuel = strtoupper($statutActuel);
        
        if (in_array($modeConsommation, ['SUR_PLACE', 'EMPORTER'])) {
            unset($tousLesStatuts['EN_LIVRAISON']);
            unset($tousLesStatuts['LIVREE']);
        }
        
        if ($statutActuel === 'ANNULEE' || $statutActuel === 'LIVREE') {
            return [];
        }
        
        unset($tousLesStatuts[$statutActuel]);
        unset($tousLesStatuts['ANNULEE']);
        
        return $tousLesStatuts;
    }
    
    private function enregistrerHistorique(
        EntityManagerInterface $em, 
        int $commandeId, 
        ?string $ancienStatut, 
        string $nouveauStatut,
        string $changedBy,
        ?string $notes = null
    ): void {
        $conn = $em->getConnection();
        $conn->executeQuery(
            "INSERT INTO commande_statut_historique (commande_id, ancien_statut, nouveau_statut, changed_by, notes, changed_at) 
             VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
            [$commandeId, $ancienStatut, $nouveauStatut, $changedBy, $notes]
        );
    }
    
    #[Route('/update-statut/{id}', name: 'admin_commande_update_statut', methods: ['POST'])]
    public function updateStatut(int $id, Request $request, EntityManagerInterface $em): Response
    {
        if (!$request->getSession()->has('admin_id')) {
            return $this->redirectToRoute('app_login');
        }
        
        $nouveauStatut = strtoupper($request->request->get('statut'));
        
        if (!$nouveauStatut) {
            $this->addFlash('error', 'Statut invalide');
            return $this->redirectToRoute('admin_commande_details', ['id' => $id]);
        }
        
        try {
            $conn = $em->getConnection();
            
            $commande = $conn->executeQuery(
                "SELECT * FROM commande WHERE id = ?",
                [$id]
            )->fetchAssociative();
            
            if (!$commande) {
                $this->addFlash('error', 'Commande introuvable');
                return $this->redirectToRoute('admin_commandes');
            }
            
            $ancienStatut = strtoupper($commande['statut']);
            $modeConsommation = strtoupper($commande['mode_consommation']);
            
            if (in_array($modeConsommation, ['SUR_PLACE', 'EMPORTER'])) {
                if (in_array($nouveauStatut, ['EN_LIVRAISON', 'LIVREE'])) {
                    $this->addFlash('error', 'Statut non autorisé pour ce mode de consommation');
                    return $this->redirectToRoute('admin_commande_details', ['id' => $id]);
                }
            }
            
            if ($nouveauStatut === 'EN_LIVRAISON') {
                $livraison = $conn->executeQuery(
                    "SELECT * FROM livraison WHERE commande_id = ?",
                    [$id]
                )->fetchAssociative();
                
                if (!$livraison) {
                    $this->addFlash('error', 'Vous devez affecter un livreur avant de passer en livraison');
                    return $this->redirectToRoute('admin_commande_details', ['id' => $id]);
                }
                
                // Rendre le livreur indisponible
                $conn->executeQuery(
                    "UPDATE livreur SET est_disponible = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                    [$livraison['livreur_id']]
                );
                
                // Mettre à jour la livraison
                $conn->executeQuery(
                    "UPDATE livraison SET statut = 'EN_COURS', date_debut = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE commande_id = ?",
                    [$id]
                );
            }
            
            // Mise à jour du statut
            $conn->executeQuery(
                "UPDATE commande SET statut = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                [$nouveauStatut, $id]
            );
            
            // Enregistrer l'historique
            $adminNom = $request->getSession()->get('admin_nom', 'Administrateur');
            $this->enregistrerHistorique($em, $id, $ancienStatut, $nouveauStatut, $adminNom);
            
            $this->addFlash('success', 'Statut mis à jour avec succès');
            
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de la mise à jour: ' . $e->getMessage());
        }
        
        return $this->redirectToRoute('admin_commande_details', ['id' => $id]);
    }
    
    #[Route('/annuler/{id}', name: 'admin_commande_annuler', methods: ['POST'])]
    public function annuler(int $id, Request $request, EntityManagerInterface $em): Response
    {
        if (!$request->getSession()->has('admin_id')) {
            return $this->redirectToRoute('app_login');
        }
        
        try {
            $conn = $em->getConnection();
            
            $commande = $conn->executeQuery(
                "SELECT * FROM commande WHERE id = ?",
                [$id]
            )->fetchAssociative();
            
            if (!$commande) {
                $this->addFlash('error', 'Commande introuvable');
                return $this->redirectToRoute('admin_commandes');
            }
            
            $statutActuel = strtoupper($commande['statut']);
            
            if ($statutActuel === 'ANNULEE') {
                $this->addFlash('warning', 'Cette commande est déjà annulée');
                return $this->redirectToRoute('admin_commande_details', ['id' => $id]);
            }
            
            if ($statutActuel === 'LIVREE') {
                $this->addFlash('error', 'Impossible d\'annuler une commande déjà livrée');
                return $this->redirectToRoute('admin_commande_details', ['id' => $id]);
            }
            
            // Si en livraison, libérer le livreur
            if ($statutActuel === 'EN_LIVRAISON') {
                $livraison = $conn->executeQuery(
                    "SELECT livreur_id FROM livraison WHERE commande_id = ?",
                    [$id]
                )->fetchAssociative();
                
                if ($livraison) {
                    $conn->executeQuery(
                        "UPDATE livreur SET est_disponible = true, updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                        [$livraison['livreur_id']]
                    );
                }
            }
            
            $conn->executeQuery(
                "UPDATE commande SET statut = 'ANNULEE', updated_at = CURRENT_TIMESTAMP WHERE id = ?",
                [$id]
            );
            
            $adminNom = $request->getSession()->get('admin_nom', 'Administrateur');
            $this->enregistrerHistorique($em, $id, $statutActuel, 'ANNULEE', $adminNom, 'Annulée par l\'administrateur');
            
            $this->addFlash('success', 'Commande annulée avec succès');
            
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de l\'annulation: ' . $e->getMessage());
        }
        
        return $this->redirectToRoute('admin_commandes');
    }
    
    #[Route('/affecter-livreur/{id}', name: 'admin_commande_affecter_livreur', methods: ['POST'])]
    public function affecterLivreur(int $id, Request $request, EntityManagerInterface $em): Response
    {
        if (!$request->getSession()->has('admin_id')) {
            return $this->redirectToRoute('app_login');
        }
        
        $livreurId = $request->request->get('livreur_id');
        
        if (!$livreurId) {
            $this->addFlash('error', 'Veuillez sélectionner un livreur');
            return $this->redirectToRoute('admin_commande_details', ['id' => $id]);
        }
        
        try {
            $conn = $em->getConnection();
            
            $commande = $conn->executeQuery(
                "SELECT * FROM commande WHERE id = ?",
                [$id]
            )->fetchAssociative();
            
            if (strtoupper($commande['statut']) !== 'PRETE') {
                $this->addFlash('error', 'La commande doit être prête avant d\'affecter un livreur');
                return $this->redirectToRoute('admin_commande_details', ['id' => $id]);
            }
            
            $livraisonExiste = $conn->executeQuery(
                "SELECT id FROM livraison WHERE commande_id = ?",
                [$id]
            )->fetchAssociative();
            
            if ($livraisonExiste) {
                $conn->executeQuery(
                    "UPDATE livraison SET livreur_id = ?,statut = 'ATTENTE_AFFECTATION', updated_at = CURRENT_TIMESTAMP WHERE commande_id = ?",
                    [$livreurId, $id]
                );
            } else {
                $conn->executeQuery(
                    "INSERT INTO livraison (commande_id, livreur_id, statut, created_at, updated_at) 
                     VALUES (?, ?, 'ATTENTE_AFFECTATION', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                    [$id, $livreurId]
                );
            }
            
            $this->addFlash('success', 'Livreur affecté avec succès. Vous pouvez maintenant passer la commande en livraison.');
            
        } catch (\Exception $e) {
            $this->addFlash('error', 'Erreur lors de l\'affectation: ' . $e->getMessage());
        }
        
        return $this->redirectToRoute('admin_commande_details', ['id' => $id]);
    }
}
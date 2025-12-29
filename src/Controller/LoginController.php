<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;
use Doctrine\ORM\EntityManagerInterface;
use App\Entity\Gestionnaire;

class LoginController extends AbstractController
{
    #[Route('/login', name: 'app_login')]
    public function index(Request $request, EntityManagerInterface $em): Response
    {
        $error = null;

        // On ne redirige vers dashboard que si POST est correct
        if ($request->isMethod('POST')) {
            $email = $request->request->get('email');
            $password = $request->request->get('password');

            $gestionnaire = $em->getRepository(Gestionnaire::class)
                ->findOneBy(['email' => $email]);

            if (!$gestionnaire) {
                $error = 'Email incorrect.';
            } elseif ($password !== $gestionnaire->getMotDePasse()) {
                $error = 'Mot de passe incorrect.';
            } else {
                // Authentification réussie → on crée la session
                $request->getSession()->set('admin_id', $gestionnaire->getId());
                $request->getSession()->set('admin_email', $gestionnaire->getEmail());
                $request->getSession()->set('admin_nom', $gestionnaire->getNom());
                return $this->redirectToRoute('app_admin_dashboard');
            }
        }

        // On force l’affichage du login même si session existante
        return $this->render('login.html.twig', ['error' => $error]);
    }

    #[Route('/logout', name: 'app_logout')]
    public function logout(Request $request): Response
    {
        $request->getSession()->invalidate();
        return $this->redirectToRoute('app_login');
    }

    #[Route('/', name: 'app_home')]
    public function home(): Response
    {
        // Redirige toujours vers login
        return $this->redirectToRoute('app_login');
    }
}

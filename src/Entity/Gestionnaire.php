<?php

namespace App\Entity;

use App\Repository\GestionnaireRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: GestionnaireRepository::class)]
#[ORM\Table(name: 'gestionnaire')]
class Gestionnaire
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(name: 'id')]
    private ?int $id = null;

    #[ORM\Column(name: 'nom', length: 100)]
    private ?string $nom = null;

    #[ORM\Column(name: 'prenom', length: 100)]
    private ?string $prenom = null;

    #[ORM\Column(name: 'email', length: 255, unique: true)]
    private ?string $email = null;

    #[ORM\Column(name: 'mot_de_passe', length: 255)]
    private ?string $motDePasse = null;

    // On stocke en string pour éviter le crash Doctrine
    #[ORM\Column(name: 'created_at', type: Types::STRING)]
    private ?string $createdAt = null;

    #[ORM\Column(name: 'updated_at', type: Types::STRING)]
    private ?string $updatedAt = null;

    // -------------------------------
    // Getters et Setters
    // -------------------------------

    public function getId(): ?int { return $this->id; }

    public function getNom(): ?string { return $this->nom; }
    public function setNom(string $nom): self { $this->nom = $nom; return $this; }

    public function getPrenom(): ?string { return $this->prenom; }
    public function setPrenom(string $prenom): self { $this->prenom = $prenom; return $this; }

    public function getEmail(): ?string { return $this->email; }
    public function setEmail(string $email): self { $this->email = $email; return $this; }

    public function getMotDePasse(): ?string { return $this->motDePasse; }
    public function setMotDePasse(string $motDePasse): self { $this->motDePasse = $motDePasse; return $this; }

    // -------------------------------
    // Conversion automatique des dates
    // -------------------------------
    public function getCreatedAt(): ?\DateTimeInterface
    {
        return $this->createdAt ? new \DateTime($this->createdAt) : null;
    }

    public function setCreatedAt(string $createdAt): self
    {
        $this->createdAt = $createdAt;
        return $this;
    }

    public function getUpdatedAt(): ?\DateTimeInterface
    {
        return $this->updatedAt ? new \DateTime($this->updatedAt) : null;
    }

    public function setUpdatedAt(string $updatedAt): self
    {
        $this->updatedAt = $updatedAt;
        return $this;
    }
}

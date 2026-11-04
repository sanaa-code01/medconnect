package blooddonation.bean;

import blooddonation.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Compte utilisateur. Donneur et Hopital héritent de cette classe
 * (stratégie SINGLE_TABLE : une seule table "users" avec une colonne discriminante).
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_compte")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 60)
    private String nom;

    @Column(nullable = false, length = 60)
    private String prenom;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /** Hash PBKDF2 du mot de passe (jamais le mot de passe en clair). */
    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Column(length = 20)
    private String telephone;

    @Column(length = 200)
    private String adresse;

    @Column(length = 80)
    private String ville;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription;

    /** Constructeur exigé par Hibernate. */
    protected User() { }

    protected User(Role role) {
        this.role = role;
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
}

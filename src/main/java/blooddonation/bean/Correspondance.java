package blooddonation.bean;

import blooddonation.enums.StatutCorrespondance;
import blooddonation.utils.Dates;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Réponse d'un donneur à une demande ("Je veux donner").
 * Un donneur ne peut répondre qu'une seule fois à la même demande.
 */
@Entity
@Table(name = "reponses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"donneur_id", "demande_id"}))
public class Correspondance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "donneur_id", nullable = false)
    private Donneur donneur;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "demande_id", nullable = false)
    private Demande demande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCorrespondance statut;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    public String getDateCreationFr() { return Dates.fr(dateCreation); }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Donneur getDonneur() { return donneur; }
    public void setDonneur(Donneur donneur) { this.donneur = donneur; }

    public Demande getDemande() { return demande; }
    public void setDemande(Demande demande) { this.demande = demande; }

    public StatutCorrespondance getStatut() { return statut; }
    public void setStatut(StatutCorrespondance statut) { this.statut = statut; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}

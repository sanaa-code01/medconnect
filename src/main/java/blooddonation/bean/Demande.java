package blooddonation.bean;

import blooddonation.enums.EtatDemande;
import blooddonation.enums.GroupeSang;
import blooddonation.enums.Urgence;
import blooddonation.utils.Dates;
import jakarta.persistence.*;

import java.time.LocalDate;

/** Demande de sang publiée par un hôpital. */
@Entity
@Table(name = "demandes")
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "hopital_id", nullable = false)
    private Hopital hopital;

    @Enumerated(EnumType.STRING)
    @Column(name = "groupe_sanguin", nullable = false, length = 10)
    private GroupeSang groupeSanguin;

    /** Nombre de poches (donneurs) nécessaires. */
    @Column(nullable = false)
    private int quantite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Urgence urgence;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EtatDemande etat;

    @Column(length = 500)
    private String description;

    @Column(name = "date_demande", nullable = false)
    private LocalDate dateDemande;

    public String getDateDemandeFr() { return Dates.fr(dateDemande); }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Hopital getHopital() { return hopital; }
    public void setHopital(Hopital hopital) { this.hopital = hopital; }

    public GroupeSang getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(GroupeSang groupeSanguin) { this.groupeSanguin = groupeSanguin; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public Urgence getUrgence() { return urgence; }
    public void setUrgence(Urgence urgence) { this.urgence = urgence; }

    public EtatDemande getEtat() { return etat; }
    public void setEtat(EtatDemande etat) { this.etat = etat; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateDemande() { return dateDemande; }
    public void setDateDemande(LocalDate dateDemande) { this.dateDemande = dateDemande; }
}

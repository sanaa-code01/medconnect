package blooddonation.bean;

import blooddonation.utils.Dates;
import jakarta.persistence.*;

import java.time.LocalDate;

/** Don effectivement réalisé, enregistré par l'hôpital. */
@Entity
@Table(name = "dons")
public class Don {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "donneur_id", nullable = false)
    private Donneur donneur;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "demande_id", nullable = false)
    private Demande demande;

    @Column(name = "date_don", nullable = false)
    private LocalDate dateDon;

    @Column(length = 150)
    private String lieu;

    public String getDateDonFr() { return Dates.fr(dateDon); }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Donneur getDonneur() { return donneur; }
    public void setDonneur(Donneur donneur) { this.donneur = donneur; }

    public Demande getDemande() { return demande; }
    public void setDemande(Demande demande) { this.demande = demande; }

    public LocalDate getDateDon() { return dateDon; }
    public void setDateDon(LocalDate dateDon) { this.dateDon = dateDon; }

    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
}

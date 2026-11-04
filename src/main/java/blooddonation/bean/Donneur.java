package blooddonation.bean;

import blooddonation.enums.Disponibilite;
import blooddonation.enums.GroupeSang;
import blooddonation.enums.Role;
import blooddonation.utils.Dates;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("DONNEUR")
public class Donneur extends User {

    @Enumerated(EnumType.STRING)
    @Column(name = "groupe_sanguin", length = 10)
    private GroupeSang groupeSanguin;

    @Column(name = "date_dernier_don")
    private LocalDate dateDernierDon;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Disponibilite disponibilite;

    public Donneur() {
        super(Role.DONNEUR);
        this.disponibilite = Disponibilite.DISPONIBLE;
    }

    public String getDateDernierDonFr() { return Dates.fr(dateDernierDon); }

    public GroupeSang getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(GroupeSang groupeSanguin) { this.groupeSanguin = groupeSanguin; }

    public LocalDate getDateDernierDon() { return dateDernierDon; }
    public void setDateDernierDon(LocalDate dateDernierDon) { this.dateDernierDon = dateDernierDon; }

    public Disponibilite getDisponibilite() { return disponibilite; }
    public void setDisponibilite(Disponibilite disponibilite) { this.disponibilite = disponibilite; }
}

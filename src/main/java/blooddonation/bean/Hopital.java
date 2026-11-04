package blooddonation.bean;

import blooddonation.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Compte hôpital. Les champs nom / prénom hérités de User sont ceux
 * du responsable du compte (RespoHôpital).
 */
@Entity
@DiscriminatorValue("HOPITAL")
public class Hopital extends User {

    @Column(name = "nom_hopital", length = 120)
    private String nomHopital;

    public Hopital() {
        super(Role.HOPITAL);
    }

    public String getNomHopital() { return nomHopital; }
    public void setNomHopital(String nomHopital) { this.nomHopital = nomHopital; }
}

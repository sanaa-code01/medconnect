package blooddonation.dao.interfaces;

import blooddonation.bean.Correspondance;
import blooddonation.bean.Demande;
import blooddonation.bean.Don;
import blooddonation.bean.Donneur;

import java.util.List;

public interface DonDao {
    List<Don> findByDonneur(int donneurId);
    List<Don> findByHopital(int hopitalId);
    long countByDemande(int demandeId);

    /**
     * Enregistre un don et met à jour, dans la même transaction,
     * la réponse du donneur, le donneur (date du dernier don) et la demande (état).
     */
    void enregistrer(Don don, Correspondance correspondance, Donneur donneur, Demande demande);
}

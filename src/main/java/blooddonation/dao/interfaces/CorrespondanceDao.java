package blooddonation.dao.interfaces;

import blooddonation.bean.Correspondance;
import blooddonation.enums.StatutCorrespondance;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CorrespondanceDao {
    Optional<Correspondance> findById(int id);
    void save(Correspondance correspondance);
    Correspondance update(Correspondance correspondance);
    boolean existsByDonneurAndDemande(int donneurId, int demandeId);
    List<Correspondance> findByDonneur(int donneurId);
    List<Correspondance> findByDemande(int demandeId);
    /** Toutes les réponses reçues par les demandes d'un hôpital. */
    List<Correspondance> findByHopital(int hopitalId);
    /** Passe au statut "nouveau" toutes les réponses d'une demande qui ont un des statuts "anciens". */
    int updateStatutByDemande(int demandeId, Collection<StatutCorrespondance> anciens, StatutCorrespondance nouveau);
}

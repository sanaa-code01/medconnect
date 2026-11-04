package blooddonation.dao.interfaces;

import blooddonation.bean.Demande;
import blooddonation.enums.GroupeSang;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DemandeDao {
    Optional<Demande> findById(int id);
    void save(Demande demande);
    Demande update(Demande demande);
    void delete(int id);
    List<Demande> findByHopital(int hopitalId);
    /** Demandes ouvertes dont le groupe sanguin fait partie de la liste donnée. */
    List<Demande> findOuvertesByGroupes(Collection<GroupeSang> groupes);
}

package blooddonation.dao.impl;

import blooddonation.bean.Demande;
import blooddonation.dao.interfaces.DemandeDao;
import blooddonation.enums.EtatDemande;
import blooddonation.enums.GroupeSang;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DemandeDaoImpl implements DemandeDao {

    @Override
    public Optional<Demande> findById(int id) {
        return HibernateTx.read(s -> Optional.ofNullable(s.get(Demande.class, id)));
    }

    @Override
    public void save(Demande demande) {
        HibernateTx.writeVoid(s -> s.persist(demande));
    }

    @Override
    public Demande update(Demande demande) {
        return HibernateTx.write(s -> s.merge(demande));
    }

    @Override
    public void delete(int id) {
        HibernateTx.writeVoid(s -> {
            Demande d = s.get(Demande.class, id);
            if (d != null) {
                s.remove(d);
            }
        });
    }

    @Override
    public List<Demande> findByHopital(int hopitalId) {
        return HibernateTx.read(s -> s.createQuery(
                        "FROM Demande d WHERE d.hopital.id = :id ORDER BY d.dateDemande DESC, d.id DESC", Demande.class)
                .setParameter("id", hopitalId)
                .list());
    }

    @Override
    public List<Demande> findOuvertesByGroupes(Collection<GroupeSang> groupes) {
        if (groupes == null || groupes.isEmpty()) {
            return List.of();
        }
        return HibernateTx.read(s -> s.createQuery(
                        "FROM Demande d WHERE d.etat = :etat AND d.groupeSanguin IN (:groupes) "
                                + "ORDER BY d.dateDemande DESC, d.id DESC", Demande.class)
                .setParameter("etat", EtatDemande.OUVERTE)
                .setParameterList("groupes", groupes)
                .list());
    }
}

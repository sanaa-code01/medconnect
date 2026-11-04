package blooddonation.dao.impl;

import blooddonation.bean.Correspondance;
import blooddonation.dao.interfaces.CorrespondanceDao;
import blooddonation.enums.StatutCorrespondance;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CorrespondanceDaoImpl implements CorrespondanceDao {

    @Override
    public Optional<Correspondance> findById(int id) {
        return HibernateTx.read(s -> Optional.ofNullable(s.get(Correspondance.class, id)));
    }

    @Override
    public void save(Correspondance correspondance) {
        HibernateTx.writeVoid(s -> s.persist(correspondance));
    }

    @Override
    public Correspondance update(Correspondance correspondance) {
        return HibernateTx.write(s -> s.merge(correspondance));
    }

    @Override
    public boolean existsByDonneurAndDemande(int donneurId, int demandeId) {
        Long count = HibernateTx.read(s -> s.createQuery(
                        "SELECT COUNT(c) FROM Correspondance c WHERE c.donneur.id = :donneur AND c.demande.id = :demande",
                        Long.class)
                .setParameter("donneur", donneurId)
                .setParameter("demande", demandeId)
                .uniqueResult());
        return count != null && count > 0;
    }

    @Override
    public List<Correspondance> findByDonneur(int donneurId) {
        return HibernateTx.read(s -> s.createQuery(
                        "FROM Correspondance c WHERE c.donneur.id = :id ORDER BY c.dateCreation DESC", Correspondance.class)
                .setParameter("id", donneurId)
                .list());
    }

    @Override
    public List<Correspondance> findByDemande(int demandeId) {
        return HibernateTx.read(s -> s.createQuery(
                        "FROM Correspondance c WHERE c.demande.id = :id ORDER BY c.dateCreation ASC", Correspondance.class)
                .setParameter("id", demandeId)
                .list());
    }

    @Override
    public List<Correspondance> findByHopital(int hopitalId) {
        return HibernateTx.read(s -> s.createQuery(
                        "FROM Correspondance c WHERE c.demande.hopital.id = :id ORDER BY c.dateCreation DESC",
                        Correspondance.class)
                .setParameter("id", hopitalId)
                .list());
    }

    @Override
    public int updateStatutByDemande(int demandeId, Collection<StatutCorrespondance> anciens,
                                     StatutCorrespondance nouveau) {
        return HibernateTx.write(s -> s.createMutationQuery(
                        "UPDATE Correspondance c SET c.statut = :nouveau "
                                + "WHERE c.demande.id = :demande AND c.statut IN (:anciens)")
                .setParameter("nouveau", nouveau)
                .setParameter("demande", demandeId)
                .setParameterList("anciens", anciens)
                .executeUpdate());
    }
}

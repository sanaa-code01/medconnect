package blooddonation.dao.impl;

import blooddonation.bean.Correspondance;
import blooddonation.bean.Demande;
import blooddonation.bean.Don;
import blooddonation.bean.Donneur;
import blooddonation.dao.interfaces.DonDao;

import java.util.List;

public class DonDaoImpl implements DonDao {

    @Override
    public List<Don> findByDonneur(int donneurId) {
        return HibernateTx.read(s -> s.createQuery(
                        "FROM Don d WHERE d.donneur.id = :id ORDER BY d.dateDon DESC", Don.class)
                .setParameter("id", donneurId)
                .list());
    }

    @Override
    public List<Don> findByHopital(int hopitalId) {
        return HibernateTx.read(s -> s.createQuery(
                        "FROM Don d WHERE d.demande.hopital.id = :id ORDER BY d.dateDon DESC", Don.class)
                .setParameter("id", hopitalId)
                .list());
    }

    @Override
    public long countByDemande(int demandeId) {
        Long count = HibernateTx.read(s -> s.createQuery(
                        "SELECT COUNT(d) FROM Don d WHERE d.demande.id = :id", Long.class)
                .setParameter("id", demandeId)
                .uniqueResult());
        return count == null ? 0 : count;
    }

    @Override
    public void enregistrer(Don don, Correspondance correspondance, Donneur donneur, Demande demande) {
        HibernateTx.writeVoid(s -> {
            Donneur d = s.merge(donneur);
            Demande dem = s.merge(demande);
            s.merge(correspondance);
            don.setDonneur(d);
            don.setDemande(dem);
            s.persist(don);
        });
    }
}

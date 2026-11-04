package blooddonation.support;

import blooddonation.bean.Correspondance;
import blooddonation.bean.Demande;
import blooddonation.bean.Don;
import blooddonation.bean.Donneur;
import blooddonation.bean.Hopital;
import blooddonation.bean.User;
import blooddonation.dao.interfaces.CorrespondanceDao;
import blooddonation.dao.interfaces.DemandeDao;
import blooddonation.dao.interfaces.DonDao;
import blooddonation.dao.interfaces.DonneurDao;
import blooddonation.dao.interfaces.HopitalDao;
import blooddonation.dao.interfaces.UserDao;
import blooddonation.enums.EtatDemande;
import blooddonation.enums.GroupeSang;
import blooddonation.enums.StatutCorrespondance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Faux DAO en mémoire : permet de tester les services sans base de données.
 * Chaque test crée son propre InMemoryStore, donc les tests sont indépendants.
 */
public class InMemoryStore {

    private final AtomicInteger sequence = new AtomicInteger(1);
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private final Map<Integer, Demande> demandes = new LinkedHashMap<>();
    private final Map<Integer, Correspondance> reponses = new LinkedHashMap<>();
    private final Map<Integer, Don> dons = new LinkedHashMap<>();

    public final UserDao userDao = new UserDao() {
        @Override
        public Optional<User> findByEmail(String email) {
            return users.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
        }

        @Override
        public Optional<User> findById(int id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public void save(User user) {
            user.setId(sequence.getAndIncrement());
            users.put(user.getId(), user);
        }
    };

    public final DonneurDao donneurDao = new DonneurDao() {
        @Override
        public Optional<Donneur> findById(int id) {
            return users.get(id) instanceof Donneur d ? Optional.of(d) : Optional.empty();
        }

        @Override
        public Donneur update(Donneur donneur) {
            users.put(donneur.getId(), donneur);
            return donneur;
        }
    };

    public final HopitalDao hopitalDao = new HopitalDao() {
        @Override
        public Optional<Hopital> findById(int id) {
            return users.get(id) instanceof Hopital h ? Optional.of(h) : Optional.empty();
        }

        @Override
        public Hopital update(Hopital hopital) {
            users.put(hopital.getId(), hopital);
            return hopital;
        }
    };

    public final DemandeDao demandeDao = new DemandeDao() {
        @Override
        public Optional<Demande> findById(int id) {
            return Optional.ofNullable(demandes.get(id));
        }

        @Override
        public void save(Demande demande) {
            demande.setId(sequence.getAndIncrement());
            demandes.put(demande.getId(), demande);
        }

        @Override
        public Demande update(Demande demande) {
            demandes.put(demande.getId(), demande);
            return demande;
        }

        @Override
        public void delete(int id) {
            demandes.remove(id);
        }

        @Override
        public List<Demande> findByHopital(int hopitalId) {
            return demandes.values().stream()
                    .filter(d -> d.getHopital().getId() == hopitalId)
                    .sorted(Comparator.comparing(Demande::getId).reversed())
                    .toList();
        }

        @Override
        public List<Demande> findOuvertesByGroupes(Collection<GroupeSang> groupes) {
            return demandes.values().stream()
                    .filter(d -> d.getEtat() == EtatDemande.OUVERTE && groupes.contains(d.getGroupeSanguin()))
                    .toList();
        }
    };

    public final CorrespondanceDao correspondanceDao = new CorrespondanceDao() {
        @Override
        public Optional<Correspondance> findById(int id) {
            return Optional.ofNullable(reponses.get(id));
        }

        @Override
        public void save(Correspondance c) {
            c.setId(sequence.getAndIncrement());
            reponses.put(c.getId(), c);
        }

        @Override
        public Correspondance update(Correspondance c) {
            reponses.put(c.getId(), c);
            return c;
        }

        @Override
        public boolean existsByDonneurAndDemande(int donneurId, int demandeId) {
            return reponses.values().stream()
                    .anyMatch(c -> c.getDonneur().getId() == donneurId && c.getDemande().getId() == demandeId);
        }

        @Override
        public List<Correspondance> findByDonneur(int donneurId) {
            return reponses.values().stream().filter(c -> c.getDonneur().getId() == donneurId).toList();
        }

        @Override
        public List<Correspondance> findByDemande(int demandeId) {
            return reponses.values().stream().filter(c -> c.getDemande().getId() == demandeId).toList();
        }

        @Override
        public List<Correspondance> findByHopital(int hopitalId) {
            return reponses.values().stream()
                    .filter(c -> c.getDemande().getHopital().getId() == hopitalId).toList();
        }

        @Override
        public int updateStatutByDemande(int demandeId, Collection<StatutCorrespondance> anciens,
                                         StatutCorrespondance nouveau) {
            int n = 0;
            for (Correspondance c : findByDemande(demandeId)) {
                if (anciens.contains(c.getStatut())) {
                    c.setStatut(nouveau);
                    n++;
                }
            }
            return n;
        }
    };

    public final DonDao donDao = new DonDao() {
        @Override
        public List<Don> findByDonneur(int donneurId) {
            return dons.values().stream().filter(d -> d.getDonneur().getId() == donneurId).toList();
        }

        @Override
        public List<Don> findByHopital(int hopitalId) {
            return dons.values().stream().filter(d -> d.getDemande().getHopital().getId() == hopitalId).toList();
        }

        @Override
        public long countByDemande(int demandeId) {
            return dons.values().stream().filter(d -> d.getDemande().getId() == demandeId).count();
        }

        @Override
        public void enregistrer(Don don, Correspondance correspondance, Donneur donneur, Demande demande) {
            users.put(donneur.getId(), donneur);
            demandes.put(demande.getId(), demande);
            reponses.put(correspondance.getId(), correspondance);
            don.setDonneur(donneur);
            don.setDemande(demande);
            don.setId(sequence.getAndIncrement());
            dons.put(don.getId(), don);
        }
    };

    public List<Don> allDons() {
        return new ArrayList<>(dons.values());
    }
}

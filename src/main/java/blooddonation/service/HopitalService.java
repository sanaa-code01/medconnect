package blooddonation.service;

import blooddonation.bean.Correspondance;
import blooddonation.bean.Demande;
import blooddonation.bean.Don;
import blooddonation.bean.Donneur;
import blooddonation.bean.Hopital;
import blooddonation.dao.interfaces.CorrespondanceDao;
import blooddonation.dao.interfaces.DemandeDao;
import blooddonation.dao.interfaces.DonDao;
import blooddonation.dao.interfaces.DonneurDao;
import blooddonation.dao.interfaces.HopitalDao;
import blooddonation.enums.EtatDemande;
import blooddonation.enums.GroupeSang;
import blooddonation.enums.StatutCorrespondance;
import blooddonation.enums.Urgence;
import blooddonation.utils.Validation;

import java.time.Clock;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Logique métier de l'espace hôpital : demandes de sang, réponses des donneurs, dons.
 * <p>
 * Chaque méthode reçoit l'id de l'hôpital connecté et vérifie que la ressource
 * lui appartient : un hôpital ne peut jamais lire ou modifier les demandes d'un autre
 * en changeant un id dans l'URL (protection contre les failles IDOR).
 */
public class HopitalService {

    public static final int QUANTITE_MAX = 50;

    public record DemandeRequest(String groupeSanguin, String quantite, String urgence, String description) { }

    public record ProfilRequest(String nomHopital, String prenom, String nom, String telephone,
                                String ville, String adresse) { }

    /** Une demande avec ses compteurs, pour les tableaux. */
    public record DemandeView(Demande demande, long enAttente, long confirmees, long donsRecus) {
        // Getters pour les JSP (EL de Tomcat 10.1 ne lit pas les accesseurs de record)
        public Demande getDemande() { return demande; }
        public long getEnAttente() { return enAttente; }
        public long getConfirmees() { return confirmees; }
        public long getDonsRecus() { return donsRecus; }

        public int getProgression() {
            int q = demande.getQuantite();
            return q <= 0 ? 0 : (int) Math.min(100, donsRecus * 100 / q);
        }
    }

    public record Detail(DemandeView demande, List<Correspondance> reponses) {
        // Getters pour les JSP (EL de Tomcat 10.1 ne lit pas les accesseurs de record)
        public DemandeView getDemande() { return demande; }
        public List<Correspondance> getReponses() { return reponses; }
    }

    public record Dashboard(Hopital hopital, long demandesOuvertes, long demandesUrgentes,
                            long reponsesEnAttente, long donsCeMois, List<DemandeView> demandesRecentes) {
        // Getters pour les JSP (EL de Tomcat 10.1 ne lit pas les accesseurs de record)
        public Hopital getHopital() { return hopital; }
        public long getDemandesOuvertes() { return demandesOuvertes; }
        public long getDemandesUrgentes() { return demandesUrgentes; }
        public long getReponsesEnAttente() { return reponsesEnAttente; }
        public long getDonsCeMois() { return donsCeMois; }
        public List<DemandeView> getDemandesRecentes() { return demandesRecentes; }
    }

    private final HopitalDao hopitalDao;
    private final DemandeDao demandeDao;
    private final CorrespondanceDao correspondanceDao;
    private final DonDao donDao;
    private final DonneurDao donneurDao;
    private final Clock clock;

    public HopitalService(HopitalDao hopitalDao, DemandeDao demandeDao, CorrespondanceDao correspondanceDao,
                          DonDao donDao, DonneurDao donneurDao, Clock clock) {
        this.hopitalDao = hopitalDao;
        this.demandeDao = demandeDao;
        this.correspondanceDao = correspondanceDao;
        this.donDao = donDao;
        this.donneurDao = donneurDao;
        this.clock = clock;
    }

    // ------------------------------------------------------------------ lecture

    public Hopital getHopital(int hopitalId) {
        return hopitalDao.findById(hopitalId)
                .orElseThrow(() -> new ServiceException("Compte hôpital introuvable."));
    }

    public Dashboard dashboard(int hopitalId) {
        Hopital hopital = getHopital(hopitalId);
        List<DemandeView> demandes = demandes(hopitalId);
        LocalDate debutMois = LocalDate.now(clock).withDayOfMonth(1);

        long ouvertes = demandes.stream().filter(v -> v.demande().getEtat() == EtatDemande.OUVERTE).count();
        long urgentes = demandes.stream()
                .filter(v -> v.demande().getEtat() == EtatDemande.OUVERTE
                        && v.demande().getUrgence() != Urgence.NORMAL)
                .count();
        long enAttente = demandes.stream().mapToLong(DemandeView::enAttente).sum();
        long donsCeMois = donDao.findByHopital(hopitalId).stream()
                .filter(d -> !d.getDateDon().isBefore(debutMois))
                .count();

        return new Dashboard(hopital, ouvertes, urgentes, enAttente, donsCeMois,
                demandes.stream().limit(5).toList());
    }

    public List<DemandeView> demandes(int hopitalId) {
        Map<Integer, List<Correspondance>> parDemande = correspondanceDao.findByHopital(hopitalId).stream()
                .collect(Collectors.groupingBy(c -> c.getDemande().getId()));
        Map<Integer, Long> donsParDemande = donDao.findByHopital(hopitalId).stream()
                .collect(Collectors.groupingBy(d -> d.getDemande().getId(), Collectors.counting()));

        return demandeDao.findByHopital(hopitalId).stream()
                .map(d -> toView(d, parDemande.getOrDefault(d.getId(), List.of()),
                        donsParDemande.getOrDefault(d.getId(), 0L)))
                .toList();
    }

    public Detail detail(int hopitalId, int demandeId) {
        Demande demande = demandeDeLHopital(hopitalId, demandeId);
        List<Correspondance> reponses = correspondanceDao.findByDemande(demandeId);
        return new Detail(toView(demande, reponses, donDao.countByDemande(demandeId)), reponses);
    }

    public Demande getDemande(int hopitalId, int demandeId) {
        return demandeDeLHopital(hopitalId, demandeId);
    }

    public List<Don> dons(int hopitalId) {
        return donDao.findByHopital(hopitalId);
    }

    // ------------------------------------------------------------------ demandes

    public Demande creerDemande(int hopitalId, DemandeRequest r) {
        Demande d = new Demande();
        d.setHopital(getHopital(hopitalId));
        appliquer(d, r);
        d.setEtat(EtatDemande.OUVERTE);
        d.setDateDemande(LocalDate.now(clock));
        demandeDao.save(d);
        return d;
    }

    public void modifierDemande(int hopitalId, int demandeId, DemandeRequest r) {
        Demande d = demandeDeLHopital(hopitalId, demandeId);
        if (d.getEtat() != EtatDemande.OUVERTE) {
            throw new ServiceException("Seule une demande ouverte peut être modifiée.");
        }
        long dons = donDao.countByDemande(demandeId);
        appliquer(d, r);
        if (d.getQuantite() < dons) {
            throw new ServiceException("La quantité ne peut pas être inférieure aux " + dons + " dons déjà reçus.");
        }
        if (dons > 0 && d.getQuantite() == dons) {
            d.setEtat(EtatDemande.SATISFAITE);
        }
        demandeDao.update(d);
    }

    /** Annule une demande ouverte et prévient les donneurs qui avaient répondu. */
    public void annulerDemande(int hopitalId, int demandeId) {
        Demande d = demandeDeLHopital(hopitalId, demandeId);
        if (d.getEtat() != EtatDemande.OUVERTE) {
            throw new ServiceException("Cette demande n'est plus ouverte.");
        }
        d.setEtat(EtatDemande.ANNULEE);
        demandeDao.update(d);
        correspondanceDao.updateStatutByDemande(demandeId,
                EnumSet.of(StatutCorrespondance.EN_ATTENTE, StatutCorrespondance.CONFIRMEE),
                StatutCorrespondance.REFUSEE);
    }

    /** Suppression définitive : autorisée seulement si aucun donneur n'a encore répondu. */
    public void supprimerDemande(int hopitalId, int demandeId) {
        demandeDeLHopital(hopitalId, demandeId);
        if (!correspondanceDao.findByDemande(demandeId).isEmpty()) {
            throw new ServiceException("Des donneurs ont déjà répondu à cette demande : annulez-la plutôt que de la supprimer.");
        }
        demandeDao.delete(demandeId);
    }

    // ------------------------------------------------------------------ réponses et dons

    public void confirmerReponse(int hopitalId, int reponseId) {
        Correspondance c = reponseDeLHopital(hopitalId, reponseId);
        if (c.getDemande().getEtat() != EtatDemande.OUVERTE) {
            throw new ServiceException("La demande n'est plus ouverte.");
        }
        if (c.getStatut() != StatutCorrespondance.EN_ATTENTE) {
            throw new ServiceException("Seule une réponse en attente peut être confirmée.");
        }
        c.setStatut(StatutCorrespondance.CONFIRMEE);
        correspondanceDao.update(c);
    }

    public void refuserReponse(int hopitalId, int reponseId) {
        Correspondance c = reponseDeLHopital(hopitalId, reponseId);
        if (c.getStatut() != StatutCorrespondance.EN_ATTENTE && c.getStatut() != StatutCorrespondance.CONFIRMEE) {
            throw new ServiceException("Cette réponse ne peut plus être refusée.");
        }
        c.setStatut(StatutCorrespondance.REFUSEE);
        correspondanceDao.update(c);
    }

    /**
     * Le donneur confirmé est venu donner : on crée le don, on met à jour sa date
     * de dernier don, et la demande passe à « Satisfaite » quand la quantité est atteinte.
     */
    public Don enregistrerDon(int hopitalId, int reponseId, String dateDon, String lieu) {
        Correspondance c = reponseDeLHopital(hopitalId, reponseId);
        if (c.getStatut() != StatutCorrespondance.CONFIRMEE) {
            throw new ServiceException("Confirmez d'abord le donneur avant d'enregistrer le don.");
        }
        LocalDate today = LocalDate.now(clock);
        LocalDate date = parseDate(dateDon, today);
        if (date.isAfter(today)) {
            throw new ServiceException("La date du don ne peut pas être dans le futur.");
        }
        if (date.isBefore(c.getDemande().getDateDemande())) {
            throw new ServiceException("La date du don ne peut pas être antérieure à la demande.");
        }

        Demande demande = c.getDemande();
        Donneur donneur = donneurDao.findById(c.getDonneur().getId())
                .orElseThrow(() -> new ServiceException("Donneur introuvable."));

        Don don = new Don();
        don.setDateDon(date);
        String lieuSaisi = Validation.optional(lieu, "Lieu", 150);
        don.setLieu(lieuSaisi != null ? lieuSaisi : demande.getHopital().getNomHopital());

        c.setStatut(StatutCorrespondance.DON_EFFECTUE);
        if (donneur.getDateDernierDon() == null || date.isAfter(donneur.getDateDernierDon())) {
            donneur.setDateDernierDon(date);
        }
        if (donDao.countByDemande(demande.getId()) + 1 >= demande.getQuantite()) {
            demande.setEtat(EtatDemande.SATISFAITE);
        }

        donDao.enregistrer(don, c, donneur, demande);
        return don;
    }

    // ------------------------------------------------------------------ profil

    public Hopital updateProfil(int hopitalId, ProfilRequest r) {
        Hopital h = getHopital(hopitalId);
        h.setNomHopital(Validation.required(r.nomHopital(), "Nom de l'hôpital", 120));
        h.setPrenom(Validation.required(r.prenom(), "Prénom du responsable", 60));
        h.setNom(Validation.required(r.nom(), "Nom du responsable", 60));
        h.setTelephone(Validation.telephone(r.telephone()));
        h.setVille(Validation.required(r.ville(), "Ville", 80));
        h.setAdresse(Validation.optional(r.adresse(), "Adresse", 200));
        return hopitalDao.update(h);
    }

    // ------------------------------------------------------------------ outils internes

    private Demande demandeDeLHopital(int hopitalId, int demandeId) {
        return demandeDao.findById(demandeId)
                .filter(d -> d.getHopital().getId() == hopitalId)
                .orElseThrow(() -> new ServiceException("Demande introuvable."));
    }

    private Correspondance reponseDeLHopital(int hopitalId, int reponseId) {
        return correspondanceDao.findById(reponseId)
                .filter(c -> c.getDemande().getHopital().getId() == hopitalId)
                .orElseThrow(() -> new ServiceException("Réponse introuvable."));
    }

    private static void appliquer(Demande d, DemandeRequest r) {
        d.setGroupeSanguin(Validation.enumValue(GroupeSang.class, r.groupeSanguin(), "Groupe sanguin"));
        d.setUrgence(Validation.enumValue(Urgence.class, r.urgence(), "Urgence"));
        d.setDescription(Validation.optional(r.description(), "Description", 500));
        int quantite;
        try {
            quantite = Integer.parseInt(Validation.required(r.quantite(), "Quantité", 3));
        } catch (NumberFormatException e) {
            throw new ServiceException("La quantité doit être un nombre.");
        }
        if (quantite < 1 || quantite > QUANTITE_MAX) {
            throw new ServiceException("La quantité doit être comprise entre 1 et " + QUANTITE_MAX + " poches.");
        }
        d.setQuantite(quantite);
    }

    private static DemandeView toView(Demande d, List<Correspondance> reponses, long donsRecus) {
        Map<StatutCorrespondance, Long> parStatut = reponses.stream()
                .collect(Collectors.groupingBy(Correspondance::getStatut, Collectors.counting()));
        Function<StatutCorrespondance, Long> nb = s -> parStatut.getOrDefault(s, 0L);
        return new DemandeView(d, nb.apply(StatutCorrespondance.EN_ATTENTE),
                nb.apply(StatutCorrespondance.CONFIRMEE), donsRecus);
    }

    private static LocalDate parseDate(String value, LocalDate defaut) {
        String v = Validation.trimToNull(value);
        if (v == null) {
            return defaut;
        }
        try {
            return LocalDate.parse(v);
        } catch (java.time.format.DateTimeParseException e) {
            throw new ServiceException("Date invalide.");
        }
    }
}

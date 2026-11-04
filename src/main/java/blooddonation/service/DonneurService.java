package blooddonation.service;

import blooddonation.bean.Correspondance;
import blooddonation.bean.Demande;
import blooddonation.bean.Don;
import blooddonation.bean.Donneur;
import blooddonation.dao.interfaces.CorrespondanceDao;
import blooddonation.dao.interfaces.DemandeDao;
import blooddonation.dao.interfaces.DonDao;
import blooddonation.dao.interfaces.DonneurDao;
import blooddonation.enums.Disponibilite;
import blooddonation.enums.EtatDemande;
import blooddonation.enums.GroupeSang;
import blooddonation.enums.StatutCorrespondance;
import blooddonation.utils.Dates;
import blooddonation.utils.Validation;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Logique métier de l'espace donneur. */
public class DonneurService {

    /** Une demande vue par un donneur, avec ce qu'il faut pour afficher le bouton « Je veux donner ». */
    public record Annonce(Demande demande, boolean dejaRepondu, boolean memeVille) {
        // Getters pour les JSP (EL de Tomcat 10.1 ne lit pas les accesseurs de record)
        public Demande getDemande() { return demande; }
        public boolean isDejaRepondu() { return dejaRepondu; }
        public boolean isMemeVille() { return memeVille; }
    }

    public record Dashboard(
            Donneur donneur,
            int totalDons,
            boolean eligible,
            String prochaineDate,
            long reponsesEnAttente,
            List<Correspondance> dernieresReponses,
            List<Annonce> annoncesPrioritaires) {
        // Getters pour les JSP (EL de Tomcat 10.1 ne lit pas les accesseurs de record)
        public Donneur getDonneur() { return donneur; }
        public int getTotalDons() { return totalDons; }
        public boolean isEligible() { return eligible; }
        public String getProchaineDate() { return prochaineDate; }
        public long getReponsesEnAttente() { return reponsesEnAttente; }
        public List<Correspondance> getDernieresReponses() { return dernieresReponses; }
        public List<Annonce> getAnnoncesPrioritaires() { return annoncesPrioritaires; }
    }

    public record ProfilRequest(String prenom, String nom, String telephone, String ville,
                                String adresse, String groupeSanguin, String disponibilite) { }

    /** Tri des annonces : les plus urgentes d'abord, puis les plus récentes. */
    static final Comparator<Demande> PAR_PRIORITE = Comparator
            .comparing((Demande d) -> d.getUrgence().ordinal())
            .thenComparing(Demande::getDateDemande, Comparator.reverseOrder());

    private final DonneurDao donneurDao;
    private final DemandeDao demandeDao;
    private final CorrespondanceDao correspondanceDao;
    private final DonDao donDao;
    private final CompatibiliteService compatibilite;
    private final EligibiliteService eligibilite;
    private final Clock clock;

    public DonneurService(DonneurDao donneurDao, DemandeDao demandeDao, CorrespondanceDao correspondanceDao,
                          DonDao donDao, CompatibiliteService compatibilite, EligibiliteService eligibilite,
                          Clock clock) {
        this.donneurDao = donneurDao;
        this.demandeDao = demandeDao;
        this.correspondanceDao = correspondanceDao;
        this.donDao = donDao;
        this.compatibilite = compatibilite;
        this.eligibilite = eligibilite;
        this.clock = clock;
    }

    public Donneur getDonneur(int donneurId) {
        return donneurDao.findById(donneurId)
                .orElseThrow(() -> new ServiceException("Compte donneur introuvable."));
    }

    public Dashboard dashboard(int donneurId) {
        Donneur donneur = getDonneur(donneurId);
        List<Correspondance> reponses = correspondanceDao.findByDonneur(donneurId);
        long enAttente = reponses.stream()
                .filter(c -> c.getStatut() == StatutCorrespondance.EN_ATTENTE
                        || c.getStatut() == StatutCorrespondance.CONFIRMEE)
                .count();
        List<Annonce> annonces = annonces(donneurId, false);
        return new Dashboard(
                donneur,
                donDao.findByDonneur(donneurId).size(),
                eligibilite.estEligible(donneur.getDateDernierDon()),
                Dates.fr(eligibilite.prochaineDatePossible(donneur.getDateDernierDon())),
                enAttente,
                reponses.stream().limit(5).toList(),
                annonces.stream().filter(a -> !a.dejaRepondu()).limit(5).toList());
    }

    /**
     * Demandes ouvertes compatibles avec le groupe sanguin du donneur
     * (pas seulement son propre groupe : un O- peut répondre à toutes les demandes).
     */
    public List<Annonce> annonces(int donneurId, boolean maVilleSeulement) {
        Donneur donneur = getDonneur(donneurId);
        if (donneur.getGroupeSanguin() == null) {
            return List.of();
        }
        Set<GroupeSang> groupes = compatibilite.receveursCompatibles(donneur.getGroupeSanguin());
        Set<Integer> dejaRepondues = correspondanceDao.findByDonneur(donneurId).stream()
                .map(c -> c.getDemande().getId())
                .collect(Collectors.toSet());

        return demandeDao.findOuvertesByGroupes(groupes).stream()
                .sorted(PAR_PRIORITE)
                .map(d -> new Annonce(d, dejaRepondues.contains(d.getId()), memeVille(donneur, d)))
                .filter(a -> !maVilleSeulement || a.memeVille())
                .toList();
    }

    /** Bouton « Je veux donner » : le donneur se propose pour une demande. */
    public void repondre(int donneurId, int demandeId) {
        Donneur donneur = getDonneur(donneurId);
        Demande demande = demandeDao.findById(demandeId)
                .orElseThrow(() -> new ServiceException("Cette demande n'existe pas."));

        if (demande.getEtat() != EtatDemande.OUVERTE) {
            throw new ServiceException("Cette demande n'est plus ouverte.");
        }
        if (donneur.getGroupeSanguin() == null) {
            throw new ServiceException("Renseignez votre groupe sanguin dans votre profil avant de répondre.");
        }
        if (!compatibilite.peutDonner(donneur.getGroupeSanguin(), demande.getGroupeSanguin())) {
            throw new ServiceException("Votre groupe sanguin n'est pas compatible avec cette demande.");
        }
        if (donneur.getDisponibilite() == Disponibilite.INDISPONIBLE) {
            throw new ServiceException("Vous êtes indiqué comme indisponible. Modifiez votre disponibilité dans votre profil.");
        }
        if (!eligibilite.estEligible(donneur.getDateDernierDon())) {
            throw new ServiceException("Vous pourrez de nouveau donner à partir du "
                    + Dates.fr(eligibilite.prochaineDatePossible(donneur.getDateDernierDon())) + ".");
        }
        if (correspondanceDao.existsByDonneurAndDemande(donneurId, demandeId)) {
            throw new ServiceException("Vous avez déjà répondu à cette demande.");
        }

        Correspondance c = new Correspondance();
        c.setDonneur(donneur);
        c.setDemande(demande);
        c.setStatut(StatutCorrespondance.EN_ATTENTE);
        c.setDateCreation(LocalDateTime.now(clock));
        correspondanceDao.save(c);
    }

    public List<Correspondance> mesReponses(int donneurId) {
        return correspondanceDao.findByDonneur(donneurId);
    }

    public List<Don> historique(int donneurId) {
        return donDao.findByDonneur(donneurId);
    }

    public Donneur updateProfil(int donneurId, ProfilRequest r) {
        Donneur d = getDonneur(donneurId);
        d.setPrenom(Validation.required(r.prenom(), "Prénom", 60));
        d.setNom(Validation.required(r.nom(), "Nom", 60));
        d.setTelephone(Validation.telephone(r.telephone()));
        d.setVille(Validation.required(r.ville(), "Ville", 80));
        d.setAdresse(Validation.optional(r.adresse(), "Adresse", 200));
        d.setGroupeSanguin(Validation.enumValue(GroupeSang.class, r.groupeSanguin(), "Groupe sanguin"));
        d.setDisponibilite(Validation.enumValue(Disponibilite.class, r.disponibilite(), "Disponibilité"));
        return donneurDao.update(d);
    }

    public boolean estEligible(Donneur donneur) {
        return eligibilite.estEligible(donneur.getDateDernierDon());
    }

    /** Groupes des demandes auxquelles ce donneur peut répondre. */
    public java.util.Set<GroupeSang> groupesCompatibles(Donneur donneur) {
        return donneur.getGroupeSanguin() == null
                ? java.util.Set.of()
                : compatibilite.receveursCompatibles(donneur.getGroupeSanguin());
    }

    /** Date du prochain don possible, pour l'afficher dans le profil. */
    public LocalDate prochaineDatePossible(Donneur donneur) {
        return eligibilite.prochaineDatePossible(donneur.getDateDernierDon());
    }

    private static boolean memeVille(Donneur donneur, Demande demande) {
        String v1 = donneur.getVille();
        String v2 = demande.getHopital().getVille();
        return v1 != null && v2 != null && v1.trim().equalsIgnoreCase(v2.trim());
    }
}

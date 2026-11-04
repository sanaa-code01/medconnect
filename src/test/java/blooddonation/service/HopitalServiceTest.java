package blooddonation.service;

import blooddonation.bean.Correspondance;
import blooddonation.bean.Demande;
import blooddonation.bean.Donneur;
import blooddonation.bean.Hopital;
import blooddonation.enums.EtatDemande;
import blooddonation.enums.StatutCorrespondance;
import blooddonation.service.HopitalService.DemandeRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HopitalServiceTest {

    private final Fixture f = new Fixture();
    private final Hopital hopital = f.hopital("Agadir");
    private final Hopital autreHopital = f.hopital("Agadir");

    private Correspondance reponse(Donneur d, Demande demande) {
        f.donneurs.repondre(d.getId(), demande.getId());
        return f.store.correspondanceDao.findByDemande(demande.getId()).stream()
                .filter(c -> c.getDonneur().getId().equals(d.getId())).findFirst().orElseThrow();
    }

    @Test
    void creationDeDemandeValidee() {
        Demande d = f.demande(hopital, "B_MOINS", 3, "URGENT");
        assertEquals(EtatDemande.OUVERTE, d.getEtat());
        assertEquals(Fixture.TODAY, d.getDateDemande());

        assertThrows(ServiceException.class, () -> f.hopitaux.creerDemande(hopital.getId(),
                new DemandeRequest("B_MOINS", "0", "URGENT", null)));
        assertThrows(ServiceException.class, () -> f.hopitaux.creerDemande(hopital.getId(),
                new DemandeRequest("B_MOINS", "abc", "URGENT", null)));
        assertThrows(ServiceException.class, () -> f.hopitaux.creerDemande(hopital.getId(),
                new DemandeRequest("Z_PLUS", "1", "URGENT", null)));
    }

    @Test
    void unHopitalNePeutPasToucherAuxDemandesDUnAutre() {
        Demande d = f.demande(hopital, "A_PLUS", 1, "NORMAL");
        Correspondance c = reponse(f.donneur("A_PLUS", "Agadir"), d);
        int autre = autreHopital.getId();

        assertThrows(ServiceException.class, () -> f.hopitaux.detail(autre, d.getId()));
        assertThrows(ServiceException.class, () -> f.hopitaux.modifierDemande(autre, d.getId(),
                new DemandeRequest("A_PLUS", "2", "NORMAL", null)));
        assertThrows(ServiceException.class, () -> f.hopitaux.annulerDemande(autre, d.getId()));
        assertThrows(ServiceException.class, () -> f.hopitaux.confirmerReponse(autre, c.getId()));
        assertThrows(ServiceException.class, () -> f.hopitaux.enregistrerDon(autre, c.getId(), null, null));
        assertEquals(EtatDemande.OUVERTE, d.getEtat());
        assertEquals(StatutCorrespondance.EN_ATTENTE, c.getStatut());
    }

    @Test
    void parcoursCompletJusquALaDemandeSatisfaite() {
        Demande d = f.demande(hopital, "O_PLUS", 2, "CRITIQUE");
        Donneur d1 = f.donneur("O_PLUS", "Agadir");
        Donneur d2 = f.donneur("O_MOINS", "Agadir");
        Correspondance c1 = reponse(d1, d);
        Correspondance c2 = reponse(d2, d);

        f.hopitaux.confirmerReponse(hopital.getId(), c1.getId());
        f.hopitaux.enregistrerDon(hopital.getId(), c1.getId(), "2026-11-04", "");
        assertEquals(StatutCorrespondance.DON_EFFECTUE, c1.getStatut());
        assertEquals(Fixture.TODAY, f.donneurs.getDonneur(d1.getId()).getDateDernierDon());
        assertEquals(EtatDemande.OUVERTE, d.getEtat(), "1 don sur 2 : la demande reste ouverte");
        assertEquals(hopital.getNomHopital(), f.store.allDons().get(0).getLieu(), "lieu par défaut = hôpital");

        f.hopitaux.confirmerReponse(hopital.getId(), c2.getId());
        f.hopitaux.enregistrerDon(hopital.getId(), c2.getId(), null, "Centre de transfusion");
        assertEquals(EtatDemande.SATISFAITE, d.getEtat(), "2 dons sur 2 : demande satisfaite");
        assertEquals(2, f.hopitaux.dons(hopital.getId()).size());
        assertEquals(50, new HopitalService.DemandeView(d, 0, 0, 1).getProgression());
    }

    @Test
    void onNEnregistrePasUnDonSansConfirmationNiDansLeFutur() {
        Demande d = f.demande(hopital, "A_PLUS", 1, "NORMAL");
        Correspondance c = reponse(f.donneur("A_PLUS", "Agadir"), d);

        assertThrows(ServiceException.class, () -> f.hopitaux.enregistrerDon(hopital.getId(), c.getId(), null, null));
        f.hopitaux.confirmerReponse(hopital.getId(), c.getId());
        assertThrows(ServiceException.class,
                () -> f.hopitaux.enregistrerDon(hopital.getId(), c.getId(), "2026-11-05", null));
        assertThrows(ServiceException.class,
                () -> f.hopitaux.enregistrerDon(hopital.getId(), c.getId(), "05/11/2026", null));
    }

    @Test
    void annulerUneDemandePrevientLesDonneurs() {
        Demande d = f.demande(hopital, "A_PLUS", 2, "URGENT");
        Correspondance c = reponse(f.donneur("A_PLUS", "Agadir"), d);

        f.hopitaux.annulerDemande(hopital.getId(), d.getId());

        assertEquals(EtatDemande.ANNULEE, d.getEtat());
        assertEquals(StatutCorrespondance.REFUSEE, c.getStatut());
        assertThrows(ServiceException.class, () -> f.hopitaux.confirmerReponse(hopital.getId(), c.getId()));
    }

    @Test
    void suppressionInterditeSiDesDonneursOntRepondu() {
        Demande vide = f.demande(hopital, "A_PLUS", 1, "NORMAL");
        Demande avecReponse = f.demande(hopital, "A_PLUS", 1, "NORMAL");
        reponse(f.donneur("A_PLUS", "Agadir"), avecReponse);

        f.hopitaux.supprimerDemande(hopital.getId(), vide.getId());
        assertTrue(f.store.demandeDao.findById(vide.getId()).isEmpty());
        assertThrows(ServiceException.class, () -> f.hopitaux.supprimerDemande(hopital.getId(), avecReponse.getId()));
    }

    @Test
    void tableauDeBord() {
        f.demande(hopital, "A_PLUS", 1, "CRITIQUE");
        Demande normale = f.demande(hopital, "A_PLUS", 1, "NORMAL");
        reponse(f.donneur("A_PLUS", "Agadir"), normale);

        HopitalService.Dashboard dash = f.hopitaux.dashboard(hopital.getId());
        assertEquals(2, dash.demandesOuvertes());
        assertEquals(1, dash.demandesUrgentes());
        assertEquals(1, dash.reponsesEnAttente());
        assertEquals(0, dash.donsCeMois());
    }
}

package blooddonation.service;

import blooddonation.bean.Demande;
import blooddonation.bean.Donneur;
import blooddonation.bean.Hopital;
import blooddonation.enums.EtatDemande;
import blooddonation.enums.StatutCorrespondance;
import blooddonation.service.DonneurService.Annonce;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DonneurServiceTest {

    private final Fixture f = new Fixture();
    private final Hopital agadir = f.hopital("Agadir");
    private final Hopital casa = f.hopital("Casablanca");

    @Test
    void lesAnnoncesNeMontrentQueLesDemandesCompatiblesTrieesParUrgence() {
        Donneur aPlus = f.donneur("A_PLUS", "Agadir");
        Demande normaleA = f.demande(agadir, "A_PLUS", 1, "NORMAL");
        Demande critiqueAb = f.demande(agadir, "AB_PLUS", 1, "CRITIQUE");
        f.demande(agadir, "O_PLUS", 1, "CRITIQUE"); // A+ ne peut pas donner à O+

        List<Annonce> annonces = f.donneurs.annonces(aPlus.getId(), false);

        assertEquals(List.of(critiqueAb.getId(), normaleA.getId()),
                annonces.stream().map(a -> a.demande().getId()).toList());
    }

    @Test
    void filtreMaVille() {
        Donneur o = f.donneur("O_MOINS", "Agadir");
        Demande locale = f.demande(agadir, "B_PLUS", 1, "URGENT");
        f.demande(casa, "B_PLUS", 1, "URGENT");

        List<Annonce> annonces = f.donneurs.annonces(o.getId(), true);
        assertEquals(1, annonces.size());
        assertEquals(locale.getId(), annonces.get(0).demande().getId());
    }

    @Test
    void jeVeuxDonnerCreeUneReponseEnAttente() {
        Donneur o = f.donneur("O_MOINS", "Agadir");
        Demande d = f.demande(agadir, "A_MOINS", 2, "URGENT");

        f.donneurs.repondre(o.getId(), d.getId());

        assertEquals(1, f.donneurs.mesReponses(o.getId()).size());
        assertEquals(StatutCorrespondance.EN_ATTENTE, f.donneurs.mesReponses(o.getId()).get(0).getStatut());
        assertTrue(f.donneurs.annonces(o.getId(), false).get(0).dejaRepondu());
    }

    @Test
    void onNePeutPasRepondreDeuxFois() {
        Donneur o = f.donneur("O_MOINS", "Agadir");
        Demande d = f.demande(agadir, "A_MOINS", 2, "URGENT");
        f.donneurs.repondre(o.getId(), d.getId());

        ServiceException e = assertThrows(ServiceException.class, () -> f.donneurs.repondre(o.getId(), d.getId()));
        assertTrue(e.getMessage().contains("déjà répondu"));
    }

    @Test
    void groupeIncompatibleRefuse() {
        Donneur aPlus = f.donneur("A_PLUS", "Agadir");
        Demande d = f.demande(agadir, "O_MOINS", 1, "CRITIQUE");
        assertThrows(ServiceException.class, () -> f.donneurs.repondre(aPlus.getId(), d.getId()));
    }

    @Test
    void donneurPasEncoreEligibleRefuse() {
        Donneur o = f.donneur("O_MOINS", "Agadir");
        o.setDateDernierDon(Fixture.TODAY.minusDays(30));
        Demande d = f.demande(agadir, "O_MOINS", 1, "CRITIQUE");

        ServiceException e = assertThrows(ServiceException.class, () -> f.donneurs.repondre(o.getId(), d.getId()));
        assertTrue(e.getMessage().contains("de nouveau donner"));
    }

    @Test
    void demandeFermeeRefusee() {
        Donneur o = f.donneur("O_MOINS", "Agadir");
        Demande d = f.demande(agadir, "O_MOINS", 1, "CRITIQUE");
        d.setEtat(EtatDemande.ANNULEE);
        assertThrows(ServiceException.class, () -> f.donneurs.repondre(o.getId(), d.getId()));
        assertFalse(f.donneurs.annonces(o.getId(), false).stream().anyMatch(a -> a.demande().getId().equals(d.getId())));
    }
}

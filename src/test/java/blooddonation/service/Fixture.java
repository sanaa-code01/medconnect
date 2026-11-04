package blooddonation.service;

import blooddonation.bean.Demande;
import blooddonation.bean.Donneur;
import blooddonation.bean.Hopital;
import blooddonation.security.PasswordHasher;
import blooddonation.service.AuthService.RegistrationRequest;
import blooddonation.service.HopitalService.DemandeRequest;
import blooddonation.support.InMemoryStore;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/** Construit les vrais services au-dessus des faux DAO, avec une date fixe. */
class Fixture {

    static final LocalDate TODAY = LocalDate.of(2026, 11, 4);
    static final String PASSWORD = "Secret123";

    final Clock clock = Clock.fixed(Instant.parse("2026-11-04T10:00:00Z"), ZoneOffset.UTC);
    final InMemoryStore store = new InMemoryStore();
    final AuthService auth = new AuthService(store.userDao, new PasswordHasher(1_000), clock);
    final DonneurService donneurs = new DonneurService(store.donneurDao, store.demandeDao, store.correspondanceDao,
            store.donDao, new CompatibiliteService(), new EligibiliteService(clock), clock);
    final HopitalService hopitaux = new HopitalService(store.hopitalDao, store.demandeDao, store.correspondanceDao,
            store.donDao, store.donneurDao, clock);

    private int counter;

    Donneur donneur(String groupe, String ville) {
        counter++;
        return (Donneur) auth.register(new RegistrationRequest("DONNEUR", "Prenom", "Donneur" + counter,
                "donneur" + counter + "@test.ma", "0612345678", ville, null, groupe, null, PASSWORD, PASSWORD));
    }

    Hopital hopital(String ville) {
        counter++;
        return (Hopital) auth.register(new RegistrationRequest("HOPITAL", "Resp", "Onsable" + counter,
                "hopital" + counter + "@test.ma", "0528000000", ville, "Adresse", null, "Hôpital " + counter,
                PASSWORD, PASSWORD));
    }

    Demande demande(Hopital h, String groupe, int quantite, String urgence) {
        return hopitaux.creerDemande(h.getId(), new DemandeRequest(groupe, String.valueOf(quantite), urgence, "test"));
    }
}

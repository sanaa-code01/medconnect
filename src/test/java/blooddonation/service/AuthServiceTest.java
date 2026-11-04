package blooddonation.service;

import blooddonation.bean.Donneur;
import blooddonation.bean.User;
import blooddonation.enums.GroupeSang;
import blooddonation.enums.Role;
import blooddonation.service.AuthService.RegistrationRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {

    private final Fixture f = new Fixture();

    private RegistrationRequest donneur(String email, String password) {
        return new RegistrationRequest("DONNEUR", "Sanae", "Daoud", email, "0612345678", "Agadir",
                null, "O_MOINS", null, password, password);
    }

    @Test
    void inscriptionDonneurPuisConnexion() {
        User u = f.auth.register(donneur("Sanae@Test.ma", "Secret123"));

        Donneur d = assertInstanceOf(Donneur.class, u);
        assertEquals(Role.DONNEUR, d.getRole());
        assertEquals(GroupeSang.O_MOINS, d.getGroupeSanguin());
        assertEquals("sanae@test.ma", d.getEmail(), "l'e-mail est normalisé en minuscules");
        assertFalse(d.getPasswordHash().contains("Secret123"), "le mot de passe n'est jamais stocké en clair");

        assertTrue(f.auth.authenticate("SANAE@test.ma", "Secret123").isPresent());
    }

    @Test
    void connexionRefuseeAvecMauvaisMotDePasseOuEmailInconnu() {
        f.auth.register(donneur("a@test.ma", "Secret123"));
        assertTrue(f.auth.authenticate("a@test.ma", "mauvais123").isEmpty());
        assertTrue(f.auth.authenticate("inconnu@test.ma", "Secret123").isEmpty());
        assertTrue(f.auth.authenticate(null, null).isEmpty());
    }

    @Test
    void emailDejaUtilise() {
        f.auth.register(donneur("a@test.ma", "Secret123"));
        ServiceException e = assertThrows(ServiceException.class, () -> f.auth.register(donneur("A@test.ma", "Secret123")));
        assertTrue(e.getMessage().contains("existe déjà"));
    }

    @Test
    void motDePasseTropFaibleOuConfirmationDifferente() {
        assertThrows(ServiceException.class, () -> f.auth.register(donneur("a@test.ma", "court1")));
        assertThrows(ServiceException.class, () -> f.auth.register(donneur("a@test.ma", "sanschiffre")));
        assertThrows(ServiceException.class, () -> f.auth.register(new RegistrationRequest("DONNEUR", "A", "B",
                "a@test.ma", null, "Agadir", null, "O_MOINS", null, "Secret123", "Secret124")));
    }

    @Test
    void roleInconnuRefuse() {
        // Impossible de se créer un compte avec un rôle non prévu en modifiant le formulaire
        assertThrows(ServiceException.class, () -> f.auth.register(new RegistrationRequest("ADMIN", "A", "B",
                "a@test.ma", null, "Agadir", null, null, null, "Secret123", "Secret123")));
    }

    @Test
    void groupeSanguinObligatoirePourUnDonneur() {
        assertThrows(ServiceException.class, () -> f.auth.register(new RegistrationRequest("DONNEUR", "A", "B",
                "a@test.ma", null, "Agadir", null, "", null, "Secret123", "Secret123")));
    }
}

package blooddonation.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    private final PasswordHasher hasher = new PasswordHasher(1_000);

    @Test
    void leBonMotDePasseEstAccepte() {
        String hash = hasher.hash("Secret123");
        assertTrue(hasher.verify("Secret123", hash));
    }

    @Test
    void unMauvaisMotDePasseEstRefuse() {
        String hash = hasher.hash("Secret123");
        assertFalse(hasher.verify("secret123", hash));
        assertFalse(hasher.verify("", hash));
        assertFalse(hasher.verify(null, hash));
    }

    @Test
    void leHashNeContientPasLeMotDePasseEtUtiliseUnSelAleatoire() {
        String h1 = hasher.hash("Secret123");
        String h2 = hasher.hash("Secret123");
        assertFalse(h1.contains("Secret123"));
        assertNotEquals(h1, h2, "deux hash du même mot de passe doivent être différents (sel)");
    }

    @Test
    void unHashMalFormeEstRefuseSansException() {
        assertFalse(hasher.verify("Secret123", "U2VjcmV0MTIz")); // ancien format Base64
        assertFalse(hasher.verify("Secret123", "pbkdf2_sha256$abc$$"));
        assertFalse(hasher.verify("Secret123", null));
    }

    @Test
    void leNombreDIterationsEstLuDepuisLeHash() {
        String ancien = new PasswordHasher(500).hash("Secret123");
        assertTrue(hasher.verify("Secret123", ancien));
    }
}

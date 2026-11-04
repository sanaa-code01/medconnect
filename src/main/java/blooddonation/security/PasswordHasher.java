package blooddonation.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hachage des mots de passe avec PBKDF2-HMAC-SHA256 (JDK, aucune dépendance).
 * <p>
 * Format stocké : {@code pbkdf2_sha256$<iterations>$<sel base64>$<hash base64>}.
 * Le sel est aléatoire pour chaque mot de passe, et le nombre d'itérations est
 * stocké avec le hash, ce qui permet de l'augmenter plus tard sans casser les
 * comptes existants.
 */
public class PasswordHasher {

    /** Valeur recommandée par l'OWASP pour PBKDF2-HMAC-SHA256. */
    public static final int DEFAULT_ITERATIONS = 600_000;

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String PREFIX = "pbkdf2_sha256";
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;

    private final SecureRandom random = new SecureRandom();
    private final int iterations;

    public PasswordHasher() {
        this(DEFAULT_ITERATIONS);
    }

    /** Constructeur utilisé par les tests (moins d'itérations = tests rapides). */
    public PasswordHasher(int iterations) {
        if (iterations < 1) {
            throw new IllegalArgumentException("iterations doit être positif");
        }
        this.iterations = iterations;
    }

    public String hash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        byte[] hash = pbkdf2(password, salt, iterations);
        Base64.Encoder b64 = Base64.getEncoder();
        return PREFIX + "$" + iterations + "$" + b64.encodeToString(salt) + "$" + b64.encodeToString(hash);
    }

    public boolean verify(String password, String stored) {
        if (password == null || stored == null) {
            return false;
        }
        String[] parts = stored.split("\\$");
        if (parts.length != 4 || !PREFIX.equals(parts[0])) {
            return false;
        }
        try {
            int storedIterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = pbkdf2(password, salt, storedIterations);
            // Comparaison en temps constant (évite les attaques temporelles)
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] pbkdf2(String password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, HASH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 indisponible", e);
        } finally {
            spec.clearPassword();
        }
    }
}

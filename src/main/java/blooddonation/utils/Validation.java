package blooddonation.utils;

import blooddonation.service.ServiceException;

import java.util.regex.Pattern;

/** Validation des données saisies dans les formulaires (côté serveur). */
public final class Validation {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern TELEPHONE = Pattern.compile("^\\+?[0-9 ]{8,20}$");

    private Validation() { }

    /** Retourne la valeur nettoyée (trim) ou lève une erreur si elle est vide ou trop longue. */
    public static String required(String value, String champ, int maxLength) {
        String v = trimToNull(value);
        if (v == null) {
            throw new ServiceException("Le champ « " + champ + " » est obligatoire.");
        }
        if (v.length() > maxLength) {
            throw new ServiceException("Le champ « " + champ + " » ne doit pas dépasser " + maxLength + " caractères.");
        }
        return v;
    }

    /** Champ facultatif : null si vide, erreur si trop long. */
    public static String optional(String value, String champ, int maxLength) {
        String v = trimToNull(value);
        if (v != null && v.length() > maxLength) {
            throw new ServiceException("Le champ « " + champ + " » ne doit pas dépasser " + maxLength + " caractères.");
        }
        return v;
    }

    public static String email(String value) {
        String v = required(value, "E-mail", 120).toLowerCase();
        if (!EMAIL.matcher(v).matches()) {
            throw new ServiceException("Adresse e-mail invalide.");
        }
        return v;
    }

    public static String telephone(String value) {
        String v = optional(value, "Téléphone", 20);
        if (v != null && !TELEPHONE.matcher(v).matches()) {
            throw new ServiceException("Numéro de téléphone invalide.");
        }
        return v;
    }

    /** Au moins 8 caractères, dont au moins une lettre et un chiffre. */
    public static void motDePasse(String password, String confirmation) {
        if (password == null || password.length() < 8
                || !password.matches(".*[A-Za-z].*") || !password.matches(".*[0-9].*")) {
            throw new ServiceException("Le mot de passe doit contenir au moins 8 caractères, dont une lettre et un chiffre.");
        }
        if (password.length() > 128) {
            throw new ServiceException("Le mot de passe est trop long.");
        }
        if (!password.equals(confirmation)) {
            throw new ServiceException("Les deux mots de passe ne correspondent pas.");
        }
    }

    public static <E extends Enum<E>> E enumValue(Class<E> type, String value, String champ) {
        String v = trimToNull(value);
        if (v == null) {
            throw new ServiceException("Le champ « " + champ + " » est obligatoire.");
        }
        try {
            return Enum.valueOf(type, v);
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Valeur invalide pour « " + champ + " ».");
        }
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String v = value.trim();
        return v.isEmpty() ? null : v;
    }
}

package blooddonation.utils;

/** Lecture d'une variable d'environnement, puis d'une propriété système (-DNOM=...). */
public final class Env {

    private Env() { }

    public static String get(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            value = System.getProperty(name);
        }
        return (value == null || value.isBlank()) ? null : value;
    }

    public static boolean isTrue(String name) {
        return "true".equalsIgnoreCase(get(name));
    }
}

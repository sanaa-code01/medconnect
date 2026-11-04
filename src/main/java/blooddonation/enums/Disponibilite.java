package blooddonation.enums;

/** Disponibilité déclarée par le donneur. */
public enum Disponibilite {
    DISPONIBLE("Disponible"),
    INDISPONIBLE("Indisponible");

    private final String label;

    Disponibilite(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }
}

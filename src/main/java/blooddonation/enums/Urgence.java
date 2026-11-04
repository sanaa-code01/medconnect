package blooddonation.enums;

/** Niveau d'urgence d'une demande, du plus urgent au moins urgent. */
public enum Urgence {
    CRITIQUE("Critique", "danger"),
    URGENT("Urgent", "warning"),
    NORMAL("Normal", "secondary");

    private final String label;
    private final String badge;

    Urgence(String label, String badge) {
        this.label = label;
        this.badge = badge;
    }

    public String getLabel() { return label; }
    /** Classe Bootstrap utilisée pour le badge (bg-danger, bg-warning...). */
    public String getBadge() { return badge; }
}

package blooddonation.enums;

/** Statut de la réponse d'un donneur à une demande. */
public enum StatutCorrespondance {
    EN_ATTENTE("En attente", "warning"),
    CONFIRMEE("Confirmée", "info"),
    REFUSEE("Refusée", "secondary"),
    DON_EFFECTUE("Don effectué", "success");

    private final String label;
    private final String badge;

    StatutCorrespondance(String label, String badge) {
        this.label = label;
        this.badge = badge;
    }

    public String getLabel() { return label; }
    public String getBadge() { return badge; }
}

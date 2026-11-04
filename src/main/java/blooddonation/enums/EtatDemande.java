package blooddonation.enums;

/** Cycle de vie d'une demande de sang. */
public enum EtatDemande {
    OUVERTE("Ouverte", "primary"),
    SATISFAITE("Satisfaite", "success"),
    ANNULEE("Annulée", "secondary");

    private final String label;
    private final String badge;

    EtatDemande(String label, String badge) {
        this.label = label;
        this.badge = badge;
    }

    public String getLabel() { return label; }
    public String getBadge() { return badge; }
}

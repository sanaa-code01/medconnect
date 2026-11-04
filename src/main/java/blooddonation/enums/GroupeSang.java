package blooddonation.enums;

/** Groupes sanguins (système ABO + rhésus). */
public enum GroupeSang {
    A_PLUS("A+", "A", true),
    A_MOINS("A-", "A", false),
    B_PLUS("B+", "B", true),
    B_MOINS("B-", "B", false),
    AB_PLUS("AB+", "AB", true),
    AB_MOINS("AB-", "AB", false),
    O_PLUS("O+", "O", true),
    O_MOINS("O-", "O", false);

    private final String label;
    private final String abo;
    private final boolean rhesusPositif;

    GroupeSang(String label, String abo, boolean rhesusPositif) {
        this.label = label;
        this.abo = abo;
        this.rhesusPositif = rhesusPositif;
    }

    public String getLabel() { return label; }
    public String getAbo() { return abo; }
    public boolean isRhesusPositif() { return rhesusPositif; }
}

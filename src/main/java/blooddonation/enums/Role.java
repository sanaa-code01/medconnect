package blooddonation.enums;

/** Type de compte. Chaque rôle a son propre espace dans l'application. */
public enum Role {
    DONNEUR("/donneur/dashboard"),
    HOPITAL("/hopital/dashboard");

    private final String dashboardPath;

    Role(String dashboardPath) {
        this.dashboardPath = dashboardPath;
    }

    public String getDashboardPath() {
        return dashboardPath;
    }
}

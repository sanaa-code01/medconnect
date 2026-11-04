package blooddonation.security;

import blooddonation.bean.Hopital;
import blooddonation.bean.User;
import blooddonation.enums.Role;

import java.io.Serializable;

/**
 * Informations minimales gardées en session après la connexion.
 * On ne met pas l'entité Hibernate en session : elle deviendrait vite
 * obsolète et contient le hash du mot de passe.
 */
public final class SessionUser implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SESSION_KEY = "authUser";

    private final int id;
    private final Role role;
    private final String displayName;

    public SessionUser(int id, Role role, String displayName) {
        this.id = id;
        this.role = role;
        this.displayName = displayName;
    }

    public static SessionUser from(User user) {
        String name = (user instanceof Hopital h && h.getNomHopital() != null)
                ? h.getNomHopital()
                : user.getNomComplet();
        return new SessionUser(user.getId(), user.getRole(), name);
    }

    public int getId() { return id; }
    public Role getRole() { return role; }
    public String getDisplayName() { return displayName; }
}

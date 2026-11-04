package blooddonation.service;

import blooddonation.enums.GroupeSang;

import java.util.EnumSet;
import java.util.Set;

/**
 * Règles de compatibilité des globules rouges (système ABO + rhésus).
 * <ul>
 *   <li>ABO : O donne à tous, A donne à A et AB, B donne à B et AB, AB donne à AB.</li>
 *   <li>Rhésus : un donneur négatif donne à tous, un donneur positif seulement aux positifs.</li>
 * </ul>
 */
public class CompatibiliteService {

    public boolean peutDonner(GroupeSang donneur, GroupeSang receveur) {
        if (donneur == null || receveur == null) {
            return false;
        }
        return aboCompatible(donneur.getAbo(), receveur.getAbo())
                && (!donneur.isRhesusPositif() || receveur.isRhesusPositif());
    }

    /** Groupes des demandes auxquelles ce donneur peut répondre. */
    public Set<GroupeSang> receveursCompatibles(GroupeSang donneur) {
        Set<GroupeSang> result = EnumSet.noneOf(GroupeSang.class);
        for (GroupeSang receveur : GroupeSang.values()) {
            if (peutDonner(donneur, receveur)) {
                result.add(receveur);
            }
        }
        return result;
    }

    private static boolean aboCompatible(String donneur, String receveur) {
        return switch (donneur) {
            case "O" -> true;
            case "A" -> receveur.equals("A") || receveur.equals("AB");
            case "B" -> receveur.equals("B") || receveur.equals("AB");
            case "AB" -> receveur.equals("AB");
            default -> false;
        };
    }
}

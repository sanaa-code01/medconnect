package blooddonation.service;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Délai minimum entre deux dons de sang total.
 * Règle simplifiée pour l'application : 90 jours (à adapter selon le
 * règlement du centre de transfusion).
 */
public class EligibiliteService {

    public static final int DELAI_MIN_JOURS = 90;

    private final Clock clock;

    public EligibiliteService(Clock clock) {
        this.clock = clock;
    }

    public LocalDate aujourdHui() {
        return LocalDate.now(clock);
    }

    public boolean estEligible(LocalDate dernierDon) {
        return dernierDon == null || !prochaineDatePossible(dernierDon).isAfter(aujourdHui());
    }

    /** Date à partir de laquelle le donneur peut redonner (aujourd'hui s'il n'a jamais donné). */
    public LocalDate prochaineDatePossible(LocalDate dernierDon) {
        return dernierDon == null ? aujourdHui() : dernierDon.plusDays(DELAI_MIN_JOURS);
    }
}

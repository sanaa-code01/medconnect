package blooddonation.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EligibiliteServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 4);
    private final EligibiliteService service = new EligibiliteService(
            Clock.fixed(Instant.parse("2026-11-04T10:00:00Z"), ZoneOffset.UTC));

    @Test
    void unDonneurQuiNAJamaisDonneEstEligible() {
        assertTrue(service.estEligible(null));
        assertEquals(TODAY, service.prochaineDatePossible(null));
    }

    @Test
    void ilFautAttendreLeDelaiMinimum() {
        LocalDate recent = TODAY.minusDays(EligibiliteService.DELAI_MIN_JOURS - 1);
        assertFalse(service.estEligible(recent));
        assertEquals(TODAY.plusDays(1), service.prochaineDatePossible(recent));
    }

    @Test
    void leJourExactDuDelaiLeDonneurRedevientEligible() {
        assertTrue(service.estEligible(TODAY.minusDays(EligibiliteService.DELAI_MIN_JOURS)));
    }
}

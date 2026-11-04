package blooddonation.service;

import blooddonation.enums.GroupeSang;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.EnumSet;

import static blooddonation.enums.GroupeSang.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompatibiliteServiceTest {

    private final CompatibiliteService service = new CompatibiliteService();

    @Test
    void oMoinsEstDonneurUniversel() {
        assertEquals(EnumSet.allOf(GroupeSang.class), service.receveursCompatibles(O_MOINS));
    }

    @Test
    void abPlusNeDonneQuAAbPlus() {
        assertEquals(EnumSet.of(AB_PLUS), service.receveursCompatibles(AB_PLUS));
    }

    @Test
    void abPlusEstReceveurUniversel() {
        for (GroupeSang donneur : GroupeSang.values()) {
            assertTrue(service.peutDonner(donneur, AB_PLUS), donneur + " -> AB+");
        }
    }

    @ParameterizedTest(name = "{0} peut donner à {1} groupes")
    @CsvSource({"O_MOINS,8", "O_PLUS,4", "A_MOINS,4", "A_PLUS,2", "B_MOINS,4", "B_PLUS,2", "AB_MOINS,2", "AB_PLUS,1"})
    void nombreDeReceveursCompatibles(GroupeSang donneur, int attendu) {
        assertEquals(attendu, service.receveursCompatibles(donneur).size());
    }

    @Test
    void casIncompatibles() {
        assertFalse(service.peutDonner(A_PLUS, O_PLUS), "A ne donne pas à O");
        assertFalse(service.peutDonner(O_PLUS, O_MOINS), "rhésus + ne donne pas à rhésus -");
        assertFalse(service.peutDonner(B_MOINS, A_MOINS), "B ne donne pas à A");
        assertFalse(service.peutDonner(null, A_PLUS));
    }
}

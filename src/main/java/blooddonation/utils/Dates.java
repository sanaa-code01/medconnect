package blooddonation.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Formatage des dates pour l'affichage (JSTL ne sait pas formater java.time). */
public final class Dates {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_HEURE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Dates() { }

    public static String fr(LocalDate date) {
        return date == null ? "" : date.format(DATE);
    }

    public static String fr(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATE_HEURE);
    }
}

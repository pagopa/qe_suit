package it.pagopa.send.web.legal_notification.infrastructure;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Conversioni tra le date del dominio e i formati del portale della PA: le date dei filtri di ricerca e quelle delle voci
 * della cronologia.
 */
@Component
public class WebPortalDateMapper {

    private static final Set<String> DATE_FILTERS = Set.of("startDate", "endDate");
    private static final DateTimeFormatter SEARCH_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String TIMELINE_DATE_PATTERN = "dd MMM, HH:mm";

    /**
     * Filtri di ricerca con le date ({@code startDate}, {@code endDate}) convertite da ISO ({@code aaaa-mm-gg}) al
     * formato dei campi del portale ({@code gg/mm/aaaa}); gli altri filtri restano invariati.
     */
    public Map<String, String> toSearchFilters(Map<String, String> filters) {
        Map<String, String> portalFilters = new LinkedHashMap<>();
        filters.forEach((key, value) -> portalFilters.put(key,
                DATE_FILTERS.contains(key) ? LocalDate.parse(value).format(SEARCH_DATE_FORMAT) : value));
        return portalFilters;
    }

    /**
     * Data di una voce della cronologia ({@code "27 NOV, 07:48"}: giorno, prime tre lettere del mese, ora locale). Il
     * portale non mostra l'anno: è quello corrente, o il precedente se la data cadrebbe nel futuro (una voce di fine
     * dicembre letta a gennaio).
     */
    public LocalDateTime fromTimelineDate(String portalDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime = parseTimelineDate(portalDate, now.getYear());
        // tolleranza di un giorno per il fuso orario del browser
        return dateTime.isAfter(now.plusDays(1)) ? parseTimelineDate(portalDate, now.getYear() - 1) : dateTime;
    }

    private static LocalDateTime parseTimelineDate(String portalDate, int year) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(TIMELINE_DATE_PATTERN)
                .parseDefaulting(ChronoField.YEAR, year)
                .toFormatter(Locale.ITALIAN);
        return LocalDateTime.parse(portalDate.strip(), formatter);
    }
}

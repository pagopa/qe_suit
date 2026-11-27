package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TableRow;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.SearchNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Cerca tra le notifiche della PA con i filtri del portale ({@code iun}, {@code startDate}, ...): una notifica per
 * riga, con IUN, oggetto e stato; lo stato resta vuoto se il portale lo mostra con un'etichetta non ancora riconosciuta.
 */
@Service
@RequiredArgsConstructor
public class WebSearchNotificationGateway implements SearchNotificationGateway {

    // le colonne si cercano per intestazione: "Gruppi" compare solo in alcuni casi e sposta le successive
    private static final String SUBJECT_HEADER = "Oggetto";
    private static final String IUN_HEADER = "Codice IUN";
    private static final String STATUS_HEADER = "Stato";
    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(20);
    private static final Duration RESULTS_INTERVAL = Duration.ofSeconds(1);
    // le date dei filtri arrivano in ISO (aaaa-mm-gg), il portale le vuole come gg/mm/aaaa
    private static final Set<String> DATE_FILTERS = Set.of("startDate", "endDate");
    private static final DateTimeFormatter PORTAL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DashboardPage dashboardPage;

    @Override
    public List<LegalNotificationDomain> searchNotification(Map<String, String> filters) {
        dashboardPage.navigateTo();
        dashboardPage.assertLoaded();
        // ente senza notifiche: niente filtri né tabella
        if (!hasNotifications(dashboardPage)) {
            return List.of();
        }
        // "Filtra" senza filtri è disabilitato
        if (!filters.isEmpty()) {
            dashboardPage.searchNotification(toPortalFilters(filters));
            // ricerca senza risultati
            if (!hasNotifications(dashboardPage)) {
                return List.of();
            }
        }
        Table table = dashboardPage.notificationsTable();
        List<String> headers = table.getColumnHeaders();
        int subject = column(headers, SUBJECT_HEADER);
        int iun = column(headers, IUN_HEADER);
        int status = column(headers, STATUS_HEADER);
        return table.tBody().rows().stream()
                .map(TableRow::cells)
                .filter(cells -> cells.size() == headers.size())
                .map(cells -> LegalNotificationDomain.builder()
                        .iun(cells.get(iun).read())
                        .subject(cells.get(subject).read())
                        .status(NotificationStatus.fromLabel(cells.get(status).read()).orElse(null))
                        .build())
                .toList();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }

    /**
     * Attende la tabella o lo stato di lista vuota: durante il caricamento non c'è nessuno dei due, e non va scambiato
     * per una ricerca senza risultati.
     */
    private static boolean hasNotifications(DashboardPage dashboard) {
        return PollingUtils.pollUntil(
                        () -> dashboard.notificationsTable().get(FindPolicy.PRESENT).isPresent()
                                ? Optional.of(true)
                                : dashboard.emptyState().get(FindPolicy.PRESENT).map(emptyState -> false),
                        Optional::isPresent,
                        RESULTS_TIMEOUT,
                        RESULTS_INTERVAL,
                        "Né la tabella né lo stato di lista vuota compaiono tra le notifiche della PA")
                .orElseThrow();
    }

    private static Map<String, String> toPortalFilters(Map<String, String> filters) {
        Map<String, String> portalFilters = new LinkedHashMap<>();
        filters.forEach((key, value) -> portalFilters.put(key,
                DATE_FILTERS.contains(key) ? LocalDate.parse(value).format(PORTAL_DATE_FORMAT) : value));
        return portalFilters;
    }

    private static int column(List<String> headers, String header) {
        int index = headers.indexOf(header);
        if (index < 0) {
            throw new IllegalStateException("Colonna " + header + " non presente tra le notifiche della PA: " + headers);
        }
        return index;
    }
}

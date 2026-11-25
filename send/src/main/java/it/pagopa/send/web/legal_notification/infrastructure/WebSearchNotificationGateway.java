package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;
import it.frontend.e2e.framework.web.WebPresentationGateway;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private final WebPresentationGateway browser;

    @Override
    public List<LegalNotificationDomain> searchNotification(Map<String, String> filters) {
        DashboardPage dashboard = browser.bind(DashboardPage.class);
        dashboard.navigateTo();
        dashboard.assertLoaded();
        // ente senza notifiche: niente filtri né tabella
        if (!hasNotifications(dashboard)) {
            return List.of();
        }
        // "Filtra" senza filtri è disabilitato
        if (!filters.isEmpty()) {
            dashboard.searchNotification(filters);
            // ricerca senza risultati
            if (!hasNotifications(dashboard)) {
                return List.of();
            }
        }
        Table table = dashboard.notificationsTable();
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

    private static int column(List<String> headers, String header) {
        int index = headers.indexOf(header);
        if (index < 0) {
            throw new IllegalStateException("Colonna " + header + " non presente tra le notifiche della PA: " + headers);
        }
        return index;
    }
}

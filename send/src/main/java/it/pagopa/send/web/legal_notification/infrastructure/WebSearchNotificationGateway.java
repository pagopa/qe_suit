package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TableRow;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.SearchNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    private final WebPresentationGateway browser;

    @Override
    public List<LegalNotificationDomain> searchNotification(Map<String, String> filters) {
        DashboardPage dashboard = browser.bind(DashboardPage.class);
        dashboard.navigateTo();
        dashboard.assertLoaded();
        dashboard.searchNotification(filters);
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

    private static int column(List<String> headers, String header) {
        int index = headers.indexOf(header);
        if (index < 0) {
            throw new IllegalStateException("Colonna " + header + " non presente tra le notifiche della PA: " + headers);
        }
        return index;
    }
}

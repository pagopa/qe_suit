package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.infrastructure.suit.component.TableRow;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.section.MittenteNotificationSummarySection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Legge le notifiche dal portale della PA: il dettaglio di una notifica (IUN, numero di protocollo, mittente, stato) e
 * le righe delle notifiche della PA (data, codice fiscale, oggetto, IUN, gruppi, stato).
 */
@Component
@RequiredArgsConstructor
public class WebLegalNotificationReadGateway {

    // le colonne si cercano per intestazione: "Gruppi" compare solo in alcuni casi e sposta le successive
    private static final String SUBJECT_HEADER = "Oggetto";
    private static final String IUN_HEADER = "Codice IUN";
    private static final String STATUS_HEADER = "Stato";

    private final WebPresentationGateway browser;

    public LegalNotificationDomain read(String iun) {
        MittenteNotificationDetailsPage details = browser.bind(MittenteNotificationDetailsPage.class);
        details.navigateTo(iun);
        MittenteNotificationSummarySection summary = details.notificationSummarySection();
        return LegalNotificationDomain.builder()
                .iun(iun)
                .paProtocolNumber(summary.protocolNumberValue().read())
                .senderDenomination(summary.senderValue().read())
                .status(NotificationStatusLabel.toStatus(details.notificationStatusSection().statusChip().text().read()))
                .build();
    }

    /** Lo stato di una riga resta vuoto se il portale lo mostra con un'etichetta non ancora riconosciuta. */
    public List<LegalNotificationDomain> search(Map<String, String> filters) {
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
                        .status(NotificationStatusLabel.find(cells.get(status).read()).orElse(null))
                        .build())
                .toList();
    }

    /**
     * Lo IUN della notifica con il numero di protocollo indicato, tra quelle del destinatario con lo stesso oggetto
     * (il numero di protocollo si legge solo nel dettaglio); vuoto se non compare ancora tra le notifiche della PA.
     */
    public Optional<String> findIun(String recipientTaxId, String subject, String paProtocolNumber) {
        return search(Map.of("recipientId", recipientTaxId)).stream()
                .filter(notification -> subject.equals(notification.getSubject()))
                .map(LegalNotificationDomain::getIun)
                .filter(iun -> paProtocolNumber.equals(read(iun).getPaProtocolNumber()))
                .findFirst();
    }

    private static int column(List<String> headers, String header) {
        int index = headers.indexOf(header);
        if (index < 0) {
            throw new IllegalStateException("Colonna " + header + " non presente tra le notifiche della PA: " + headers);
        }
        return index;
    }
}

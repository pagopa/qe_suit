package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.infrastructure.suit.component.TableCell;
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

    private static final int SUBJECT_COLUMN = 2;
    private static final int IUN_COLUMN = 3;
    private static final int STATUS_COLUMN = 5;

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
        return dashboard.notificationsTable().tBody().rows().stream()
                .map(TableRow::cells)
                .filter(cells -> cells.size() > STATUS_COLUMN)
                .map(WebLegalNotificationReadGateway::toNotification)
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

    private static LegalNotificationDomain toNotification(List<TableCell> cells) {
        return LegalNotificationDomain.builder()
                .iun(cells.get(IUN_COLUMN).read())
                .subject(cells.get(SUBJECT_COLUMN).read())
                .status(NotificationStatusLabel.find(cells.get(STATUS_COLUMN).read()).orElse(null))
                .build();
    }
}

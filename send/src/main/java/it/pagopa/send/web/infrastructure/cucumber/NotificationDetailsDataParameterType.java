package it.pagopa.send.web.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationDetailsPFPage;
import it.pagopa.send.web.notification_details.infrastructure.NotificationDetailsProxy;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationDetailsDataParameterType {

    private final NotificationDetailsProxy notificationDetailsProxy;
    private final NotificationDetailsPFPage notificationDetailsPFPage;

    @ParameterType("sommario della notifica|dettagli di pagamento|documenti allegati|stato della notifica|facsimile della notifica")
    public Component notificationDetailsSection(String sectionName) {
        return switch (sectionName) {
            case "sommario della notifica" -> notificationDetailsProxy.notificationSummarySection();
            case "dettagli di pagamento" -> notificationDetailsProxy.paymentSection();
            case "documenti allegati" -> notificationDetailsProxy.attachmentSection();
            case "stato della notifica" -> notificationDetailsProxy.notificationStatusSection();
            case "facsimile della notifica" -> notificationDetailsPFPage.facsimileSection();
            default -> throw new IllegalArgumentException("Unsupported notification details section: " + sectionName);
        };
    }

}

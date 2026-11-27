package it.pagopa.send.common.legal_notification.infrastructure.cucumber;

import io.cucumber.java.ParameterType;
import it.pagopa.send.common.legal_notification.domain.NotificationDocumentType;

/**
 * Vocabolario degli step di consultazione di una notifica da parte della PA mittente:
 * <ul>
 *   <li>{@code {notificationDocument}}: documento della notifica, "l'atto" (notificato dalla PA) oppure
 *   "l'avviso di avvenuta ricezione" (generato da SEND).</li>
 * </ul>
 */
public class LegalNotificationConsultationParameterTypes {

    @ParameterType("l'atto|l'avviso di avvenuta ricezione")
    public NotificationDocumentType notificationDocument(String value) {
        return "l'atto".equals(value) ? NotificationDocumentType.ATTACHMENT : NotificationDocumentType.AAR;
    }
}

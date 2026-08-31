package it.pagopa.send.web.notification_details.infrastructure.suit;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.AttachmentSection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationStatusSection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationSummarySection;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.PaymentSection;

/**
 * Contratto condiviso tra le varianti attore (PA/PF/PG) della pagina di dettaglio notifica.
 * L'{@link Url} non è risolta: questa interfaccia non viene mai bindata direttamente,
 * solo le sue implementazioni concrete (una per attore) lo sono, ciascuna con la propria URL reale.
 */
@Url("about:blank")
public interface NotificationDetailsPage extends Page {

    NotificationSummarySection notificationSummarySection();

    PaymentSection paymentSection();

    AttachmentSection attachmentSection();

    NotificationStatusSection notificationStatusSection();

}

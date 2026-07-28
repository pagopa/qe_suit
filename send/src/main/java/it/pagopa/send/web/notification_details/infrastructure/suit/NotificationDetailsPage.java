package it.pagopa.send.web.notification_details.infrastructure.suit;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;

/**
 * Contratto condiviso tra le varianti attore (PA/PF/PG) della pagina di dettaglio notifica.
 * L'{@link Url} non è risolta: questa interfaccia non viene mai bindata direttamente,
 * solo le sue implementazioni concrete (una per attore) lo sono, ciascuna con la propria URL reale.
 */
@Url("www.google.com")
public interface NotificationDetailsPage extends Page {

    Component notificationSummarySection();

    Component paymentSection();

    Component attachmentSection();

    Component notificationStatusSection();

}

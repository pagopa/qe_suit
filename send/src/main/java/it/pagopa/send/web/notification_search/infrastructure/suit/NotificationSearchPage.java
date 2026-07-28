package it.pagopa.send.web.notification_search.infrastructure.suit;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.web.domain.Page;

import java.util.Map;

/**
 * Contratto condiviso tra le varianti attore (PA/PF/PG) della pagina "Notifiche" con la ricerca:
 * i filtri comuni sono {@code iun}, {@code startDate}, {@code endDate}. Il portale mittenti (PA)
 * ha filtri aggiuntivi ({@code recipientId}, {@code status}) che non fanno parte di questo
 * contratto: restano definiti solo su {@code DashboardPage}, quindi passarli per un utente PF
 * fa fallire {@link #searchNotification} con {@link IllegalArgumentException} (nessun case
 * corrispondente nello switch della pagina concreta), non silenziosamente ignorati.
 * <p>
 * L'{@link Url} non è risolta: questa interfaccia non viene mai bindata direttamente, solo le sue
 * implementazioni concrete lo sono, ciascuna con la propria URL reale.
 */
@Url("about:blank")
public interface NotificationSearchPage extends Page {

    void searchNotification(Map<String, String> searchParams);

    void goToNotificationDetails();
}

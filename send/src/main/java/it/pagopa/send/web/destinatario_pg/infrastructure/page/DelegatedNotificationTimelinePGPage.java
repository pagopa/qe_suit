package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;

/**
 * {@code {baseUrl}/notifiche-delegato/<id delega>/<IUN>/dettaglio/timeline}
 * Pagina "Stato della notifica" di una notifica a valore legale ricevuta tramite delega dalla persona giuridica.
 * Si apre da "Vai al dettaglio" del dettaglio della notifica delegata ({@link DelegatedNotificationDetailPGPage})
 * oppure direttamente dall'URL, con l'id della delega e lo IUN della notifica come parametri.
 * Ha gli stessi elementi della pagina "Stato della notifica" di una notifica dell'impresa
 * ({@link NotificationTimelinePGPage}).
 * Breadcrumb ed eventi sono verificati da {@code WebNotificationTimelineDelegatoContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.notifiche-delegato-dettaglio-timeline}")
public interface DelegatedNotificationTimelinePGPage extends NotificationTimelinePGPage {
}

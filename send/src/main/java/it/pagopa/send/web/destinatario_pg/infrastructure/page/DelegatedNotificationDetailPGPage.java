package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;

/**
 * {@code {baseUrl}/notifiche-delegato/<id delega>/<IUN>/dettaglio}
 * Pagina di dettaglio di una notifica a valore legale ricevuta tramite delega dalla persona giuridica.
 * Si apre dal pulsante "Apri" di una riga della pagina "In arrivo dalle deleghe" ({@link DelegatedNotificationPage})
 * oppure direttamente dall'URL, con l'id della delega e lo IUN della notifica come parametri.
 * Ha gli stessi elementi del dettaglio di una notifica dell'impresa ({@link NotificationDetailPGPage}) e in più
 * "Persona destinataria" con il delegante.
 * Testi e sezioni sono verificati da {@code WebDelegatedNotificationDetailPGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.notifiche-delegato-dettaglio}")
public interface DelegatedNotificationDetailPGPage extends NotificationDetailPGPage {

    // "Persona destinataria" con "<nome> - <codice fiscale>" del delegante

    @XPath("//*[@data-testid=\"aarDownload\"]//h3")
    Readable<String> recipientTitle();

    @XPath("//*[@data-testid=\"aarDownload\"]//h3/following-sibling::span[1]")
    Readable<String> recipient();
}

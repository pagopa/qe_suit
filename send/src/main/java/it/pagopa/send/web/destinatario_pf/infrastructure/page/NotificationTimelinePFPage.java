package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/notifiche/<IUN>/dettaglio/timeline}
 * Pagina "Stato della notifica" del cittadino, con la timeline degli eventi di una notifica a valore legale.
 * Si apre dal pulsante "Vai al dettaglio" della sezione
 * "Stato della notifica" nel dettaglio della notifica ({@link NotificationDetailsPFPage}) oppure direttamente dall'URL,
 * con lo IUN della notifica come parametro.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e il breadcrumb. Breadcrumb ed eventi sono
 * verificati da {@code WebNotificationTimelinePFContractTest}.
 */
@Url("${url.notifiche.cittadino.notifiche}/${iun}/dettaglio/timeline")
public interface NotificationTimelinePFPage extends Page {

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]")
    Button notificationsBreadcrumb();

    @XPath("//*[@data-testid=\"breadcrumb-subject-button\"]")
    Button notificationBreadcrumb();

    @XPath("//*[@data-testid=\"NotificationEventsTimeline\"]//*[@data-testid=\"dateItem\"]")
    Readable<String> eventDates();

    // labels

    @XPath("//nav[@aria-label=\"breadcrumbs\"]//p[@aria-current=\"page\"]")
    Readable<String> currentBreadcrumb();

    @XPath("//*[@data-testid=\"NotificationEventsTimeline\"]/preceding-sibling::h1")
    Readable<String> title();

    @XPath("//*[@data-testid=\"NotificationEventsTimeline\"]/ul/li/div[2]/div/div[1]/span")
    Readable<String> eventTitles();

    // evento più vecchio, in fondo alla cronologia: l'accettazione della notifica, presente da quando è accettata

    @XPath("(//*[@data-testid=\"NotificationEventsTimeline\"]/ul/li)[last()]/div[2]/div/div[1]")
    Readable<String> firstEventStatus();

    // descrizione seguita dalla data dell'evento
    @XPath("(//*[@data-testid=\"NotificationEventsTimeline\"]/ul/li)[last()]//*[@data-testid=\"dateItem\"]/parent::*")
    Readable<String> firstEventDescription();

    @XPath("(//*[@data-testid=\"NotificationEventsTimeline\"]/ul/li)[last()]//*[@data-testid=\"dateItem\"]")
    Readable<String> firstEventDate();

    // link per scaricare l'attestazione opponibile a terzi dell'evento
    @XPath("((//*[@data-testid=\"NotificationEventsTimeline\"]/ul/li)[last()]//*[starts-with(@data-testid,\"download-legalfact\")])[1]")
    Readable<String> firstEventLegalFact();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Stato della notifica"));
        notificationsBreadcrumb().assertLoaded();
    }
}

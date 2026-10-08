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
 * "Stato della notifica" nel dettaglio della notifica.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e il breadcrumb; breadcrumb ed eventi sono
 * verificati da {@code WebNotificationDetailsPFContractTest}. Gli eventi cambiano da notifica a notifica, per cui di
 * ciascuno si verifica solo che abbia un titolo e, se presente, una data.
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

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Stato della notifica"));
        notificationsBreadcrumb().assertLoaded();
    }
}

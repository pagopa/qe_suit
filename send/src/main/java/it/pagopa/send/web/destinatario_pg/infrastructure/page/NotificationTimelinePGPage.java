package it.pagopa.send.web.destinatario_pg.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/notifiche/<IUN>/dettaglio/timeline}
 * Pagina "Stato della notifica" della persona giuridica: la cronologia degli stati di una notifica.
 * Si apre da "Vai al dettaglio" della sezione "Stato della notifica" del dettaglio ({@link NotificationDetailPGPage})
 * oppure direttamente dall'URL, con lo IUN della notifica come parametro.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo. Breadcrumb ed eventi sono verificati da
 * {@code WebNotificationTimelinePGContractTest}.
 */
@Url("${url.notifiche.persona-giuridica.notifiche-dettaglio-timeline}")
public interface NotificationTimelinePGPage extends Page {

    String TITLE = "Stato della notifica";

    // breadcrumb: "In arrivo per <impresa>" > oggetto della notifica > "Stato della notifica"

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]")
    Readable<String> notificationsBreadcrumb();

    @XPath("//*[@data-testid=\"breadcrumb-subject-button\"]")
    Readable<String> notificationBreadcrumb();

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]/ancestor::ol/li[last()]")
    Readable<String> currentBreadcrumb();

    @XPath("//main//h1")
    Readable<String> title();

    // stato di ogni evento della cronologia, dal più recente
    @XPath("//*[@data-testid=\"NotificationEventsTimeline\"]/ul/li/div[2]/div/div[1]")
    Readable<String> eventStatuses();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
    }
}

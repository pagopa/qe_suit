package it.pagopa.send.web.notification_details.infrastructure.page.timeline;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/dashboard/:iun/dettaglio/timeline}
 * Pagina "Stato della notifica" della PA: la cronologia degli stati di una notifica inviata.
 * Si apre da "Vai al dettaglio" della sezione "Stato della notifica" del dettaglio oppure direttamente dall'URL, con lo
 * IUN della notifica come parametro.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e la timeline.
 * È usata da più test:
 * <ul>
 *     <li>{@code WebNotificationTimelinePaContractTest}: breadcrumb, titolo ed eventi della pagina;</li>
 *     <li>{@code WebNotificationTimelineContractTest}: testi degli eventi di una notifica inviata a una PF e a una PG,
 *     visti dalla PA.</li>
 * </ul>
 * Per Cucumber è registrata come bean in {@code NotificationDetailsConfig}.
 */
@Url("${url.notifiche.mittente.dashboard}/${iun}/dettaglio/timeline")
public interface TimelineDetailsPage extends Page {

    String TITLE = "Stato della notifica";

    // breadcrumb: "Notifiche" > IUN della notifica > "Stato della notifica"; su mobile è sostituito da "Indietro"

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]")
    Readable<String> notificationsBreadcrumb();

    @XPath("//*[@data-testid=\"breadcrumb-iun-button\"]")
    Readable<String> iunBreadcrumb();

    @XPath("//*[@data-testid=\"breadcrumb-root-button\"]/ancestor::ol/li[last()]")
    Readable<String> currentBreadcrumb();

    @XPath("//main//h1")
    Readable<String> title();

    TimelineComponent timeline();

    Optional<OneTrustBanner> oneTrustBanner();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
        timeline().assertLoaded();
    }
}

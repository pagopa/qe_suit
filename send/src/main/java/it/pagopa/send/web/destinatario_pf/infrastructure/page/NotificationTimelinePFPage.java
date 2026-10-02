package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * Questa pagina rappresenta lo stato di una notifica a valore legale per il cittadino, con la timeline degli eventi,
 * accessibile dal dettaglio della notifica tramite "Vai al dettaglio".
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
        notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("In arrivo"));
        notificationBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        eventDates().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).matches("\\d{2} [A-Z]{3}, \\d{2}:\\d{2}")));

        // labels
        currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Stato della notifica"));
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Stato della notifica"));
        eventTitles().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
    }
}

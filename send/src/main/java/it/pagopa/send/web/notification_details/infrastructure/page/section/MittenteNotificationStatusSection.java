package it.pagopa.send.web.notification_details.infrastructure.page.section;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationStatusSection;
import it.pagopa.infrastructure.suit.component.Chip;
import org.assertj.core.api.Assertions;

/**
 * Riquadro "Stato della notifica" del dettaglio della PA: titolo, stato attuale con la sua descrizione e
 * "Vai al dettaglio", che apre la timeline della notifica.
 */
@XPath(".//div[@data-testid='NotificationDetailTimeline']")
public interface MittenteNotificationStatusSection extends NotificationStatusSection {
    //        @XPath("header")
    @Override
    @XPath("//div[@data-testid=\"NotificationDetailTimeline\"]//h2")
    Readable<String> header();

    @Override
    @XPath("//div[@data-testid='NotificationDetailTimeline']")
    Chip statusChip();

    //        @XPath("dettaglio stato notifica")
    @Override
    @XPath("//div[@data-testid=\"NotificationDetailTimeline\"]//p")
    Readable<String> detailsMessage();

    //        @XPath("griglia contenuti")
    @Override
    @XPath("//button[@aria-label=\"Vai alla timeline della notifica\"]")
    Button detailsButton();

    @Override
    default void assertLoaded() {
        header().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
        });
        statusChip().text().readAndAssert((t) -> {
            Assertions.assertThat(t).isNotNull();
        });
        detailsMessage().readAndAssert((m) -> {
            Assertions.assertThat(m).isNotNull();
        });
    }
}

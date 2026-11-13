package it.pagopa.send.web.destinatario_pf.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationSummarySection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Riepilogo della notifica nella pagina di dettaglio del cittadino ({@code NotificationDetailsPFPage}).
 */
public interface NotificationDetailsSection extends NotificationSummarySection {

    @XPath("//*[@id=\"item\"]")
    Chip type();

    @XPath("//*[@id=\"item\"]")
    Readable<String> header();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[1]/div[1]/div[3]/div[1]/div[2]/span")
    Readable<String> sender();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[1]/div[1]/div[3]/div[1]/div[2]/p")
    Readable<String> date();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[1]/div[1]/div[3]/div[2]/div/p[2]")
    Readable<String> iun();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[1]/div[1]/p")
    Readable<String> description();

    @Override
    default void assertLoaded() {
        header().readAndAssert((h) -> {
            assertThat(h).isNotNull();
            assertThat(h).isIn("Configure SEND", "Configura SEND");
        });
    }
}

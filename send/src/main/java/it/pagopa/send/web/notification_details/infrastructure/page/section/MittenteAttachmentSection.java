package it.pagopa.send.web.notification_details.infrastructure.page.section;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.AttachmentSection;
import org.assertj.core.api.Assertions;

/**
 * Sezione "Documenti allegati" del dettaglio della PA: titolo ed elenco dei documenti scaricabili.
 */
@XPath("//h2[@id=\"notification-detail-document-attached\"]/ancestor::div[.//button[@data-testid=\"cancelNotificationBtn\"]][1]")
public interface MittenteAttachmentSection extends AttachmentSection {
    //        @XPath("header")
    @XPath("//h2[@id=\"notification-detail-document-attached\"]")
    Readable<String> header();

    //        @XPath("griglia contenuti")
    @XPath("//div[@data-testid=\"notificationDetailDocuments\"]//button[@data-testid=\"documentButton\"]/div/span")
    Readable<String> attachmentGrid();

    @Override
    default void assertLoaded() {
        header().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
        });
        attachmentGrid().readAndAssert((g) -> {
            Assertions.assertThat(g).isNotNull();
        });
    }
}

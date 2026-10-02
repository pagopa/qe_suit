package it.pagopa.interop.web.purpose.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;

@Url("${interop.web.producer-purpose-list}")
public interface ProducerPurposeListPage extends Page {

    Breadcrumbs breadcrumbs();

    @XPath(".//h1[normalize-space(.)='Finalità ricevute']")
    Readable<String> pageTitle();

    @XPath(".//th[normalize-space(.)='Finalità']")
    Readable<String> purposeHeader();

    @XPath(".//th[normalize-space(.)='Fruitore']")
    Readable<String> consumerHeader();

    @XPath(".//th[normalize-space(.)='Stato finalità']")
    Readable<String> stateHeader();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert("Finalità ricevute");
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Erogazione", "Finalità ricevute");
    }
}

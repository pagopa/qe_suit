package it.pagopa.interop.web.agreement.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import org.assertj.core.api.Assertions;

@Url("${interop.web.producer-agreement-list}")
public interface ProducerAgreementListPage extends Page {

    @XPath(".//h1[normalize-space(.)='Richieste di fruizione ricevute']")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert("Richieste di fruizione ricevute");
        Assertions.assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Erogazione", "Richieste ricevute");
    }
}

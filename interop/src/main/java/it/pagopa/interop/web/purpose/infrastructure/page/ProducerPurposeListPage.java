package it.pagopa.interop.web.purpose.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;

@Url("${interop.web.producer-purpose-list}")
public interface ProducerPurposeListPage extends Page {

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
    }
}

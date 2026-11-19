package it.pagopa.interop.web.purpose_template.infrastructure.suite.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;

@Url("${interop.web.base-url}/fruizione/template-finalita")
public interface ConsumerMyPurposeTemplatePage extends Page {

    @XPath(".//h1")
    Readable<String> title();

    default String getTitle() {
        return title().read();
    }

    @Override
    default void assertLoaded() {
        title().readAndAssert("I miei template di finalità agevolata");
    }
}

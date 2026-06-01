package it.pagopa.interop.ui.domain.page.eservice_creation.step;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;

public interface ThresholdAndAttributeStep extends Component {

    @XPath(".//h2[text()='Soglie di chiamate API']")
    Readable<String> title();

    @Override
    default void assertLoaded() {
        title().readAndAssert("Soglie di chiamate API");
    }
}

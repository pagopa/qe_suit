package it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.common.infrastructure.suit.component.AttributeTabs;

@XPath("//*[contains(@class, 'MuiStepper-root')]/parent::div/parent::div")
public interface ThresholdAndAttributeWizard extends Component {

    @XPath(".//h2[text()='Soglie di chiamate API']")
    Readable<String> title();

    AttributeTabs attributesTabs();

    @Override
    default void assertLoaded() {
        title().readAndAssert("Soglie di chiamate API");
    }
}

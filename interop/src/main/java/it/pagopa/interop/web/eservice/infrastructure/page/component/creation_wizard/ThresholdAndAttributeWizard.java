package it.pagopa.interop.web.eservice.infrastructure.page.component.creation_wizard;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.TextField;

public interface ThresholdAndAttributeWizard extends Component {

    @XPath(".//h2[text()='Soglie di chiamate API']")
    Readable<String> title();

    @XPath(".//*[@id=\"dailyCallsPerConsumer\"]")
    TextField dailyCallsPerConsumer();

    @XPath(".//*[@id=\"dailyCallsTotal\"]")
    TextField dailyCallsTotal();

    default String getDailyCallsPerConsumerErrorText() {
        return dailyCallsPerConsumer().getErrorMessage("dailyCallsPerConsumer-error");
    }

    default String getDailyCallsTotalErrorText() {
        return dailyCallsTotal().getErrorMessage("dailyCallsTotal-error");
    }

    @Override
    default void assertLoaded() {
        title().readAndAssert("Soglie di chiamate API");
    }
}

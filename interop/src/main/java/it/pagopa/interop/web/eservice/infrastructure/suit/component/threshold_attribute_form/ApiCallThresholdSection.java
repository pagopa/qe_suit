package it.pagopa.interop.web.eservice.infrastructure.suit.component.threshold_attribute_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.TextField;

@XPath(".//*[contains(@class, 'MuiPaper-root') and (.//h2)[1][normalize-space(.)='Soglie di chiamate API']]")
public interface ApiCallThresholdSection extends Component {
    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//p")
    Readable<String> description();

    @XPath("//*[@id=\"dailyCallsPerConsumer\"]")
    TextField dailyCallsPerConsumerInput();

    @XPath("//*[@id=\"dailyCallsTotal\"]")
    TextField dailyCallsTotalInput();

    @Override
    default void assertLoaded() {
        title().readAndAssert("Soglie di chiamate API");
    }
}

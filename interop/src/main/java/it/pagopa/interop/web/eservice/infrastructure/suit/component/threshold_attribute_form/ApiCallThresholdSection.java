package it.pagopa.interop.web.eservice.infrastructure.suit.component.threshold_attribute_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.FormHelperText;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Switch;
import it.pagopa.infrastructure.suit.component.TextField;

@XPath(".//*[contains(@class, 'MuiPaper-root') and (.//h2)[1][normalize-space(.)='Soglie di chiamate API']]")
public interface ApiCallThresholdSection extends Component {
    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//p")
    Readable<String> description();

    @XPath(".//label[.//span[contains(@class, 'MuiSwitch-root')]]")
    Label thresholdsSwitchLabel();

    @XPath(".//span[contains(@class, 'MuiSwitch-root')]")
    Switch thresholdsSwitch();

    @XPath(".//div[contains(@class, 'MuiAlert-root')]")
    Alert thresholdsInfoAlert();

    @XPath(".//*[@id='dailyCallsPerConsumer']")
    TextField dailyCallsPerConsumerInput();

    @XPath(".//span[@id='dailyCallsPerConsumer-infoLabel']")
    FormHelperText dailyCallsPerConsumerHelperText();

    @XPath(".//span[@id='dailyCallsPerConsumer']/ancestor::div[contains(@class, 'MuiTextField-root')][1]//span[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]")
    FormHelperText dailyCallsPerConsumerErrorText();

    @XPath(".//*[@id='dailyCallsTotal']")
    TextField dailyCallsTotalInput();

    @XPath(".//span[@id='dailyCallsTotal-infoLabel']")
    FormHelperText dailyCallsTotalHelperText();

    @XPath(".//*[@id='dailyCallsTotal']/ancestor::div[contains(@class, 'MuiTextField-root')][1]//span[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]")
    FormHelperText dailyCallsTotalErrorText();

    @Override
    default void assertLoaded() {
        title().readAndAssert("Soglie di chiamate API");
    }
}

package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.RadioGroup;

@XPath(".//input[@name='technology']/ancestor::*[contains(@class, 'MuiPaper-root')][1]")
public interface EServiceDetailsSection extends Component {

    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//fieldset[.//input[@name='asyncExchange']]/legend")
    Label exchangeModeQuestion();

    @XPath(".//fieldset[.//input[@name='technology']]/legend")
    Label technologyQuestion();

    @XPath(".//fieldset[.//input[@name='mode']]/legend")
    Label dataFlowQuestion();

    @XPath(".//fieldset[.//input[@name='personalData']]/legend")
    Label personalDataQuestion();

    @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'Questi dati non saranno più modificabili')]")
    Alert generalAlert();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//input[@name='asyncExchange']]")
    RadioGroup asyncExchange();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//input[@name='technology']]")
    RadioGroup technology();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//input[@name='mode']]")
    RadioGroup mode();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//input[@name='personalData']]")
    RadioGroup personalData();

    @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'SOAP non permette di abilitare il download a blocchi')]")
    Alert soapAsyncAlert();

    @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'Per gli scambi asincroni è necessario collegare un portachiavi all’e-service')]")
    Alert keychainAlert();
}

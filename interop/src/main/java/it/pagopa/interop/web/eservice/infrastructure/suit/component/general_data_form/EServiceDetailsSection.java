package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.RadioGroup;

@XPath(".//*[contains(@class, 'MuiPaper-root') and (.//h2)[1][normalize-space(.)=\"Dettagli dell'e-service\"]]")
public interface EServiceDetailsSection extends Component {

    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'Questi dati non saranno più modificabili')]")
    Alert generalAlert();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='Sincrono (standard)'] and .//*[text()='Asincrono / massivo (in differita)']]")
    RadioGroup asyncExchange();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='REST'] and .//*[text()='SOAP']]")
    RadioGroup technology();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='Eroga'] and .//*[text()='Riceve']]")
    RadioGroup mode();

    @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and ((.//*[text()='Eroga dati personali'] and .//*[text()='Non eroga dati personali']) or (.//*[text()='Riceve dati personali'] and .//*[text()='Non riceve dati personali']))]")
    RadioGroup personalData();

    @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'SOAP non permette di abilitare il download a blocchi')]")
    Alert soapAsyncAlert();

    @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'Per gli scambi asincroni è necessario collegare un portachiavi all’e-service')]")
    Alert keychainAlert();
}

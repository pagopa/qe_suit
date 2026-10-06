package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Switch;

@XPath(".//*[contains(@class, 'MuiPaper-root') and (.//h2)[1][normalize-space(.)='Signal Hub: servizio di notifica della variazione dei dati']]")
public interface SignalHubSection extends Component {
    @XPath(".//h2")
    it.frontend.e2e.framework.web.capability.core.Readable<String> title();

    @XPath(".//p")
    Readable<String> description();

    @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'Prima di procedere, assicurati che il documento di designazione')]")
    Alert generalAlert();

    Switch signalHubSwitch();
}

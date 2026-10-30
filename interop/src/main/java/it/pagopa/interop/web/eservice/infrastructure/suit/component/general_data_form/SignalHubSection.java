package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Alert;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.Switch;

@XPath(".//input[@name='isSignalHubEnabled']/ancestor::*[contains(@class, 'MuiPaper-root')][1]")
public interface SignalHubSection extends Component {
    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//a[contains(., 'soluzione integrata')]/ancestor::p[1]")
    Readable<String> description();

    @XPath(".//a[contains(., 'documento di designazione')]/ancestor::p[1]")
    Label designationDescription();

    @XPath(".//a[contains(., 'soluzione integrata')]")
    Link solutionLink();

    @XPath(".//a[contains(., 'documento di designazione')]")
    Link designationPdfLink();

    @XPath(".//*[@id='isSignalHubEnabled-label']")
    Label signalHubLabel();

    @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'Prima di procedere, assicurati che il documento di designazione')]")
    Alert generalAlert();

    @XPath(".//input[@name='isSignalHubEnabled']/ancestor::span[contains(@class, 'MuiSwitch-root')][1]")
    Switch signalHubSwitch();
}

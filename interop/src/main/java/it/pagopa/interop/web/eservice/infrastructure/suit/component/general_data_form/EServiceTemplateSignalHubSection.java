package it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.Switch;

@XPath(".//div[contains(@class, 'MuiPaper-root') and .//h2[normalize-space(.)='Signal Hub']]")
public interface EServiceTemplateSignalHubSection extends Component {

    @XPath(".//h2")
    Readable<String> title();

    @XPath(".//*[@id='isSignalHubEnabled-label']/span")
    Label signalHubLabel();

    @XPath(".//a[normalize-space(.)='notifiche push']/ancestor::p[1]")
    Label infoLabel();

    @XPath(".//a[normalize-space(.)='notifiche push']")
    Link pushNotificationsLink();

    @XPath(".//input[@name='isSignalHubEnabled']/ancestor::span[contains(@class, 'MuiSwitch-root')][1]")
    Switch signalHubSwitch();
}


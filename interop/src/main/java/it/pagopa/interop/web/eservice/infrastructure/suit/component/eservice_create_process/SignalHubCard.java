package it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_create_process;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;
import it.pagopa.infrastructure.suit.component.Switch;

public interface SignalHubCard extends EServiceCreatePageCard {
    @XPath(".//a[contains(., 'soluzione integrata')]/ancestor::p[1]")
    Label description();

    @XPath(".//a[contains(., 'documento di designazione')]/ancestor::p[1]")
    Label designationDescription();

    @XPath(".//a[contains(., 'soluzione integrata')]")
    Link solutionLink();

    @XPath(".//a[contains(., 'documento di designazione')]")
    Link designationPdfLink();

    @XPath(".//input[@name='isSignalHubEnabled']/ancestor::span[contains(@class, 'MuiSwitch-root')][1]")
    Switch signalHubSwitch();

    @XPath(".//*[@id='isSignalHubEnabled-label']")
    Label signalHubLabel();
}

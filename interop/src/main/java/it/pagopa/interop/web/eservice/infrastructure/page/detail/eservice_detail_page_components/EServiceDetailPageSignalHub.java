package it.pagopa.interop.web.eservice.infrastructure.page.detail.eservice_detail_page_components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface EServiceDetailPageSignalHub extends Component {
    String SIGNAL_HUB_SECTION = ".//section[./div[1]//h2[normalize-space()='Signal Hub']]";

    @XPath(SIGNAL_HUB_SECTION + "/div[1]//h2")
    Label signalHubTitle();

    // Description is <p>before <a>link</a> after</p>, wrapped by the section description <p>
    @XPath(SIGNAL_HUB_SECTION + "/div[1]/p")
    Label signalHubDescription();

    @XPath(SIGNAL_HUB_SECTION + "//a[normalize-space()='soluzione integrata']")
    Label signalHubLink();

    @XPath(SIGNAL_HUB_SECTION + "//*[normalize-space()='Disponibilità del servizio' and not(*)]")
    Label signalHubAvailabilityLabel();

    @XPath(SIGNAL_HUB_SECTION + "//p[normalize-space()='Disponibilità del servizio']/parent::div/following-sibling::div[1]//span")
    Label signalHubAvailabilityValue();
}

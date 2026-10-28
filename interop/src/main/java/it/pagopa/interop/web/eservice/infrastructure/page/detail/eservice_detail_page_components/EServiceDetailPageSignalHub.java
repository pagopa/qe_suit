package it.pagopa.interop.web.eservice.infrastructure.page.detail.eservice_detail_page_components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;

public interface EServiceDetailPageSignalHub extends Component {
    @XPath("./div[1]//h2")
    Label signalHubTitle();

    // Description is <p>before <a>link</a> after</p>, wrapped by the section description <p>
    @XPath("./div[1]/p")
    Label signalHubDescription();

    @XPath(".//a[normalize-space()='soluzione integrata']")
    Link signalHubLink();

    @XPath(".//*[normalize-space()='Disponibilità del servizio' and not(*)]")
    Label signalHubAvailabilityLabel();

    @XPath(".//p[normalize-space()='Disponibilità del servizio']/parent::div/following-sibling::div[1]//span")
    Label signalHubAvailabilityValue();
}

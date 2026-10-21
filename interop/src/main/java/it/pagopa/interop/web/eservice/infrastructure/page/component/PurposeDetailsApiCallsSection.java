package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;

public interface PurposeDetailsApiCallsSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//span[normalize-space()='Stima attuale']")
    Label currentEstimateLabel();

    @XPath(".//span[normalize-space()='Stima attuale']/ancestor::div[contains(.,'Chiamate API/giorno')][1]//h4")
    Label currentEstimateValue();

    @XPath(".//button[.//span[normalize-space()='Richiedi cambio stima']]")
    Button requestEstimateChangeButton();

    @XPath(".//span[normalize-space()='Soglie definite dall’erogatore']")
    Label providerThresholdsLabel();

    @XPath("(//span[normalize-space()='Soglie definite dall’erogatore']/ancestor::div[contains(.,'Chiamate API/giorno')][1]//h4)[1]")
    Label consumerThresholdValue();

    @XPath("(//span[normalize-space()='Soglie definite dall’erogatore']/ancestor::div[contains(.,'Chiamate API/giorno')][1]//h4)[2]")
    Label totalThresholdValue();
}

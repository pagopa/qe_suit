package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;

public interface PurposeDetailsApiCallsSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//h2/ancestor::div[3]/p[1]")
    Label sectionDescription();

    @XPath(".//span[normalize-space()='Stima attuale']")
    Label currentEstimateLabel();

    @XPath(".//span[normalize-space()='Stima attuale']/following-sibling::span")
    Label currentEstimateUnitLabel();

    @XPath(".//span[normalize-space()='Stima attuale']/ancestor::div[2]/following-sibling::div//h4")
    Label currentEstimateValue();

    @XPath(".//button[.//span[normalize-space()='Richiedi cambio stima']]")
    Button requestEstimateChangeButton();

    @XPath(".//span[normalize-space()='Soglie definite dall’erogatore']")
    Label providerThresholdsLabel();

    @XPath(".//span[normalize-space()='Soglie definite dall’erogatore']/following-sibling::p[1]")
    Label providerThresholdsUnitLabel();

    @XPath(".//p[normalize-space()='per fruitore']")
    Label consumerThresholdLabel();

    @XPath(".//p[normalize-space()='per fruitore']/preceding-sibling::h4[1]")
    Label consumerThresholdValue();

    @XPath(".//p[normalize-space()='totali']")
    Label totalThresholdLabel();

    @XPath(".//p[normalize-space()='totali']/preceding-sibling::h4[1]")
    Label totalThresholdValue();
}

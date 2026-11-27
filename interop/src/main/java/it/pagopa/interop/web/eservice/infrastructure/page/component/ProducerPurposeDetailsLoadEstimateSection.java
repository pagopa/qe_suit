package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface ProducerPurposeDetailsLoadEstimateSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//p[normalize-space()=\"Il numero di chiamate previste ogni giorno verso l'e-service\"]")
    Label sectionDescription();

    @XPath(".//*[normalize-space()='Stima attuale del fruitore']")
    Label currentEstimateTitle();

    @XPath(".//div[contains(@class,'MuiCard-root') and .//*[normalize-space()='Stima attuale del fruitore']]//*[normalize-space()='Chiamate API/giorno']")
    Label currentEstimateUnitLabel();

    @XPath(".//div[contains(@class,'MuiCard-root') and .//*[normalize-space()='Stima attuale del fruitore']]//h4")
    Label currentEstimateValue();

    @XPath(".//*[normalize-space()='Soglie definite']")
    Label thresholdsTitle();

    @XPath(".//div[contains(@class,'MuiCard-root') and .//*[normalize-space()='Soglie definite']]//*[normalize-space()='Chiamate API/giorno']")
    Label thresholdsUnitLabel();

    @XPath(".//p[normalize-space()='per fruitore']")
    Label consumerThresholdLabel();

    @XPath(".//div[.//p[normalize-space()='per fruitore'] and .//h4 and not(.//div[.//p[normalize-space()='per fruitore'] and .//h4])]//h4")
    Label consumerThresholdValue();

    @XPath(".//p[normalize-space()='totali']")
    Label totalThresholdLabel();

    @XPath(".//div[.//p[normalize-space()='totali'] and .//h4 and not(.//div[.//p[normalize-space()='totali'] and .//h4])]//h4")
    Label totalThresholdValue();
}


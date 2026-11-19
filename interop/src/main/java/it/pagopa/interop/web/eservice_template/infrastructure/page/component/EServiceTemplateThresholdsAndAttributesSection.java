package it.pagopa.interop.web.eservice_template.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;

public interface EServiceTemplateThresholdsAndAttributesSection extends Component {

    @XPath(".//h2")
    Label sectionTitle();

    @XPath(".//h2/following::p[1]")
    Label sectionDescription();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Soglie di chiamate API']")
    Label thresholdsHeading();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Soglie di chiamate API']/ancestor::div[2]//button[normalize-space()='Modifica']")
    Button editThresholdsButton();

    @XPath(".//p[normalize-space()='Soglia giornaliera per fruitore']")
    Label dailyCallsPerConsumerLabel();

    @XPath(".//p[normalize-space()='Soglia giornaliera per fruitore']/ancestor::div[1]/following-sibling::div//*[normalize-space()][1]")
    Label dailyCallsPerConsumerValue();

    @XPath(".//p[normalize-space()='Soglia giornaliera totale']")
    Label dailyCallsTotalLabel();

    @XPath(".//p[normalize-space()='Soglia giornaliera totale']/ancestor::div[1]/following-sibling::div//*[normalize-space()][1]")
    Label dailyCallsTotalValue();

    @XPath(".//*[normalize-space()='Questo template di e-service non ha soglie suggerite']")
    Label emptyThresholdsCard();

    @XPath(".//*[self::h3 or self::h4 or self::p][normalize-space()='Attributi']")
    Label attributesHeading();

    @XPath(".//*[normalize-space()='Questo template di e-service non richiede attributi']")
    Label emptyAttributesCard();
}


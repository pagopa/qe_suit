package it.pagopa.interop.web.eservice.infrastructure.page.detail.eservice_detail_page_components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface EServiceDetailPageThresholdsAndAttributes extends Component {
    @XPath("./div[1]//h2")
    Label attributesSectionTitle();

    @XPath("./div[1]/p")
    Label attributesSectionDescription();

    @XPath(".//h3[normalize-space()='Soglie di chiamate API']")
    Label thresholdsTitle();

    @XPath(".//*[normalize-space()='Soglia giornaliera per fruitore' and not(*)]")
    Label dailyCallsPerConsumerLabel();

    @XPath(".//*[normalize-space()='Soglia giornaliera per fruitore' and not(*)]/parent::div/following-sibling::div[1]//span")
    Label dailyCallsPerConsumerValue();

    @XPath(".//*[normalize-space()='Soglia giornaliera totale' and not(*)]")
    Label dailyCallsTotalLabel();

    @XPath(".//*[normalize-space()='Soglia giornaliera totale' and not(*)]/parent::div/following-sibling::div[1]//span")
    Label dailyCallsTotalValue();

    @XPath(".//h3[normalize-space()='Soglie di chiamate API personalizzate']")
    Label customizedThresholdsTitle();

    @XPath(".//h3[normalize-space()='Attributi']")
    Label attributesTitle();

    @XPath(".//*[normalize-space()='Questo e-service non richiede attributi' and not(*)]")
    Label noAttributesRequiredMessage();

    @XPath(".//h3[normalize-space()='Attributi Certificati']")
    Label certifiedAttributesTitle();

    @XPath(".//h3[normalize-space()='Attributi Verificati']")
    Label verifiedAttributesTitle();

    @XPath(".//h3[normalize-space()='Attributi Dichiarati']")
    Label declaredAttributesTitle();
}

package it.pagopa.interop.web.eservice.infrastructure.page.detail.eservice_detail_page_components;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;

public interface EServiceDetailPageThresholdsAndAttributes extends Component {
    String ATTRIBUTES_SECTION = ".//section[./div[1]//h2[normalize-space()='Soglie e attributi']]";

    // ----------------------------------------------- Thresholds and attributes
    @XPath(ATTRIBUTES_SECTION + "/div[1]//h2")
    Label attributesSectionTitle();

    @XPath(ATTRIBUTES_SECTION + "/div[1]/p")
    Label attributesSectionDescription();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Soglie di chiamate API']")
    Label thresholdsTitle();

    @XPath(ATTRIBUTES_SECTION + "//*[normalize-space()='Soglia giornaliera per fruitore' and not(*)]")
    Label dailyCallsPerConsumerLabel();

    @XPath(ATTRIBUTES_SECTION + "//*[normalize-space()='Soglia giornaliera per fruitore' and not(*)]/parent::div/following-sibling::div[1]//span")
    Label dailyCallsPerConsumerValue();

    @XPath(ATTRIBUTES_SECTION + "//*[normalize-space()='Soglia giornaliera totale' and not(*)]")
    Label dailyCallsTotalLabel();

    @XPath(ATTRIBUTES_SECTION + "//*[normalize-space()='Soglia giornaliera totale' and not(*)]/parent::div/following-sibling::div[1]//span")
    Label dailyCallsTotalValue();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Soglie di chiamate API personalizzate']")
    Label customizedThresholdsTitle();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Attributi']")
    Label attributesTitle();

    // TODO controllare che sia valido
    @XPath(ATTRIBUTES_SECTION + "//*[normalize-space()='Questo e-service non richiede attributi' and not(*)]")
    Label noAttributesRequiredMessage();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Attributi Certificati']")
    Label certifiedAttributesTitle();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Attributi Verificati']")
    Label verifiedAttributesTitle();

    @XPath(ATTRIBUTES_SECTION + "//h3[normalize-space()='Attributi Dichiarati']")
    Label declaredAttributesTitle();
}

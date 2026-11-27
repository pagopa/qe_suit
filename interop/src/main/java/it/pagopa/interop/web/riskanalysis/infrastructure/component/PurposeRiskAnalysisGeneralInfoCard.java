package it.pagopa.interop.web.riskanalysis.infrastructure.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;

public interface PurposeRiskAnalysisGeneralInfoCard extends Component {
    @XPath(".//h2")
    Label title();

    @XPath(".//p[normalize-space()='E-service da associare']")
    Label eserviceLabel();

    @XPath(".//p[normalize-space()='E-service da associare']/parent::div/following-sibling::div//a")
    Link eserviceValue();

    @XPath(".//p[normalize-space()='Erogatore']")
    Label providerLabel();

    @XPath(".//p[normalize-space()='Erogatore']/parent::div/following-sibling::div[1]//span")
    Label providerValue();

    @XPath(".//p[normalize-space()='Nome della finalità']")
    Label purposeNameLabel();

    @XPath(".//p[normalize-space()='Nome della finalità']/parent::div/following-sibling::div[1]//span")
    Label purposeNameValue();

    @XPath(".//p[contains(normalize-space(), 'Descrizione')]")
    Label purposeDescLabel();

    @XPath(".//p[contains(normalize-space(), 'Descrizione ')]/parent::div/following-sibling::div[1]//span")
    Label purposeDescValue();

    @XPath(".//p[contains(normalize-space(), 'Come metterai a disposizione')]")
    Label accessLabel();

    @XPath(".//p[contains(normalize-space(), 'Come metterai a disposizione')]/parent::div/following-sibling::div[1]//span")
    Label accessValue();

    @XPath(".//p[normalize-space()='Motivazione titolo gratuito']")
    Label freeReasonLabel();

    @XPath(".//p[normalize-space()='Motivazione titolo gratuito']/parent::div/following-sibling::div[1]//span")
    Label freeReasonValue();

}

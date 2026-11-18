package it.pagopa.interop.web.riskanalysis.infrastructure.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Link;

public interface GeneralInfoCard extends Component{
    @XPath(".//div[contains(@class, 'MuiAccordionSummary-content')]//h6[contains(@class, 'MuiTypography-h6')]")
    Label accordionTitle();

    @XPath(".//p[normalize-space(.)='Nome della finalità']")
    Label purposeNameLabel();

    @XPath(".//p[normalize-space(.)='Nome della finalità']/parent::div/following-sibling::div[1]")
    Label purposeNameValue();

    @XPath(".//p[normalize-space(.)='Descrizione']")
    Label descriptionLabel();

    @XPath(".//p[normalize-space(.)='Descrizione']/parent::div/following-sibling::div[1]")
    Label descriptionValue();

    @XPath(".//p[normalize-space(.)='E-service associato']")
    Label eServiceLabel();

    @XPath(".//p[normalize-space(.)='E-service associato']/parent::div/following-sibling::div[1]//a")
    Link eServiceLink();

    @XPath(".//p[normalize-space(.)='Erogatore']")
    Label providerLabel();

    @XPath(".//p[normalize-space(.)='Erogatore']/parent::div/following-sibling::div[1]")
    Label providerValue();

    @XPath(".//h3[text()='Stima e soglie di chiamate API']")
    Label thresholdSubtitle();

    @XPath(".//p[normalize-space(.)='Stima richiesta dal fruitore']")
    Label estimatedRequestLabel();

    @XPath(".//p[normalize-space(.)='Stima richiesta dal fruitore']/parent::div/following-sibling::div[1]")
    Label estimatedRequestValue();

    @XPath(".//p[normalize-space(.)=\"Soglia per fruitore prevista dall'erogatore\"]")
    Label thresholdLabel();

    @XPath(".//p[normalize-space(.)=\"Soglia per fruitore prevista dall'erogatore\"]/parent::div/following-sibling::div[1]")
    Label thresholdValue();

    @XPath(".//p[normalize-space(.)=\"Soglia totale prevista dall'erogatore\"]")
    Label totalThresholdLabel();

    @XPath(".//p[normalize-space(.)=\"Soglia totale prevista dall'erogatore\"]/parent::div/following-sibling::div[1]")
    Label totalThresholdValue();

    @XPath(".//h3[text()='Assegnazione']")
    Label assignmentSubtitle();

    @XPath(".//p[normalize-space(.)='Data assegnazione']")
    Label assignmentDateLabel();

    @XPath(".//p[normalize-space(.)='Data assegnazione']/parent::div/following-sibling::div[1]")
    Label assignmentDateValue();

    @XPath(".//p[normalize-space(.)='Assegnata a']")
    Label assignedToLabel();

    @XPath(".//p[normalize-space(.)='Assegnata a']/parent::div/following-sibling::div[1]")
    Label assignedToValue();
}
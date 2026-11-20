package it.pagopa.interop.web.riskanalysis.infrastructure.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.infrastructure.suit.component.Label;


public interface RiskAnalysisCompleteCard extends RiskAnalysisCard {
    @XPath(".//div[contains(@class, 'MuiAccordionSummary-root') and @role='button']")
    Button header();

    @XPath(".//div[contains(@class, 'MuiAccordionSummary-content')]//h6[contains(@class, 'MuiTypography-h6')]")
    Label accordionTitle();

    @XPath(".//section//h3[contains(normalize-space(.), 'Analisi del rischio')]")
    Label title();

    @XPath(".//div[contains(@class, 'MuiChip-root')]")
    Chip statusChip();
}



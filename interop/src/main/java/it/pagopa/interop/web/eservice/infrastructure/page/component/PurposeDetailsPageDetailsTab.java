package it.pagopa.interop.web.eservice.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.TabPanel;

public interface PurposeDetailsPageDetailsTab extends TabPanel {

    @XPath(".//section[.//h2[normalize-space()='Assegnazione analisi del rischio']]")
    PurposeDetailsRiskAnalysisSection riskAnalysisSection();

    @XPath(".//section[.//h2[normalize-space()='Informazioni generali']]")
    PurposeDetailsGeneralInfoSection generalInfoSection();

    @XPath(".//section[.//h2[normalize-space()='Stima di chiamate API']]")
    PurposeDetailsApiCallsSection apiCallsSection();
}

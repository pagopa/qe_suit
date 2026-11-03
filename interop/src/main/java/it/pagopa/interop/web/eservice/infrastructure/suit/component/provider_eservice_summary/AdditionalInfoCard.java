package it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary;

import it.frontend.e2e.framework.annotation.selector.XPath;

public interface AdditionalInfoCard extends EServiceSummaryCard {

    String VERSION_DESCRIPTION = "Descrizione versione";
    String MANUAL_APPROVAL = "Attivazione manuale richieste di fruizione";

    @Override
    @XPath(".//*[contains(@class,'MuiAccordionDetails-root')]")
    AdditionalInfoContent content();

    interface AdditionalInfoContent extends Content {

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + VERSION_DESCRIPTION + "']]")
        Field versionDescription();

        @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='" + MANUAL_APPROVAL + "']]")
        Field manualApproval();
    }
}

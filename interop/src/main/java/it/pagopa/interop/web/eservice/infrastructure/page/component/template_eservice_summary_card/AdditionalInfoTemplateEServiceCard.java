package it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.EServiceSummaryCard;

@XPath(".//div[contains(@class, 'MuiAccordion-root') and .//h6[normalize-space()='Informazioni aggiuntive']]")
public interface AdditionalInfoTemplateEServiceCard extends EServiceSummaryCard {

    @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='Descrizione versione']]")
    EServiceSummaryCard.Field versionDescription();

    @XPath(".//p[contains(@class, 'MuiTypography-body2') and normalize-space()='Documentazione']")
    Label documentationLabel();

    @XPath(".//div[2]//div[contains(@class, 'MuiTypography-body2')] | ./div[2]/p[contains(@class, 'MuiTypography-body2')]")
    Label documentationValue();

    @XPath(".//div[contains(@class,'MuiStack-root')][./div/p[normalize-space(.)='Hai suggerito di approvare manualmente le richieste di fruizione?']]")
    EServiceSummaryCard.Field manualApproval();
}




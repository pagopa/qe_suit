package it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.EServiceSummaryCard;

@XPath(".//div[contains(@class, 'MuiAccordion-root') and .//h6[normalize-space()='Informazioni generali']]")
public interface GeneralInfoTemplateEServiceCard extends EServiceSummaryCard {
    @XPath(".//h6[contains(@class, 'MuiTypography-h6')]")
    Label title();

    @XPath(".//p[normalize-space()='Nome del template di e-service']")
    Label eserviceTemplateNameLabel();

    @XPath(".//p[normalize-space()='Nome del template di e-service']/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//span")
    Label eserviceTemplateNameValue();

    @XPath(".//p[normalize-space()='A chi è rivolto questo template?']")
    Label templateScopeLabel();

    @XPath(".//p[normalize-space()='A chi è rivolto questo template?']/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//span")
    Label templateScopeValue();

    @XPath(".//p[normalize-space()='Cosa permette di fare questo template?']")
    Label templateUseLabel();

    @XPath(".//p[normalize-space()='Cosa permette di fare questo template?']/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//span")
    Label templateUseValue();

    @XPath(".//p[normalize-space()='Tecnologia API']")
    Label technologyLabel();

    @XPath(".//p[normalize-space()='Tecnologia API']/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//span")
    Label technologyValue();

    @XPath(".//p[normalize-space()='Scambio dati']")
    Label exchangeDataLabel();

    @XPath(".//p[normalize-space()='Scambio dati']/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//span")
    Label exchangeDataValue();

    @XPath(".//p[normalize-space()='Eroga dati personali']")
    Label personalDataLabel();

    @XPath(".//p[normalize-space()='Eroga dati personali']/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//span")
    Label personalDataValue();

    @XPath(".//p[contains(normalize-space(), 'uso di Signal Hub?')]")
    Label signalHubUseLabel();

    @XPath(".//p[contains(normalize-space(), 'uso di Signal Hub?')]/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//span")
    Label signalHubUseValue();
}

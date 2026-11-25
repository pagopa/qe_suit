package it.pagopa.interop.web.eservice.infrastructure.page.component.generalInfo_step;


import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.SignalHubSection;

@XPath(".//form")
public interface GeneralInfoStep extends Component {

    String SUBMIT_BTN_TEXT = "Salva bozza e prosegui";

    @XPath(".//button[contains(., '" + SUBMIT_BTN_TEXT + "')]")
    Button saveDraftButton();

    EServiceTemplateCaratherizationCard eServiceTemplateCaratherizationCard();

    EServiceDetailsCard eServiceDetailsCard();

    SignalHubCard signalHubCard();

    @XPath(".//section[contains(@class, 'MuiPaper-root') and .//h2[contains(normalize-space(), 'Dettagli dell')]]")
    interface EServiceDetailsCard extends Card {
        @XPath(".//h2[contains(@class, 'MuiTypography-root') and contains(normalize-space(), 'Dettagli dell') and contains(normalize-space(), 'e-service')]")
        Label eserviceDetailsCardTitle();

        @XPath(".//section[.//h2[contains(normalize-space(), 'Dettagli dell')]]//p[normalize-space()='Queste informazioni non sono modificabili.']")
        Label subTitle();

        @XPath(".//p[normalize-space()='Scambio dati']")
        Label exchangeDataLabel();

        @XPath(".//p[normalize-space()='Scambio dati']/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//span")
        Label exchangeDataValue();

        @XPath(".//p[normalize-space()='Tecnologia API']")
        Label technologyLabel();

        @XPath(".//p[normalize-space()='Tecnologia API']/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//span")
        Label technologyValue();

        @XPath(".//p[contains(normalize-space(), 'e-service eroga o riceve dati?')]")
        Label producerOrConsumerLabel();

        @XPath(".//p[contains(normalize-space(), 'e-service eroga o riceve dati?')]/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//span")
        Label producerOrConsumerValue();

        @XPath(".//p[normalize-space()='Eroga dati personali']")
        Label personalDataLabel();

        @XPath(".//p[normalize-space()='Eroga dati personali']/ancestor::div[contains(@class, 'MuiStack-root')][1]/div[contains(@class, 'MuiBox-root')][2]//span")
        Label personalDataValue();
    }

    @XPath(".//div[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Caratterizzazione template di e-service']]")
    interface EServiceTemplateCaratherizationCard extends Card {
        @XPath(".//h2[contains(@class, 'MuiTypography-root') and normalize-space()='Caratterizzazione template di e-service']")
        Label templateCharacterizationCardTitle();

        @XPath(".//p[contains(@class, 'MuiTypography-root') and normalize-space()='Queste informazioni non sono modificabili.']")
        Label subtitle();

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
    }

    @XPath(".//div[contains(@class, 'MuiPaper-root') and .//h2[normalize-space()='Signal Hub']]")
    interface SignalHubCard extends Card {
        @XPath(".//h2[contains(@class, 'MuiTypography-root') and normalize-space()='Signal Hub']")
        Label signalHubCardTitle();

        @XPath(".//p[contains(@class, 'MuiTypography-root') and normalize-space()='Queste informazioni non sono modificabili.']")
        Label subtitle();

        @XPath(".//span[@id='isSignalHubEnabled-label']/span[1]")
        Label switchPrimaryLabel();

        @XPath(".//span[@id='isSignalHubEnabled-label']/p[contains(normalize-space(), 'Signal Hub invia')]")
        Label switchDescription();

        @XPath(".//input[@name='isSignalHubEnabled']")
        Switch switchSignalHub();
    }
}

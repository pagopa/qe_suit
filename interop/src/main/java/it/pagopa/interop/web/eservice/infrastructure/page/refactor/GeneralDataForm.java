package it.pagopa.interop.web.eservice.infrastructure.page.refactor;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.common.eservice.domain.EServiceMode;

@XPath(".//form")
public interface GeneralDataForm extends Component {

    AboutEServiceSection aboutEServiceSection();

    EServiceDetailsSection eServiceDetailsSection();

    DelegationsSection delegationsSection();

    SignalHubSection signalHubSection();

    @XPath(".//button[contains(., 'Salva bozza e prosegui')]")
    Button saveDraftButton();

    default GeneralDataForm setEServiceName(String name) {
        aboutEServiceSection().eServiceNameInput().fill(name);
        return this;
    }

    default GeneralDataForm setEServiceDescription(String description) {
        aboutEServiceSection().eServiceDescriptionInput().fill(description);
        return this;
    }

    default GeneralDataForm setUsePersonalData(boolean usePersonalData) {
        switch (getEServiceMode()) {
            case DELIVER -> eServiceDetailsSection().personalData().selectLike(usePersonalData ? "Eroga dati personali" : "Non eroga dati personali");
            case RECEIVE -> eServiceDetailsSection().personalData().selectLike(usePersonalData ? "Riceve dati personali" : "Non riceve dati personali");
        }
        return this;
    }

    default GeneralDataForm saveDraft(){
        saveDraftButton().click();
        return this;
    }

    default EServiceMode getEServiceMode() {
        return eServiceDetailsSection().mode().getSelected().contains("Eroga") ? EServiceMode.DELIVER : EServiceMode.RECEIVE;
    }

    @XPath(".//*[contains(@class, 'MuiPaper-root') and (.//h2)[1][normalize-space(.)='Informazioni sull’e-service']]")
    interface AboutEServiceSection extends Component {
        @XPath(".//h2")
        Readable<String> title();

        @XPath(".//p")
        Readable<String> description();

        @XPath(".//*[@id='name']")
        TextField eServiceNameInput();

        @XPath(".//*[@id='description']")
        TextField eServiceDescriptionInput();
    }

    @XPath(".//*[contains(@class, 'MuiPaper-root') and (.//h2)[1][normalize-space(.)=\"Dettagli dell'e-service\"]]")
    interface EServiceDetailsSection extends Component {

        @XPath(".//h2")
        Readable<String> title();

        @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'Questi dati non saranno più modificabili')]")
        Alert generalAlert();

        @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='Sincrono (standard)'] and .//*[text()='Asincrono / massivo (in differita)']]")
        RadioGroup asyncExchange();

        @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='REST'] and .//*[text()='SOAP']]")
        RadioGroup technology();

        @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and .//*[text()='Eroga'] and .//*[text()='Riceve']]")
        RadioGroup mode();

        @XPath(".//div[contains(@class, 'MuiRadioGroup-root') and ((.//*[text()='Eroga dati personali'] and .//*[text()='Non eroga dati personali']) or (.//*[text()='Riceve dati personali'] and .//*[text()='Non riceve dati personali']))]")
        RadioGroup personalData();

        @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'SOAP non permette di abilitare il download a blocchi')]")
        Alert soapAsyncAlert();

        @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'Per gli scambi asincroni è necessario collegare un portachiavi all’e-service')]")
        Alert keychainAlert();
    }

    @XPath(".//*[contains(@class, 'MuiPaper-root') and (.//h2)[1][normalize-space(.)='Deleghe']]")
    interface DelegationsSection extends Component {
        @XPath(".//h2")
        Readable<String> title();

        @XPath(".//p")
        Readable<String> description();
    }

    @XPath(".//*[contains(@class, 'MuiPaper-root') and (.//h2)[1][normalize-space(.)='Signal Hub: servizio di notifica della variazione dei dati']]")
    interface SignalHubSection extends Component {
        @XPath(".//h2")
        Readable<String> title();

        @XPath(".//p")
        Readable<String> description();

        @XPath(".//div[contains(@class, 'MuiAlert-root') and contains(., 'Prima di procedere, assicurati che il documento di designazione')]")
        Alert generalAlert();

        Switch signalHubSwitch();
    }
}

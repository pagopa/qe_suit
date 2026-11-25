package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;

@Url("${interop.web.producer-e-service-template-list}/${eserviceTemplateId}/${versionId}/modifica")
public interface ProducerEServiceTemplateEditPage extends Page {
    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Label pageTitle();

    @XPath(".//h4/following-sibling::p")
    Label pageDescription();

    @XPath(".//div[contains(@class, 'MuiAlert-root')]")
    Alert infoAlert();

    Stepper stepper();

    @XPath(".//form")
    GeneralDataSection generalDataSection();

    @XPath(".//form")
    ThresholdsAndAttributesSection thresholdsAndAttributesSection();

    @XPath(".//form")
    TechnicalSpecsSection technicalSpecsSection();

    @XPath(".//form")
    AdditionalInfoSection additionalInfoSection();

    @XPath(".//button[@type='submit']")
    Button nextStepButton();

    interface GeneralDataSection extends Component {

        // Card 1: Caratterizzazione template di e-service
        @XPath(".//h2[normalize-space(.)='Caratterizzazione template di e-service'] | .//h3[normalize-space(.)='Caratterizzazione template di e-service'] | .//h6[normalize-space(.)='Caratterizzazione template di e-service']")
        Label templateCharacterizationCardTitle();

        @XPath(".//input[@name='intendedTarget' or @type='radio'][1]")
        TextField intendedTargetInput();

        // Card 2: Dettagli dell'e-service
        @XPath(".//h2[normalize-space(.)=\"Dettagli dell'e-service\"] | .//h3[normalize-space(.)=\"Dettagli dell'e-service\"] | .//h6[normalize-space(.)=\"Dettagli dell'e-service\"]")
        Label eserviceDetailsCardTitle();

        @XPath(".//input[@name='name']")
        TextField templateNameInput();

        @XPath(".//textarea[@name='description']")
        TextField templateDescriptionInput();

        @XPath(".//input[@name='technology' and @value='REST']")
        TextField restTechnologyRadio();

        @XPath(".//input[@name='technology' and @value='SOAP']")
        TextField soapTechnologyRadio();

        // Card 3: Signal Hub
        @XPath(".//h2[normalize-space(.)='Signal Hub'] | .//h3[normalize-space(.)='Signal Hub'] | .//h6[normalize-space(.)='Signal Hub']")
        Label signalHubCardTitle();

        @XPath(".//input[@name='isSignalHubEnabled' and @value='true']")
        TextField signalHubEnabledRadio();

        @XPath(".//input[@name='isSignalHubEnabled' and @value='false']")
        TextField signalHubDisabledRadio();
    }

    interface ThresholdsAndAttributesSection extends Component {

        // Card 1: Soglie di chiamate API
        @XPath(".//h2[normalize-space(.)='Soglie di chiamate API'] | .//h3[normalize-space(.)='Soglie di chiamate API'] | .//h6[normalize-space(.)='Soglie di chiamate API']")
        Label apiThresholdsCardTitle();

        @XPath(".//input[@name='dailyCallsPerConsumer']")
        TextField dailyCallsPerConsumerInput();

        @XPath(".//input[@name='dailyCallsTotal']")
        TextField dailyCallsTotalInput();

        // Card 2: Requisiti di accesso
        @XPath(".//h2[normalize-space(.)='Requisiti di accesso'] | .//h3[normalize-space(.)='Requisiti di accesso'] | .//h6[normalize-space(.)='Requisiti di accesso']")
        Label accessRequirementsCardTitle();

        @XPath(".//button[contains(normalize-space(.), 'Aggiungi')]")
        Button addAttributeButton();
    }

    interface TechnicalSpecsSection extends Component {

        // Card 1: Interfaccia
        @XPath(".//h2[normalize-space(.)='Interfaccia'] | .//h3[normalize-space(.)='Interfaccia'] | .//h6[normalize-space(.)='Interfaccia']")
        Label interfaceCardTitle();

        @XPath(".//input[@type='file']")
        TextField interfaceFileInput();

        // Card 2: Voucher
        @XPath(".//h2[normalize-space(.)='Voucher'] | .//h3[normalize-space(.)='Voucher'] | .//h6[normalize-space(.)='Voucher']")
        Label voucherCardTitle();

        @XPath(".//input[@name='voucherLifespan']")
        TextField voucherLifespanInput();

        @XPath(".//input[@name='audience']")
        TextField audienceInput();
    }

    interface AdditionalInfoSection extends Component {

        // Card 1: Descrizione della versione
        @XPath(".//h2[normalize-space(.)='Descrizione della versione'] | .//h3[normalize-space(.)='Descrizione della versione'] | .//h6[normalize-space(.)='Descrizione della versione']")
        Label versionDescriptionCardTitle();

        @XPath(".//textarea[@name='versionDescription']")
        TextField versionDescriptionInput();

        // Card 2: Documentazione
        @XPath(".//h2[normalize-space(.)='Documentazione'] | .//h3[normalize-space(.)='Documentazione'] | .//h6[normalize-space(.)='Documentazione']")
        Label documentationCardTitle();

        @XPath(".//button[contains(normalize-space(.), 'Allega')]")
        Button attachDocumentButton();

        // Card 3: Gestione richieste
        @XPath(".//h2[normalize-space(.)='Gestione richieste'] | .//h3[normalize-space(.)='Gestione richieste'] | .//h6[normalize-space(.)='Gestione richieste']")
        Label requestManagementCardTitle();

        @XPath(".//input[@name='agreementApprovalPolicy']")
        TextField agreementApprovalPolicyInput();
    }

    @Override
    default void assertLoaded() {
    }
}
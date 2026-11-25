package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_stepper.GeneralInfoStep;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_stepper.ThresholdsAndAttributesStep;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.TechnicalSpecsCard;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;

@Url("${interop.web.base-url}/erogazione/template-eservice/${eserviceTemplateId}/${versionId}/modifica")
public interface ProducerEServiceTemplateEditPage extends Page {
    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Label pageTitle();

    @XPath(".//p[contains(@class, 'MuiTypography-body1')]")
    Label pageDescription();

    @XPath(".//div[contains(@class, 'MuiAlert-root')]")
    Alert infoAlert();

    Stepper stepper();

    GeneralInfoStep generalInfoStep();

    ThresholdsAndAttributesStep thresholdsAndAttributesStep();

    TechnicalSpecsCard technicalSpecsCard();

    @XPath(".//form")
    AdditionalInfoSection additionalInfoSection();


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
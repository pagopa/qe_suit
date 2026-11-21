package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Stepper;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.additional_info_form.EServiceTemplateAdditionalInfoForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.EServiceTemplateGeneralDataForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.technical_specs_form.EServiceTemplateTechnicalSpecsForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.threshold_attribute_form.ThresholdAndAttributeForm;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;

import static org.assertj.core.api.Assertions.assertThat;

@Url("${interop.web.producer-e-service-template-list}/${eserviceTemplateId}/${versionId}/modifica")
public interface ProducerEServiceTemplateUpdatePage extends Page {

    String BTN_BACK_TEMPLATES = "Torna ai template";

    @XPath(".//a[normalize-space(.)='" + BTN_BACK_TEMPLATES + "'] | .//button[normalize-space(.)='" + BTN_BACK_TEMPLATES + "']")
    Button backToTemplatesButton();

    Breadcrumbs breadcrumbs();

    @XPath(".//h1")
    Label title();

    @XPath(".//h1/following-sibling::p")
    Label description();

    @XPath(".//*[not(*) and normalize-space(.)='*Campi obbligatori']")
    Label requiredFieldsLabel();

    @XPath(".//*[@data-testid='desktopWizardStepper']")
    Stepper upsertStepper();

    EServiceTemplateGeneralDataForm generalDataForm();

    ThresholdAndAttributeForm thresholdAndAttributeForm();

    EServiceTemplateTechnicalSpecsForm technicalSpecsForm();

    EServiceTemplateAdditionalInfoForm additionalInfoForm();

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> title().read(),
                name -> name != null && !name.isBlank(),
                "Producer E-Service Template Update Page is not loaded yet. Page title is blank"
        );
        assertThat(breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumbs")
                .containsExactly("Erogazione", "I miei template e-service", "Modifica Template E-Service");
        assertThat(upsertStepper().get(Gettable.FindPolicy.PRESENT))
                .as("The e-service template update stepper must be present")
                .isPresent();
    }
}


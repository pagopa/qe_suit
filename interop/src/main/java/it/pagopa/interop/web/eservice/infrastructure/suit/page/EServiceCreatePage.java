package it.pagopa.interop.web.eservice.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Stepper;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.additional_info_form.AdditionalInfoForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.GeneralDataForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.technical_specs_form.TechnicalSpecsForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.threshold_attribute_form.ThresholdAndAttributeForm;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;

import static org.assertj.core.api.Assertions.assertThat;


@Url("${interop.web.base-url}/erogazione/e-service/crea/")
public interface EServiceCreatePage extends Page {
    @XPath(".//h1")
    Label title();

    Breadcrumbs breadcrumbs();

    GeneralDataForm generalDataForm();

    ThresholdAndAttributeForm thresholdAndAttributeForm();

    TechnicalSpecsForm technicalSpecsForm();

    AdditionalInfoForm additionalInfoForm();

    @XPath(".//*[@data-testid='desktopWizardStepper']")
    Stepper upsertStepper();

    @Override
    default void assertLoaded() {
        title().readAndAssert("Crea e-service");
        assertThat(upsertStepper().get(FindPolicy.PRESENT))
                .as("The e-service creation stepper must be present").isPresent();
    }
}

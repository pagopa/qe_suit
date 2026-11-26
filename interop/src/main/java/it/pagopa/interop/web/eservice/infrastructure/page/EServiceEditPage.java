package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.additional_info_form.AdditionalInfoForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.general_data_form.GeneralDataForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.technical_specs_form.TechnicalSpecsForm;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.threshold_attribute_form.ThresholdAndAttributeForm;
import it.pagopa.interop.web.infrastructure.config.suit.component.Breadcrumbs;
import it.pagopa.utils.async.PollingUtils;


@Url("${interop.web.base-url}/erogazione/e-service/${eserviceId}/${descriptorId}/modifica")
public interface EServiceEditPage extends Page {

    @XPath(".//h1")
    Readable<String> pageTitle();

    Breadcrumbs breadcrumbs();

    GeneralDataForm generalDataForm();

    ThresholdAndAttributeForm thresholdAndAttributeForm();

    TechnicalSpecsForm technicalSpecsForm();

    AdditionalInfoForm additionalInfoForm();

    @XPath(".//button[normalize-space()='Salva bozza e prosegui']")
    Button saveDraftButton();

    default void saveDraftAndProceed() {
        saveDraftButton().click();
    }

    @Override
    default void assertLoaded() {
        PollingUtils.pollUntil(
                () -> breadcrumbs().getLastItemText().equals("Modifica e-service") && !pageTitle().read().isBlank(),
                isLoaded -> isLoaded,
                "EService edit page did not load within the expected time"
        );
    }
}

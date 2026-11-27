package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.*;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_stepper.AdditionalInfoStep;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_stepper.GeneralInfoStep;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_stepper.TechnicalSpecsStep;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_stepper.ThresholdsAndAttributesStep;
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

    TechnicalSpecsStep technicalSpecsStep();

    AdditionalInfoStep additionalInfoStep();

    @Override
    default void assertLoaded() {
    }
}
package it.pagopa.interop.web.page.eservice_creation;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.web.component.Button;
import it.pagopa.interop.web.page.eservice_creation.step.AdditionalDataStep;
import it.pagopa.interop.web.page.eservice_creation.step.GeneralDataStep;
import it.pagopa.interop.web.page.eservice_creation.step.ThresholdAndAttributeStep;
import it.pagopa.interop.web.page.eservice_creation.step.technical.TechnicalSpecStep;

@Url("${interop.web.base-url}/erogazione/e-service/crea/")
public interface EServiceCreationPage extends Page {

    @XPath(".//h1")
    Readable<String> title();

    @XPath(".//button[contains(., 'Salva bozza e prosegui')]")
    Button saveDraftButton();

    @XPath(".//button[contains(., 'Vai al riepilogo')]")
    Button summaryButton();

    @XPath(".//button[contains(., 'Pubblica')]")
    Button publishButton();

    GeneralDataStep generalDataStep();

    ThresholdAndAttributeStep thresholdAndAttributeStep();

    TechnicalSpecStep technicalSpecificationStep();

    AdditionalDataStep additionalInformationStep();

    @Override
    default void assertLoaded() {
        generalDataStep().assertLoaded();
        title().readAndAssert("Crea e-service");
    }
}

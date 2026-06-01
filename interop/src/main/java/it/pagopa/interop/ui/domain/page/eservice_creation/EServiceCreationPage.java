package it.pagopa.interop.ui.domain.page.eservice_creation;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.ui.domain.component.Button;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.AdditionalInformationStepComponent;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.GeneralDataStepComponent;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.technical.TechnicalSpecificationStepComponent;

@Url("${interop.web.base-url}/erogazione/e-service/crea/")
public interface EServiceCreationPage extends Page {

    @XPath(".//h1")
    Readable<String> title();

    @XPath(".//button[contains(., 'Salva bozza e prosegui')]")
    Button saveDraftButton();

    @XPath(".//button[contains(., 'Vai al riepilogo')]")
    Button summeryButton();

    @XPath(".//button[contains(., 'Pubblica')]")
    Button publishButton();

    GeneralDataStepComponent generalDataStep();

    TechnicalSpecificationStepComponent technicalSpecificationStep();

    AdditionalInformationStepComponent additionalInformationStep();

    @Override
    default void assertLoaded() {
        generalDataStep().assertLoaded();
        title().readAndAssert("Crea e-service");
    }
}

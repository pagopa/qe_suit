package it.pagopa.interop.web.pages.eservice_creation;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.web.component.Button;
import it.pagopa.interop.web.pages.eservice_creation.component.GeneralInformationStepComponent;
import it.pagopa.interop.web.pages.eservice_creation.component.TechnicalSpecificationStepComponent;

@Url("${interop.web.base-url}/erogazione/e-service/crea/")
public interface EServiceCreationPage extends Page {

    @XPath(".//h1")
    Readable<String> title();

    GeneralInformationStepComponent generalInformationStep();

    TechnicalSpecificationStepComponent technicalSpecificationStep();

    @XPath(".//button[contains(., 'Salva bozza e prosegui')]")
    Button saveDraftButton();

    default EServiceCreationPage saveDraftAndContinue() {
        saveDraftButton().click();
        return this;
    }

    default EServiceCreationPage fillGeneralInformation(EServiceSeed seed) {
        generalInformationStep().fillGeneralInformation(seed);
        saveDraftAndContinue();
        return this;
    }

    default EServiceCreationPage fillTechnicalSpecification(TechnicalSpecificationStepComponent.TechnicalSpecificationStepSeed seed) {
        technicalSpecificationStep().assertLoaded();
        technicalSpecificationStep().fillTechnicalSpecification(seed);
        saveDraftAndContinue();
        return this;
    }

    default EServiceCreationPage skipThresholdAndAttribute() {
        //TODO: assert loaded
        saveDraftAndContinue();
        return this;
    }

    default EServiceCreationPage skipTechnicalSpecification() {
        technicalSpecificationStep().assertLoaded();
        saveDraftButton().click();
        return this;
    }

    @Override
    default void assertLoaded() {
        generalInformationStep().assertLoaded();
        title().readAndAssert("Crea e-service");
    }
}

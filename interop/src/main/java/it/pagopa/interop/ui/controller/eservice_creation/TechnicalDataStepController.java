package it.pagopa.interop.ui.controller.eservice_creation;

import io.cucumber.java.en.When;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.technical.TechnicalSpecificationStepComponent;

public class TechnicalDataStepController {

    private final TechnicalSpecificationStepComponent technicalSpecificationStep;

    public TechnicalDataStepController(EServiceCreationPage eServiceCreationPage) {
        technicalSpecificationStep = eServiceCreationPage.technicalSpecificationStep();
    }

    @When("cancella i valori da tutti gli input delle specifiche tecniche")
    public void cleanInput() {

    }
}

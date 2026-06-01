package it.pagopa.interop.ui.controller.eservice_creation;

import io.cucumber.java.en.When;
import it.pagopa.interop.ui.domain.model.eservice_creation.TechnicalSpecModel;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.technical.TechnicalSpecificationStepComponent;
import it.pagopa.interop.ui.service.eservice_creation.TechnicalDataService;

public class TechnicalDataCreationController {

    private final TechnicalSpecificationStepComponent technicalSpecificationStep;
    private final TechnicalDataService technicalDataService;

    public TechnicalDataCreationController(EServiceCreationPage eServiceCreationPage, TechnicalDataService technicalDataService) {
        technicalSpecificationStep = eServiceCreationPage.technicalSpecificationStep();
        this.technicalDataService = technicalDataService;
    }

    @When("cancella i valori da tutti gli input delle specifiche tecniche")
    public void cleanInput() {
        technicalDataService.fill(TechnicalSpecModel.buildEmpty());
    }
}

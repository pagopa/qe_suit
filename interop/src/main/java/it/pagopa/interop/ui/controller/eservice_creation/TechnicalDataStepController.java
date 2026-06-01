package it.pagopa.interop.ui.controller.eservice_creation;

import io.cucumber.java.en.When;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.ui.domain.page.eservice_creation.step.technical.TechnicalSpecificationStepComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TechnicalDataStepController {

    private final EServiceCreationPage eServiceCreationPage;
    private final TechnicalSpecificationStepComponent technicalSpecificationStep = eServiceCreationPage.technicalSpecificationStep();

    @When("cancella i valori da tutti gli input delle specifiche tecniche")
    public void cleanInput(){

    }
}

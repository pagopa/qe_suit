package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.journey.application.InteropJourney;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceTemplateRiskAnalysisSteps {
    private final InteropJourney interopJourney;

    @Given("un'analisi del rischio completa associata al template")
    public void completeRiskAnalysisAssociatedToTemplate() {
        interopJourney.addTemplateRiskAnalysis();
    }

    @When("l'erogatore tenta di aggiungere al template un'analisi del rischio con il nome dell'analisi esistente")
    public void attemptAddTemplateRiskAnalysisWithExistingName() {
        // TODO
        // Usato negli scenari con id ERROR_125
    }
}

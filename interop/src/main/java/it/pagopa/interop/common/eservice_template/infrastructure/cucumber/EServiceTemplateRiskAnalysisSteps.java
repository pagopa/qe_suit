package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceTemplateRiskAnalysisSteps {
    private final InteropJourney interopJourney;
    private final EntityStore entityStore;
    private final EServiceTemplateUseCase eServiceTemplateUseCase;
    private final AuthSession authSession;

    @Given("un'analisi del rischio completa associata al template")
    public void completeRiskAnalysisAssociatedToTemplate() {
        interopJourney.addTemplateRiskAnalysis();
    }

    @When("l'erogatore tenta di aggiungere al template un'analisi del rischio con il nome dell'analisi esistente")
    public void attemptAddTemplateRiskAnalysisWithExistingName() {
        // Usato negli scenari con id ERROR_125
        EServiceTemplate template = entityStore.getLastOrThrow(EServiceTemplate.class);
        String existingName = template.getRiskAnalyses() != null && !template.getRiskAnalyses().isEmpty()
                ? template.getRiskAnalyses().get(0).getName()
                : entityStore.getLastOrThrow(EServiceRiskAnalysis.class).getName();
        eServiceTemplateUseCase.attemptAddRiskAnalysisWithName(
                authSession.getTenant(), template, existingName
        );
    }
}

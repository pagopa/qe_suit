package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceRiskAnalysisUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceRiskAnalysisAliasConfiguration;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceRiskAnalysisSteps {
    private final InteropJourney interopJourney;
    private final EntityStore entityStore;
    private final EServiceRiskAnalysisUseCase eServiceRiskAnalysisUseCase;
    private final AuthSession authSession;

    @Given("le seguenti analisi del rischio complete associate all'e-service:")
    public void completeRiskAnalysesAssociatedToEService(List<EServiceRiskAnalysisAliasConfiguration> configurations) {
        // Usato negli scenari con id ERROR_047
        validateConfigurations(configurations);
        for (EServiceRiskAnalysisAliasConfiguration configuration : configurations) {
            interopJourney.addRiskAnalysis(configuration.getName(), configuration.getAlias());
        }
    }

    private void validateConfigurations(List<EServiceRiskAnalysisAliasConfiguration> configurations) {
        if (configurations == null || configurations.isEmpty()) {
            throw new IllegalArgumentException("At least one risk analysis configuration is required");
        }
        Set<String> seenAliases = new HashSet<>();
        for (EServiceRiskAnalysisAliasConfiguration configuration : configurations) {
            if (configuration == null
                    || configuration.getAlias() == null
                    || configuration.getAlias().isBlank()
                    || !seenAliases.add(configuration.getAlias())) {
                throw new IllegalArgumentException("Risk analysis aliases must be present and unique");
            }
            if (entityStore.getByAlias(configuration.getAlias(), EServiceRiskAnalysis.class).isPresent()) {
                throw new IllegalArgumentException(
                        "Risk analysis alias is already registered: " + configuration.getAlias()
                );
            }
            if (configuration.getName() == null || configuration.getName().isBlank()) {
                throw new IllegalArgumentException("Risk analysis name must not be blank");
            }
        }
    }

    @When("l'erogatore tenta di aggiungere all'e-service un'analisi del rischio con la risposta a testo libero {string}")
    public void attemptAddRiskAnalysisWithFreeTextAnswer(String answer) {
        // Usato negli scenari con id ERROR_202
        EService eService = entityStore.getLastOrThrow(EService.class);
        eServiceRiskAnalysisUseCase.attemptAddRiskAnalysisWithFreeTextAnswer(
                authSession.getTenant(), eService, answer
        );
    }

    @When("l'erogatore tenta di rinominare l'analisi del rischio {string} dell'e-service con il nome di {string}")
    public void attemptRenameEServiceRiskAnalysis(String sourceAlias, String targetNameAlias) {
        // Usato negli scenari con id ERROR_047
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceRiskAnalysis source = entityStore.getByAliasOrThrow(sourceAlias, EServiceRiskAnalysis.class);
        EServiceRiskAnalysis target = entityStore.getByAliasOrThrow(targetNameAlias, EServiceRiskAnalysis.class);
        eServiceRiskAnalysisUseCase.attemptRenameRiskAnalysis(
                authSession.getTenant(), eService, source, target.getName()
        );
    }

    @When("l'erogatore tenta di aggiungere all'e-service un'analisi del rischio con il nome di quella esistente")
    public void attemptAddRiskAnalysisWithExistingName() {
        // Usato negli scenari con id ERROR_046
        EService eService = entityStore.getLastOrThrow(EService.class);
        EServiceRiskAnalysis existing = entityStore.getLast(EServiceRiskAnalysis.class)
                .orElseGet(() -> eServiceRiskAnalysisUseCase.getRiskAnalysis(eService));
        if (existing == null || existing.getName() == null || existing.getName().isBlank()) {
            throw new IllegalStateException("No existing risk analysis found for e-service " + eService.getId());
        }
        eServiceRiskAnalysisUseCase.attemptAddRiskAnalysisWithName(
                authSession.getTenant(), eService, existing.getName()
        );
    }
}

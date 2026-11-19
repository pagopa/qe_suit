package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceRiskAnalysisAliasConfiguration;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceRiskAnalysisSteps {
    private final InteropJourney interopJourney;
    private final EntityStore entityStore;

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
}

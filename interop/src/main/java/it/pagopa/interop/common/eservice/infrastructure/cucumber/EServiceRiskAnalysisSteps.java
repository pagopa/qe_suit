package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice.application.EServiceArchivingUseCase;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceRiskAnalysisAliasConfiguration;
import it.pagopa.interop.common.kernel.context.AuthSession;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceRiskAnalysisSteps {
    private final EServiceArchivingUseCase archiving;
    private final AuthSession session;

    @Given("le seguenti analisi del rischio complete associate all'e-service:")
    public void completeRiskAnalysesAssociatedToEService(List<EServiceRiskAnalysisAliasConfiguration> configurations) {
        // TODO
    }
}

package it.pagopa.interop.bff.support;

import it.pagopa.interop.bff.service.PurposeTestService;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.cucumber.context.UserContext;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceRiskAnalysisSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.instancio.Select.field;

@Component
@RequiredArgsConstructor
public class RiskAnalysisSeedFactory {

    private final RiskAnalysisDataInitializer riskAnalysisDataInitializer;
    private final UserContext userContext;
    private final PurposeTestService purposeService;

    public EServiceRiskAnalysisSeed from(String version, Map<String, List<String>> answers) {
        RiskAnalysisFormSeed form = new RiskAnalysisFormSeed()
                .version(version)
                .answers(answers);

        return Instancio.of(EServiceRiskAnalysisSeed.class)
                .set(field(EServiceRiskAnalysisSeed::getName), "risk-analysis-" + UUID.randomUUID())
                .set(field(EServiceRiskAnalysisSeed::getRiskAnalysisForm), form)
                .create();
    }

    public EServiceRiskAnalysisSeed latest(boolean completed) {
        Tenant currentTenant = userContext.getTenant();

        Map<String, List<String>> answers =
                riskAnalysisDataInitializer.getTemplateForTenant(currentTenant, completed);

        RiskAnalysisFormConfig config = purposeService
                .retrieveLatestRiskAnalysisConfiguration(currentTenant.getTenantType())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .getResponse()
                .getBody();

        if (config == null) {
            throw new IllegalStateException(
                    "Risk analysis configuration is not available for tenant kind: "
                            + currentTenant.getTenantType()
            );
        }

        return from(config.getVersion(), answers);
    }
}
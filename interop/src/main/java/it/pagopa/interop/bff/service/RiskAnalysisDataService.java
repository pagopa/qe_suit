package it.pagopa.interop.bff.service;

import it.pagopa.interop.bff.support.RiskAnalysisDataInitializer;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.TenantKind;
import it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysis;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.common.cucumber.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RiskAnalysisDataService {

    private final PurposesApi purposesApi;
    private final UserContext userContext;
    private final RiskAnalysisDataInitializer initializer;

    public RiskAnalysis createRiskAnalysis() {
        return createRiskAnalysis(true);
    }

    public RiskAnalysis createRiskAnalysis(boolean completed) {
        Tenant currentTenant = userContext.getTenant();
        String templateKey = resolveTemplateKey(currentTenant);

        RiskAnalysisDataInitializer.RiskAnalysisTemplate template = initializer
                .getRiskAnalysisData().get(templateKey);

        if (template == null) {
            throw new IllegalStateException("No risk analysis template for: " + templateKey);
        }

        Map<String, java.util.List<String>> answers = completed
                ? template.completed()
                : template.uncompleted();

        RiskAnalysisFormSeed seed = buildRiskAnalysisFormSeed(currentTenant, answers);

        String title = "risk-analysis-" + UUID.randomUUID().toString().substring(0, 8);
        return null;
    }

    private RiskAnalysisFormSeed buildRiskAnalysisFormSeed(Tenant tenant, Map<String, java.util.List<String>> answers) {
        it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind tenantKind = it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind.fromValue(tenant.getTenantType().name());
        RiskAnalysisFormConfig config = purposesApi.retrieveLatestRiskAnalysisConfiguration(tenantKind);

        return new RiskAnalysisFormSeed()
                .version(config.getVersion())
                .answers(answers);
    }

    private String resolveTemplateKey(Tenant tenant) {
        return tenant.getTenantType() == TenantKind.PA ? "PA" : "Privato/GSP";
    }
}
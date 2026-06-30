package it.pagopa.interop.bff.service;

import it.pagopa.interop.bff.support.RiskAnalysisDataInitializer;
import it.pagopa.interop.common.contract.model.risk_analysis.RiskAnalysisForm;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.TenantKind;
import it.pagopa.interop.common.cucumber.context.UserContext;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//TODO: da eliminare
public class RiskAnalysisBffService {

    private final PurposesApi purposesApi;
    private final UserContext userContext;
    private final RiskAnalysisDataInitializer initializer;

    public RiskAnalysisForm createRiskAnalysis() {
        return createRiskAnalysis(true);
    }

    public RiskAnalysisForm createRiskAnalysis(boolean completed) {
        Tenant currentTenant = userContext.getTenant();
        String templateKey = resolveTemplateKey(currentTenant);

        RiskAnalysisDataInitializer.RiskAnalysisTemplate template = null;

        if (template == null) {
            throw new IllegalStateException("No risk analysis template for: " + templateKey);
        }

        Map<String, java.util.List<String>> answers = completed
                ? template.completed()
                : template.uncompleted();

        RiskAnalysisFormSeed seed = buildRiskAnalysisFormSeed(currentTenant, answers);

        String title = "risk-analysis-" + UUID.randomUUID().toString().substring(0, 8);
        //TODO: da completare
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

    public RiskAnalysisFormSeed buildRiskAnalysisSeed(boolean completed) {
        Tenant currentTenant = userContext.getTenant();
        String templateKey = resolveTemplateKey(currentTenant);

        RiskAnalysisDataInitializer.RiskAnalysisTemplate template = null;

        if (template == null) {
            throw new IllegalStateException("No risk analysis template for: " + templateKey);
        }

        Map<String, java.util.List<String>> answers = completed
                ? template.completed()
                : template.uncompleted();

        return buildRiskAnalysisFormSeed(currentTenant, answers);
    }
}
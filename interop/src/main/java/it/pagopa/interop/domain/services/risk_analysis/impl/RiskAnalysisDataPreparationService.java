package it.pagopa.interop.domain.services.risk_analysis.impl;

import it.pagopa.interop.domain.enums.Tenant;
import it.pagopa.interop.domain.enums.TenantType;
import it.pagopa.interop.domain.model.RiskAnalysis;
import it.pagopa.interop.domain.services.risk_analysis.RiskAnalysisDataInitializer;
import it.pagopa.interop.domain.services.risk_analysis.RiskAnalysisService;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormConfig;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantKind;
import it.pagopa.interop.infrastructure.client.auth.context.user.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RiskAnalysisDataPreparationService implements RiskAnalysisService {

    private final PurposesApi purposesApi;
    private final CurrentUserContext currentUserContext;
    private final RiskAnalysisDataInitializer initializer;

    @Override
    public RiskAnalysis createRiskAnalysis() {
        return createRiskAnalysis(true);
    }

    @Override
    public RiskAnalysis createRiskAnalysis(boolean completed) {
        Tenant currentTenant = currentUserContext.getTenant();
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
        return new RiskAnalysis(title, seed);
    }

    private RiskAnalysisFormSeed buildRiskAnalysisFormSeed(Tenant tenant, Map<String, java.util.List<String>> answers) {
        TenantKind tenantKind = TenantKind.fromValue(tenant.getTenantType().name());
        RiskAnalysisFormConfig config = purposesApi.retrieveLatestRiskAnalysisConfiguration(tenantKind);

        return new RiskAnalysisFormSeed()
                .version(config.getVersion())
                .answers(answers);
    }

    private String resolveTemplateKey(Tenant tenant) {
        return tenant.getTenantType() == TenantType.PA ? "PA" : "Privato/GSP";
    }
}
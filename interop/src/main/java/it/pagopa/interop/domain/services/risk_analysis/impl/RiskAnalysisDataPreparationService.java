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
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
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
        TenantKind tenantKind = TenantKind.fromValue(currentTenant.getTenantType().name());

        String templateKey = resolveTemplateKey(currentTenant);

        RiskAnalysisDataInitializer.RiskAnalysisTemplate template = initializer
                .getRiskAnalysisData().get(templateKey);
        if (template == null) {
            throw new IllegalStateException("No risk analysis template for: " + templateKey);
        }

        RiskAnalysisDataInitializer.RiskAnalysisAnswers answers = completed
                ? template.completed()
                : template.uncompleted();

        RiskAnalysisFormConfig config = purposesApi.retrieveLatestRiskAnalysisConfiguration(tenantKind);

        RiskAnalysisFormSeed seed = new RiskAnalysisFormSeed()
                .version(config.getVersion())
                .answers(answers.toMap());

        String title = "risk-analysis-" + UUID.randomUUID().toString().substring(0, 8);
        return new RiskAnalysis(title, seed);
    }

    private String resolveTemplateKey(Tenant tenant) {
        return tenant.getTenantType().equals(TenantType.PA) ? "PA" : "Privato/GSP";
    }
}
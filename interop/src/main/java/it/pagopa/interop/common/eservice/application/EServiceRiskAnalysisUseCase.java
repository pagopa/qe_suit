package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataInitializer;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EServiceRiskAnalysisUseCase {

    private final PurposeUseCase purposeUseCase;
    private final EServiceRiskAnalysisGateway riskAnalysisGateway;
    private final RiskAnalysisDataInitializer riskAnalysisDataInitializer;

    public EServiceRiskAnalysis addLatestRiskAnalysis(Tenant tenant, EService eService, boolean completed) {
        RiskAnalysisFormConfig latestConfig = purposeUseCase.getLatestRiskAnalysisConfig(tenant);
        Map<String, List<String>> answers =
                riskAnalysisDataInitializer.getTemplateForTenant(tenant, completed);

        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(answers)
                .build();

        return riskAnalysisGateway.addRiskAnalysis(eService.getRef(), form);
    }

    public EServiceRiskAnalysis getRiskAnalysis(EService eService) {
        return riskAnalysisGateway.getRiskAnalysis(eService.getRef());
    }
}

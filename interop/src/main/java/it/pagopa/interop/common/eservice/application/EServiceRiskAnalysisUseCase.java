package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EServiceRiskAnalysisUseCase {

    private final RiskAnalysisGateway riskAnalysisGateway;
    private final EServiceRiskAnalysisGateway eServiceRiskAnalysisGateway;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;

    public EServiceRiskAnalysis addLatestRiskAnalysis(Tenant tenant, EService eService, boolean completed) {
        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(tenant);
        Map<String, List<String>> answers =
                riskAnalysisDataFactory.getTemplateForTenant(tenant, completed);

        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(answers)
                .build();

        return eServiceRiskAnalysisGateway.addRiskAnalysis(eService.getRef(), form);
    }

    public EServiceRiskAnalysis getRiskAnalysis(EService eService) {
        return eServiceRiskAnalysisGateway.getRiskAnalysis(eService.getRef());
    }
}

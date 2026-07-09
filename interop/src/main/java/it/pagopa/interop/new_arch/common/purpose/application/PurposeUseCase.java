package it.pagopa.interop.new_arch.common.purpose.application;

import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.kernel.domain.Tenant;
import it.pagopa.interop.new_arch.common.purpose.domain.Purpose;
import it.pagopa.interop.new_arch.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.new_arch.common.risk_analysis.application.RiskAnalysisDataInitializer;
import it.pagopa.interop.new_arch.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.new_arch.common.risk_analysis.domain.RiskAnalysisFormConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PurposeUseCase {
    private final PurposeGateway purposeGateway;
    private final RiskAnalysisDataInitializer riskAnalysisDataInitializer;

    public RiskAnalysisFormConfig getLatestRiskAnalysisConfig(Tenant tenant) {
        return purposeGateway.getLatestRiskAnalysisConfig(tenant);
    }

    public Purpose addDraftPurpose(Tenant consumer, EService eService, RiskAnalysisForm riskAnalysisForm) {
        return purposeGateway.createPurpose(eService.getRef(), consumer, riskAnalysisForm);
    }

    public Purpose addDraftPurpose(Tenant consumer, EService eService) {
        RiskAnalysisFormConfig latestConfig = getLatestRiskAnalysisConfig(consumer);
        Map<String, List<String>> answers =
                riskAnalysisDataInitializer.getTemplateForTenant(consumer, true);

        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(answers)
                .build();

        return addDraftPurpose(consumer, eService, form);
    }

    public Purpose activatePurpose(Purpose purpose, PurposeVersion purposeVersion) {
        return purposeGateway.activatePurpose(purpose.getRef(), purposeVersion.getRef());
    }
}

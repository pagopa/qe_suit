package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
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
public class PurposeUseCase {
    private final CurrentUserSession currentUserSession;
    private final PurposeGateway purposeGateway;
    private final RiskAnalysisGateway riskAnalysisGateway;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;

    public RiskAnalysisFormConfig getLatestRiskAnalysisConfig(Tenant tenant) {
        return riskAnalysisGateway.getLatestRiskAnalysisConfig(tenant);
    }

    public Purpose addDraftPurpose(Tenant consumer, EService eService, RiskAnalysisForm riskAnalysisForm) {
        return purposeGateway.createPurpose(eService.getRef(), consumer, riskAnalysisForm);
    }

    public Purpose addDraftPurpose(Tenant consumer, EService eService) {
        RiskAnalysisFormConfig latestConfig = getLatestRiskAnalysisConfig(consumer);
        Map<String, List<String>> answers =
                riskAnalysisDataFactory.getTemplateForTenant(consumer, true);

        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(answers)
                .build();

        return addDraftPurpose(consumer, eService, form);
    }

    public Purpose addDraftPurpose(EService eService){
        return addDraftPurpose(currentUserSession.getTenant(), eService);
    }

    public Purpose activatePurpose(Purpose purpose, PurposeVersion purposeVersion) {
        return purposeGateway.activatePurpose(purpose.getRef(), purposeVersion.getRef());
    }

    public Purpose suspendPurpose(Purpose purpose, PurposeVersion purposeVersion) {
        return purposeGateway.suspendPurpose(purpose.getRef(), purposeVersion.getRef());
    }
}

package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import org.springframework.plugin.core.Plugin;

public interface PurposeGateway extends Plugin<Channel> {
    Purpose createPurpose(EServiceRef eServiceRef, Tenant consumer, RiskAnalysisForm riskAnalysisForm);

    Purpose activatePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef);
}

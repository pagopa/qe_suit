package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import jakarta.annotation.Nullable;
import org.springframework.plugin.core.Plugin;

public interface PurposeGateway extends Plugin<Channel> {
    Purpose getPurpose(PurposeRef purposeRef);

    Purpose createPurpose(PurposeCreateCommand createCommand);

    Purpose createReversePurpose(it.pagopa.interop.common.eservice.domain.EService eService, java.util.UUID riskAnalysisId);

    Purpose createPurposeFromTemplate(java.util.UUID purposeTemplateId, it.pagopa.interop.common.eservice.domain.EService eService);

    Purpose activatePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);

    Purpose suspendPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);
}

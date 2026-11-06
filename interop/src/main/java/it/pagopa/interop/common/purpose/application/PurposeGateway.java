package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysisRef;
import it.pagopa.interop.common.kernel.domain.PurposeRef;
import it.pagopa.interop.common.kernel.domain.PurposeTemplateRef;
import it.pagopa.interop.common.kernel.domain.PurposeVersionRef;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import jakarta.annotation.Nullable;
import org.springframework.plugin.core.Plugin;

import java.util.List;
import java.util.UUID;

public interface PurposeGateway extends Plugin<Channel> {
    Purpose getPurpose(PurposeRef purposeRef);

    Purpose createPurpose(PurposeCreateCommand createCommand);

    Purpose createReversePurpose(EServiceRef eServiceRef, EServiceRiskAnalysisRef riskAnalysisRef);

    Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, EServiceRef eServiceRef);

    Purpose activatePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);

    Purpose suspendPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);

    void assignRiskAnalysisReviewers(
            PurposeRef purposeRef,
            RiskAnalysisReviewMode reviewMode,
            List<UUID> reviewerIds
    );

    void submitRiskAnalysis(PurposeRef purposeRef);
}

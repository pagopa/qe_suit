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
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import jakarta.annotation.Nullable;
import org.springframework.plugin.core.Plugin;

import java.util.List;
import java.util.UUID;

public interface PurposeGateway extends Plugin<Channel> {
    Purpose getPurpose(PurposeRef purposeRef);

    Purpose updatePurposeDailyCalls(PurposeRef purposeRef, int dailyCalls);

    Purpose createPurpose(PurposeCreateCommand createCommand);

    default void deletePurpose(PurposeRef purposeRef) {
        throw new UnsupportedOperationException("Purpose deletion is not supported by this gateway implementation");
    }

    Purpose createReversePurpose(EServiceRef eServiceRef, EServiceRiskAnalysisRef riskAnalysisRef);

    Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, EServiceRef eServiceRef);

    Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, PurposeCreateCommand createCommand);

    Purpose activatePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);

    Purpose rejectPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, String rejectionReason);

    Purpose askForApprovalPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);

    Purpose suspendPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);

    default Purpose archivePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef) {
        throw new UnsupportedOperationException("Purpose archiving is not supported by this gateway implementation");
    }

    void assignRiskAnalysisReviewers(PurposeRef purposeRef, RiskAnalysisReviewMode reviewMode, List<UUID> reviewerIds);

    void submitRiskAnalysis(PurposeRef purposeRef);

    default void submitRiskAnalysis(PurposeRef purposeRef, RiskAnalysisForm form) {
        throw new UnsupportedOperationException("Submission with an explicit risk-analysis form is not supported by this gateway");
    }

    RiskAnalysisForm getRiskAnalysisForm(PurposeRef purposeRef);

    void saveRiskAnalysisForm(PurposeRef purposeRef, RiskAnalysisForm form);

    void signRiskAnalysis(PurposeRef purposeRef, int metadataVersionToSign);

    void signRiskAnalysis(PurposeRef purposeRef);

    void rejectRiskAnalysis(PurposeRef purposeRef, String rejectionReason);
}

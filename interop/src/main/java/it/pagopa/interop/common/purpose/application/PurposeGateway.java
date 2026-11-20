package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.DelegationRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysisRef;
import it.pagopa.interop.common.kernel.domain.PurposeRef;
import it.pagopa.interop.common.kernel.domain.PurposeTemplateRef;
import it.pagopa.interop.common.kernel.domain.PurposeVersionRef;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeCreationFailureReason;
import jakarta.annotation.Nullable;
import org.springframework.plugin.core.Plugin;

public interface PurposeGateway extends Plugin<Channel> {
    Purpose getPurpose(PurposeRef purposeRef);

    Purpose updatePurposeDailyCalls(PurposeRef purposeRef, int dailyCalls);

    Purpose createPurpose(PurposeCreateCommand createCommand);

    void shouldFailToCreatePurpose(PurposeCreateCommand createCommand, PurposeCreationFailureReason reason);

    Purpose createReversePurpose(EServiceRef eServiceRef, EServiceRiskAnalysisRef riskAnalysisRef);

    Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, EServiceRef eServiceRef);

    Purpose createPurposeFromTemplate(PurposeTemplateRef purposeTemplateRef, PurposeCreateCommand createCommand);

    Purpose clonePurpose(PurposeRef purposeRef, PurposeCloneCommand cloneCommand);

    Purpose assignRiskAnalysisReviewer(PurposeRef purposeRef, AssignRiskAnalysisReviewerCommand assignCommand);

    Purpose submitRiskAnalysis(PurposeRef purposeRef, SubmitRiskAnalysisCommand submitCommand);

    Purpose signRiskAnalysis(PurposeRef purposeRef, SignRiskAnalysisCommand signCommand);

    Purpose rejectRiskAnalysis(PurposeRef purposeRef, RejectRiskAnalysisCommand rejectCommand);

    Purpose activatePurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);

    Purpose rejectPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, String rejectionReason);

    Purpose askForApprovalPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);

    Purpose suspendPurpose(PurposeRef purposeRef, PurposeVersionRef purposeVersionRef, @Nullable DelegationRef delegationRef);

    void attemptNameUpdate(PurposeRef purposeRef, String name);

    void attemptPurposeCloning(PurposeRef purposeRef, PurposeCloneCommand cloneCommand);

    void attemptRiskAnalysisSigning(PurposeRef purposeRef, SignRiskAnalysisCommand signCommand);

    void attemptRiskAnalysisRejection(PurposeRef purposeRef, RejectRiskAnalysisCommand rejectCommand);

    void attemptCreatePurpose(PurposeCreateCommand createCommand);

    void attemptCreateReversePurpose(
            EServiceRef eServiceRef,
            EServiceRiskAnalysisRef riskAnalysisRef,
            String title
    );

    void attemptCreatePurposeFromTemplate(
            PurposeTemplateRef purposeTemplateRef,
            PurposeCreateCommand createCommand
    );

    void attemptNameUpdateFromTemplate(
            PurposeTemplateRef purposeTemplateRef,
            PurposeRef purposeRef,
            String name
    );

    void attemptRiskAnalysisFormUpdate(PurposeRef purposeRef);
}

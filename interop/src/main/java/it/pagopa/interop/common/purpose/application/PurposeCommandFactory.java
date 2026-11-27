package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.purpose.domain.RiskAnalysisReviewMode;
import org.springframework.plugin.core.Plugin;

import java.util.List;
import java.util.UUID;

public interface PurposeCommandFactory extends Plugin<Channel> {
    PurposeCreateCommand emptyCreateCommand();
    PurposeCreateCommand validFullPopulatedCreateCommand(EServiceRef eServiceRef);

    PurposeCloneCommand cloneCommand(EServiceRef eServiceRef);
    AssignRiskAnalysisReviewerCommand assignRiskAnalysisReviewerCommand(
            RiskAnalysisReviewMode reviewMode,
            List<UUID> reviewerIds
    );
    SubmitRiskAnalysisCommand submitRiskAnalysisCommand();
    SignRiskAnalysisCommand signRiskAnalysisCommand(int metadataVersionToSign);
    RejectRiskAnalysisCommand rejectRiskAnalysisCommand(String rejectionReason);
}

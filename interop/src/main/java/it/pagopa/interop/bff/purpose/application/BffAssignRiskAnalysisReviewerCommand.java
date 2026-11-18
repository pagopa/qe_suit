package it.pagopa.interop.bff.purpose.application;

import it.pagopa.interop.common.purpose.application.AssignRiskAnalysisReviewerCommand;
import it.pagopa.interop.common.purpose.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisAssignmentSeed;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class BffAssignRiskAnalysisReviewerCommand implements AssignRiskAnalysisReviewerCommand {

    @Getter
    private final RiskAnalysisAssignmentSeed bffAssignmentPayload;

    public BffAssignRiskAnalysisReviewerCommand() {
        this.bffAssignmentPayload = new RiskAnalysisAssignmentSeed();
    }

    public BffAssignRiskAnalysisReviewerCommand(RiskAnalysisAssignmentSeed bffAssignmentPayload) {
        this.bffAssignmentPayload = bffAssignmentPayload;
    }

    @Override
    public AssignRiskAnalysisReviewerCommand reviewMode(RiskAnalysisReviewMode reviewMode) {
        bffAssignmentPayload.setReviewMode(
                reviewMode != null
                        ? it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode.fromValue(reviewMode.name())
                        : null
        );
        return this;
    }

    @Override
    public AssignRiskAnalysisReviewerCommand reviewerIds(List<UUID> reviewerIds) {
        bffAssignmentPayload.setReviewerIds(reviewerIds);
        return this;
    }

    @Override
    public RiskAnalysisReviewMode getReviewMode() {
        var mode = bffAssignmentPayload.getReviewMode();
        return mode != null ? RiskAnalysisReviewMode.valueOf(mode.getValue()) : null;
    }

    @Override
    public List<UUID> getReviewerIds() {
        return bffAssignmentPayload.getReviewerIds();
    }
}

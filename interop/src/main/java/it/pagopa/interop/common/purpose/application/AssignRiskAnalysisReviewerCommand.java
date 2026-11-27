package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.purpose.domain.RiskAnalysisReviewMode;

import java.util.List;
import java.util.UUID;

public interface AssignRiskAnalysisReviewerCommand {
    AssignRiskAnalysisReviewerCommand reviewMode(RiskAnalysisReviewMode reviewMode);

    AssignRiskAnalysisReviewerCommand reviewerIds(List<UUID> reviewerIds);

    RiskAnalysisReviewMode getReviewMode();

    List<UUID> getReviewerIds();
}

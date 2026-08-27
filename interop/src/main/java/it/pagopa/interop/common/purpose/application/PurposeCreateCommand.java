package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;

import java.util.UUID;

public interface PurposeCreateCommand {
    PurposeCreateCommand title(String title);
    PurposeCreateCommand description(String description);
    PurposeCreateCommand isFreeOfCharge(Boolean isFreeOfCharge);
    PurposeCreateCommand freeOfChargeReason(String freeOfChargeReason);
    PurposeCreateCommand dailyCalls(Integer dailyCalls);
    PurposeCreateCommand eserviceId(UUID eserviceId);
    PurposeCreateCommand consumerId(UUID consumerId);
    PurposeCreateCommand riskAnalysisForm(RiskAnalysisForm riskAnalysisForm);
}

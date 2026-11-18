package it.pagopa.interop.bff.purpose.application;

import it.pagopa.interop.common.purpose.application.RejectRiskAnalysisCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisRejectionSeed;
import lombok.Getter;

public class BffRejectRiskAnalysisCommand implements RejectRiskAnalysisCommand {

    @Getter
    private final RiskAnalysisRejectionSeed bffRejectionPayload;

    public BffRejectRiskAnalysisCommand() {
        this.bffRejectionPayload = new RiskAnalysisRejectionSeed();
    }

    public BffRejectRiskAnalysisCommand(RiskAnalysisRejectionSeed bffRejectionPayload) {
        this.bffRejectionPayload = bffRejectionPayload;
    }

    @Override
    public RejectRiskAnalysisCommand rejectionReason(String rejectionReason) {
        bffRejectionPayload.setRejectionReason(rejectionReason);
        return this;
    }

    @Override
    public String getRejectionReason() {
        return bffRejectionPayload.getRejectionReason();
    }
}

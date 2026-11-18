package it.pagopa.interop.bff.purpose.application;

import it.pagopa.interop.common.purpose.application.SignRiskAnalysisCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSignSeed;
import lombok.Getter;

public class BffSignRiskAnalysisCommand implements SignRiskAnalysisCommand {

    @Getter
    private final RiskAnalysisSignSeed bffSignPayload;

    public BffSignRiskAnalysisCommand() {
        this.bffSignPayload = new RiskAnalysisSignSeed();
    }

    public BffSignRiskAnalysisCommand(RiskAnalysisSignSeed bffSignPayload) {
        this.bffSignPayload = bffSignPayload;
    }

    @Override
    public SignRiskAnalysisCommand metadataVersionToSign(Integer metadataVersionToSign) {
        bffSignPayload.setMetadataVersionToSign(metadataVersionToSign);
        return this;
    }

    @Override
    public Integer getMetadataVersionToSign() {
        return bffSignPayload.getMetadataVersionToSign();
    }
}

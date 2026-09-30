package it.pagopa.interop.bff.purpose.application;

import it.pagopa.interop.common.purpose.application.PurposeCreateCommand;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import lombok.Getter;

import java.util.UUID;

public class BffPurposeCreateCommand implements PurposeCreateCommand {

    @Getter
    private final PurposeSeed bffCreationPayload;

    public BffPurposeCreateCommand() {
        bffCreationPayload = new PurposeSeed();
    }

    public BffPurposeCreateCommand(PurposeSeed purposeSeed) {
        bffCreationPayload = purposeSeed;
    }

    @Override
    public PurposeCreateCommand title(String title) {
        return null;
    }

    @Override
    public PurposeCreateCommand description(String description) {
        return null;
    }

    @Override
    public PurposeCreateCommand isFreeOfCharge(Boolean isFreeOfCharge) {
        return null;
    }

    @Override
    public PurposeCreateCommand freeOfChargeReason(String freeOfChargeReason) {
        return null;
    }

    @Override
    public PurposeCreateCommand dailyCalls(Integer dailyCalls) {
        return null;
    }

    @Override
    public PurposeCreateCommand eserviceId(UUID eserviceId) {
        return null;
    }

    @Override
    public PurposeCreateCommand consumerId(UUID consumerId) {
        return null;
    }

    @Override
    public PurposeCreateCommand riskAnalysisForm(RiskAnalysisForm riskAnalysisForm) {
        return null;
    }
}

package it.pagopa.interop.new_arch.bff.agreement.infrastructure.request;

import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementSubmissionPayload;
import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BffSubmitAgreementRequest implements SubmitAgreementRequest {
    private UUID agreementId;
    private Agreement agreement;
    private AgreementSubmissionPayload payload = new AgreementSubmissionPayload().consumerNotes("consumerNotes");

    @Override
    public BffSubmitAgreementRequest agreement(Agreement agreement) {
        this.agreement = agreement;
        agreementId = agreement.getRef().id();
        return this;
    }

    public BffSubmitAgreementRequest payload(AgreementSubmissionPayload payload) {
        this.payload = payload;
        return this;
    }

}

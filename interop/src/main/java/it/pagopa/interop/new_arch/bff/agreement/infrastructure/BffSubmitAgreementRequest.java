package it.pagopa.interop.new_arch.bff.agreement.infrastructure;

import it.pagopa.interop.new_arch.common.agreement.application.request.SubmitAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BffSubmitAgreementRequest implements SubmitAgreementRequest {
    private UUID agreementId;
    private UUID delegationId;

    @Override
    public BffSubmitAgreementRequest agreement(Agreement agreement) {
        agreementId = agreement.getRef().id();
        return this;
    }

    @Override
    public BffSubmitAgreementRequest delegation(DelegationRef delegationRef) {
        delegationId = delegationRef.getId();
        return this;
    }
}

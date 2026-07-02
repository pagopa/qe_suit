package it.pagopa.interop.new_arch.bff.agreement.infrastructure.request;

import it.pagopa.interop.new_arch.common.agreement.application.request.ActivateAgreementRequest;
import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import lombok.Getter;

@Getter
public class BffActivateAgreementRequest implements ActivateAgreementRequest {

    private Agreement agreement;
    private DelegationRef delegation;

    @Override
    public ActivateAgreementRequest agreement(Agreement agreement) {
        this.agreement = agreement;
        return this;
    }

    @Override
    public ActivateAgreementRequest delegation(DelegationRef delegationRef) {
        this.delegation = delegationRef;
        return this;
    }
}

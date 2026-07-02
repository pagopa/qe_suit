package it.pagopa.interop.new_arch.common.agreement.application.request;

import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;

public interface ActivateAgreementRequest {
    ActivateAgreementRequest agreement(Agreement agreement);

    ActivateAgreementRequest delegation(DelegationRef delegationRef);

    Agreement getAgreement();

    DelegationRef getDelegation();
}

package it.pagopa.interop.new_arch.common.agreement.application.request;

import it.pagopa.interop.new_arch.common.agreement.domain.Agreement;

public interface SubmitAgreementRequest {
    SubmitAgreementRequest agreement(Agreement agreement);

    Agreement getAgreement();
}

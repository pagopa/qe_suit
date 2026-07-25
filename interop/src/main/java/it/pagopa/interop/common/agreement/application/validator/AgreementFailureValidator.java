package it.pagopa.interop.new_arch.common.agreement.application.validator;

import it.pagopa.interop.new_arch.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.new_arch.common.kernel.domain.Delegation;
import jakarta.annotation.Nullable;

public interface AgreementFailureValidator {
    boolean supports(AgreementCreationFailureReason reason);

    void validate(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation);

    default void validate(EService eService, EServiceDescriptor descriptor) {
        validate(eService, descriptor, null);
    }
}

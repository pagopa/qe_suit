package it.pagopa.interop.new_arch.common.agreement.application.validator;

import it.pagopa.interop.new_arch.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.new_arch.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.new_arch.common.kernel.domain.DelegationRef;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EServiceInvalidStateValidator implements AgreementFailureValidator {

    @Override
    public boolean supports(AgreementCreationFailureReason reason) {
        return reason == AgreementCreationFailureReason.ESERVICE_INVALID_STATE;
    }

    @Override
    public void validate(EService eService, EServiceDescriptor descriptor, @Nullable DelegationRef delegation) {
        List<EServiceDescriptorState> validStates = List.of(EServiceDescriptorState.PUBLISHED);

        if (validStates.contains(descriptor.getState())) {
            throw new IllegalStateException(
                    "Descriptor state is valid [%s] for agreement creation, but the test expected a failure due to invalid state."
                            .formatted(descriptor.getState())
            );
        }
    }
}

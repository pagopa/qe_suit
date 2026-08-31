package it.pagopa.interop.common.agreement.application.validator;

import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.Delegation;
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
    public void validate(EService eService, EServiceDescriptor descriptor, @Nullable Delegation delegation) {
        List<EServiceDescriptorState> validStates = List.of(EServiceDescriptorState.PUBLISHED);

        if (validStates.contains(descriptor.getState())) {
            throw new IllegalStateException(
                    "Descriptor state is valid [%s] for agreement creation, but the test expected a failure due to invalid state."
                            .formatted(descriptor.getState())
            );
        }
    }
}

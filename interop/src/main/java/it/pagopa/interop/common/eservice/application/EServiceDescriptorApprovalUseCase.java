package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EServiceDescriptorApprovalUseCase {
    private final EServiceDescriptorApprovalGateway gateway;

    public EServiceDescriptor rejectDescriptor(EService service, EServiceDescriptor descriptor, String reason) {
        if (descriptor.getState() != EServiceDescriptorState.WAITING_FOR_APPROVAL) {
            throw new IllegalStateException("Only a descriptor awaiting approval can be rejected");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("A rejection reason is required");
        }
        return gateway.rejectDescriptor(service.getRef(), descriptor.getRef(), reason);
    }
}

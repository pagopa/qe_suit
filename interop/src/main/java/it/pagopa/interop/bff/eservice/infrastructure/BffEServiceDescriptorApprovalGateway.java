package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorApprovalGateway;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegatedEServiceDescriptorSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BffEServiceDescriptorApprovalGateway implements EServiceDescriptorApprovalGateway {
    private final BffEServiceDescriptorApprovalRestClient restClient;
    private final BffEServiceDescriptorGateway descriptors;

    @Override
    public EServiceDescriptor rejectDescriptor(
            EServiceRef serviceRef, EServiceDescriptorRef descriptorRef, String reason
    ) {
        var response = restClient.rejectDescriptor(serviceRef.id(), descriptorRef.id(),
                new RejectDelegatedEServiceDescriptorSeed().rejectionReason(reason)).withoutPolling();
        if (!response.getRaw().isSuccess()) {
            String status = response.getRaw() instanceof ApiResponse apiResponse
                    ? Integer.toString(apiResponse.getStatusCode()) : "sconosciuto";
            throw new IllegalStateException("Rifiuto della versione " + descriptorRef.id()
                    + " dell'e-service " + serviceRef.id() + " non riuscito (HTTP " + status + ")");
        }
        return descriptors.awaitDescriptorState(serviceRef, descriptorRef, EServiceDescriptorState.DRAFT);
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}

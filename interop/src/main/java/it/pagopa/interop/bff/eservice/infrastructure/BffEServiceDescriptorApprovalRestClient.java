package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegatedEServiceDescriptorSeed;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffEServiceDescriptorApprovalRestClient extends RestClient {
    private final EservicesApi api;

    public BffEServiceDescriptorApprovalRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        api = apiClient.eservices();
    }

    public TestChain<Void> rejectDescriptor(
            @Nonnull UUID serviceId, @Nonnull UUID descriptorId,
            @Nonnull RejectDelegatedEServiceDescriptorSeed payload
    ) {
        return execute(
                () -> api.rejectDelegatedEServiceDescriptor()
                        .eServiceIdPath(serviceId)
                        .descriptorIdPath(descriptorId)
                        .body(payload)
                        .execute(Function.identity()),
                Void.class
        );
    }
}

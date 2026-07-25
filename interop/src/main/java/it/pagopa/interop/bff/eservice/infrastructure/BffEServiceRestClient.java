package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.infrastructure.template.RestClient;
import it.pagopa.interop.common.infrastructure.template.action.TestChain;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffEServiceRestClient extends RestClient {

    private final EservicesApi eservicesApi;

    public BffEServiceRestClient(ApiClient apiClient) {
        this.eservicesApi = apiClient.eservices();
    }

    public TestChain<CreatedEServiceDescriptor> createEService(@Nonnull EServiceSeed payload) {
        return execute(
                () -> eservicesApi.createEService().body(payload).execute(Function.identity()),
                CreatedEServiceDescriptor.class
        );
    }

    public TestChain<CreatedResource> addDescriptor(@Nonnull UUID eserviceId) {
        return execute(
                () -> eservicesApi.createDescriptor().eServiceIdPath(eserviceId).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<CreatedResource> updateDescriptor(@Nonnull UUID eserviceId, @Nonnull UUID descriptorId, @Nonnull UpdateEServiceDescriptorQuotas payload) {
        return execute(
                () -> eservicesApi.updateDescriptor().eServiceIdPath(eserviceId).descriptorIdPath(descriptorId).body(payload).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<ProducerEServiceDetails> readEService(@Nonnull UUID eserviceId) {
        return execute(
                () -> eservicesApi.getProducerEServiceDetails().eserviceIdPath(eserviceId).execute(Function.identity()),
                ProducerEServiceDetails.class
        );
    }

    public TestChain<ProducerEServiceDescriptor> readDescriptor(@Nonnull UUID eserviceId, @Nonnull UUID descriptorId) {
        return execute(
                () -> eservicesApi.getProducerEServiceDescriptor().eserviceIdPath(eserviceId).descriptorIdPath(descriptorId).execute(Function.identity()),
                ProducerEServiceDescriptor.class
        );
    }

    public TestChain<Void> publishDescriptor(@Nonnull UUID eserviceId, @Nonnull UUID descriptorId) {
        return execute(
                () -> eservicesApi.publishDescriptor().eServiceIdPath(eserviceId).descriptorIdPath(descriptorId).execute(Function.identity()),
                Void.class
        );
    }
}

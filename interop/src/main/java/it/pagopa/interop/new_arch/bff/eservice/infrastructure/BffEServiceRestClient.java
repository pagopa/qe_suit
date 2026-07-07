package it.pagopa.interop.new_arch.bff.eservice.infrastructure;

import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import it.pagopa.interop.new_arch.common.eservice.domain.EService;
import it.pagopa.interop.new_arch.common.infrastructure.template.RestClient;
import it.pagopa.interop.new_arch.common.infrastructure.template.action.TestChain;
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

    public TestChain<CreatedEServiceDescriptor, EService> createEService(@Nonnull EServiceSeed payload) {
        return execute(
                () -> eservicesApi.createEService().body(payload).execute(Function.identity()),
                CreatedEServiceDescriptor.class,
                EService.class
        );
    }

    public TestChain<CreatedResource, EService> createDescriptor(@Nonnull UUID eserviceId) {
        return execute(
                () -> eservicesApi.createDescriptor().eServiceIdPath(eserviceId).execute(Function.identity()),
                CreatedResource.class,
                EService.class
        );
    }

    public TestChain<CreatedResource, EService> updateDescriptor(@Nonnull UUID eserviceId, @Nonnull UUID descriptorId) {
        return execute(
                () -> eservicesApi.updateDescriptor().eServiceIdPath(eserviceId).descriptorIdPath(descriptorId).execute(Function.identity()),
                CreatedResource.class,
                EService.class
        );
    }

    public TestChain<ProducerEServiceDetails, EService> readEService(@Nonnull UUID eserviceId) {
        return execute(
                () -> eservicesApi.getProducerEServiceDetails().eserviceIdPath(eserviceId).execute(Function.identity()),
                ProducerEServiceDetails.class,
                EService.class
        );
    }

    public TestChain<ProducerEServiceDescriptor, EService> readDescriptor(@Nonnull UUID eserviceId, @Nonnull UUID descriptorId) {
        return execute(
                () -> eservicesApi.getProducerEServiceDescriptor().eserviceIdPath(eserviceId).descriptorIdPath(descriptorId).execute(Function.identity()),
                ProducerEServiceDescriptor.class,
                EService.class
        );
    }
}

package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;
import java.util.function.Function;

@Component
public class BffEServiceRestClient extends RestClient {

    private final EservicesApi eservicesApi;

    public BffEServiceRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
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
                () -> eservicesApi
                        .createDescriptor()
                        .eServiceIdPath(eserviceId)
                        .reqSpec(reqSpec -> reqSpec.setContentType("application/json"))
                        .execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<CreatedResource> updateDescriptor(@Nonnull UUID eserviceId, @Nonnull UUID descriptorId, @Nonnull UpdateEServiceDescriptorSeed payload) {
        return execute(
                () -> eservicesApi.updateDraftDescriptor().eServiceIdPath(eserviceId).descriptorIdPath(descriptorId).body(payload).execute(Function.identity()),
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
                () -> eservicesApi.publishDescriptor()
                        .eServiceIdPath(eserviceId)
                        .descriptorIdPath(descriptorId)
                        .reqSpec(reqSpec -> reqSpec.setContentType("application/json"))
                        .execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<CreatedResource> addDocument(@Nonnull UUID eserviceId, @Nonnull UUID descriptorId, @Nonnull String documentKind, @Nonnull String documentName, @Nonnull File document) {
        return execute(
                () -> eservicesApi.createEServiceDocument()
                        .eServiceIdPath(eserviceId)
                        .descriptorIdPath(descriptorId)
                        .kindForm(documentKind)
                        .prettyNameForm(documentName)
                        .reqSpec(reqSpec ->
                                reqSpec.addMultiPart(
                                        "doc",
                                        document,
                                        "application/octet-stream"
                                )
                        )
                        .execute(Function.identity()),
                CreatedResource.class
        );
    }
}

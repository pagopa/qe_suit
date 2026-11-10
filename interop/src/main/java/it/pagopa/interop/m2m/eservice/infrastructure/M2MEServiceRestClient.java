package it.pagopa.interop.m2m.eservice.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.Document;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.EServiceDescriptor;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;
import java.util.function.Function;

@Component
public class M2MEServiceRestClient extends RestClient {

    private final EservicesApi eservicesApi;

    public M2MEServiceRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.eservicesApi = apiClient.eservices();
    }

    public TestChain<Document> uploadEServiceDescriptorInterface(
            @Nonnull UUID eserviceId,
            @Nonnull UUID descriptorId,
            @Nonnull String prettyName,
            @Nonnull File file
    ) {
        return execute(
                () -> eservicesApi
                        .uploadEServiceDescriptorInterface()
                        .eserviceIdPath(eserviceId)
                        .descriptorIdPath(descriptorId)
                        .prettyNameForm(prettyName)
                        ._fileMultiPart(file)
                        .execute(Function.identity()),
                Document.class
        );
    }

    public TestChain<EServiceDescriptor> getEServiceDescriptor(
            @Nonnull UUID eserviceId,
            @Nonnull UUID descriptorId
    ) {
        return execute(
                () -> eservicesApi
                        .getEServiceDescriptor()
                        .eserviceIdPath(eserviceId)
                        .descriptorIdPath(descriptorId)
                        .execute(Function.identity()),
                EServiceDescriptor.class
        );
    }
}


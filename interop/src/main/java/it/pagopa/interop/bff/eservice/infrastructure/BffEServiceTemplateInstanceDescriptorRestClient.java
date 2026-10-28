package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.UpdateEServiceDescriptorTemplateInstanceSeed;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffEServiceTemplateInstanceDescriptorRestClient extends RestClient {
    private final EservicesApi api;

    public BffEServiceTemplateInstanceDescriptorRestClient(TestChainFactory chainFactory, ApiClient client) {
        super(chainFactory);
        api = client.eservices();
    }

    public TestChain<CreatedResource> updateDraftDescriptor(
            UUID serviceId, UUID descriptorId, UpdateEServiceDescriptorTemplateInstanceSeed payload
    ) {
        return execute(() -> api.updateDraftDescriptorTemplateInstance()
                .eServiceIdPath(serviceId).descriptorIdPath(descriptorId).body(payload)
                .execute(Function.identity()), CreatedResource.class);
    }
}

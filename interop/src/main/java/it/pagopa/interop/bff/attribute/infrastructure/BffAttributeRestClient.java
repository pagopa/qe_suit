package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AttributesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffAttributeRestClient extends RestClient {

    private final AttributesApi attributesApi;

    public BffAttributeRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.attributesApi = apiClient.attributes();
    }

    public TestChain<CreatedResource> createCertifiedAttribute(@Nonnull AttributeSeed payload) {
        return execute(
                () -> attributesApi.createCertifiedAttribute().body(payload).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<Attribute> getAttribute(@Nonnull UUID attributeId) {
        return execute(
                () -> attributesApi.getAttributeById().attributeIdPath(attributeId).execute(Function.identity()),
                Attribute.class
        );
    }
}

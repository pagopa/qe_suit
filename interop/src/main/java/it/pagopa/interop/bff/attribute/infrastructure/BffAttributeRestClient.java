package it.pagopa.interop.bff.attribute.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.AttributesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.Attribute;
import it.pagopa.interop.generated.openapi.clients.bff.model.AttributeSeed;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BffAttributeRestClient extends RestClient {

    private final AttributesApi attributesApi;

    public BffAttributeRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.attributesApi = apiClient.attributes();
    }

    public TestChain<Attribute> createDeclaredAttribute(@Nonnull AttributeSeed payload) {
        return execute(
                () -> attributesApi.createDeclaredAttribute().body(payload).execute(Function.identity()),
                Attribute.class
        );
    }
}


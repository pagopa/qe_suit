package it.pagopa.interop.m2m.client.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.Client;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class M2MClientRestClient extends RestClient {

    private final ClientsApi clientsApi;

    public M2MClientRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.clientsApi = apiClient.clients();
    }

    public TestChain<Client> getClient(UUID clientId) {
        return execute(
                () -> clientsApi.getClient()
                        .clientIdPath(clientId)
                        .execute(Function.identity()),
                Client.class
        );
    }
}

package it.pagopa.interop.m2m.client.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.ApiClient;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.Client;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.ClientUpdateSeed;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
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

    public TestChain<Client> updateClient(UUID clientId, ClientUpdateSeed clientUpdateSeed) {
        // TODO serve il rilascio di PIN-11333 per fixare errore con rotta PATCH /clients/{clientId} su M2M
        return execute(
                () -> {
                    Map<String, String> payload = new LinkedHashMap<>();
                    if (clientUpdateSeed.getName() != null) {
                        payload.put(ClientUpdateSeed.JSON_PROPERTY_NAME, clientUpdateSeed.getName());
                    }
                    if (clientUpdateSeed.getDescription() != null) {
                        payload.put(ClientUpdateSeed.JSON_PROPERTY_DESCRIPTION, clientUpdateSeed.getDescription());
                    }
                    return clientsApi.updateClient()
                            .clientIdPath(clientId)
                            .reqSpec(reqSpec -> reqSpec
                                    .setContentType("application/merge-patch+json; charset=UTF-8")
                                    .setBody(payload))
                            .execute(Function.identity());
                },
                Client.class
        );
    }
}

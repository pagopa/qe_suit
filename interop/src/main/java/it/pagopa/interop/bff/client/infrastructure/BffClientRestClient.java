package it.pagopa.interop.bff.client.infrastructure;

import it.pagopa.interop.common.infrastructure.template.RestClient;
import it.pagopa.interop.common.infrastructure.template.action.TestChain;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.ClientsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Function;

@Component
public class BffClientRestClient extends RestClient {

    private final ClientsApi clientsApi;

    public BffClientRestClient(ApiClient apiClient) {
        this.clientsApi = apiClient.clients();
    }

    public TestChain<Client> getClient(@Nonnull UUID clientId) {
        return execute(
                () -> clientsApi.getClient().clientIdPath(clientId).execute(Function.identity()),
                Client.class
        );
    }

    public TestChain<PublicKeys> getClientKeys(@Nonnull UUID clientId) {
        return execute(
                () -> clientsApi.getClientKeys().clientIdPath(clientId).execute(Function.identity()),
                PublicKeys.class
        );
    }

    public TestChain<CompactUser> getClientUsers(@Nonnull UUID clientId) {
        return execute(
                () -> clientsApi.getClientUsers().clientIdPath(clientId).execute(Function.identity()),
                CompactUser.class
        );
    }

    public TestChain<CreatedResource> createApiClient(@Nonnull ClientSeed clientSeed) {
        return execute(
                () -> clientsApi.createApiClient().body(clientSeed).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<CreatedResource> createConsumerClient(@Nonnull ClientSeed clientSeed) {
        return execute(
                () -> clientsApi.createConsumerClient().body(clientSeed).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<Void> addKey(@Nonnull UUID clientId, @Nonnull KeySeed keySeed) {
        return execute(
                () -> clientsApi.createKey().clientIdPath(clientId).body(keySeed).execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<CreatedResource> addUsers(@Nonnull UUID clientId, @Nonnull AddUsersToClientRequest addUsersToClientRequest) {
        return execute(
                () -> clientsApi.addUsersToClient().clientIdPath(clientId).body(addUsersToClientRequest).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<Void> linkPurpose(@Nonnull UUID clientId, @Nonnull PurposeAdditionDetailsSeed purposeAdditionDetailsSeed) {
        return execute(
                () -> clientsApi.addClientPurpose().clientIdPath(clientId).body(purposeAdditionDetailsSeed).execute(Function.identity()),
                Void.class
        );
    }
}

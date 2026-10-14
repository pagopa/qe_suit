package it.pagopa.interop.bff.client.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
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

    public BffClientRestClient(TestChainFactory chainFactory, ApiClient apiClient) {
        super(chainFactory);
        this.clientsApi = apiClient.clients();
    }

    public TestChain<Client> getClient(UUID clientId) {
        return execute(
                () -> clientsApi.getClient().clientIdPath(clientId).execute(Function.identity()),
                Client.class
        );
    }

    public TestChain<PublicKeys> getClientKeys(UUID clientId) {
        return execute(
                () -> clientsApi.getClientKeys().clientIdPath(clientId).execute(Function.identity()),
                PublicKeys.class
        );
    }

    public TestChain<CompactUser> getClientUsers(UUID clientId) {
        return execute(
                () -> clientsApi.getClientUsers().clientIdPath(clientId).execute(Function.identity()),
                CompactUser.class
        );
    }

    public TestChain<CreatedResource> createApiClient(ClientSeed clientSeed) {
        return execute(
                () -> clientsApi.createApiClient().body(clientSeed).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<CreatedResource> createConsumerClient(ClientSeed clientSeed) {
        return execute(
                () -> clientsApi.createConsumerClient().body(clientSeed).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<Void> addKey(UUID clientId, KeySeed keySeed) {
        return execute(
                () -> clientsApi.createKey().clientIdPath(clientId).body(keySeed).execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<CreatedResource> addUsers(UUID clientId, AddUsersToClientRequest addUsersToClientRequest) {
        return execute(
                () -> clientsApi.addUsersToClient().clientIdPath(clientId).body(addUsersToClientRequest).execute(Function.identity()),
                CreatedResource.class
        );
    }

    public TestChain<Void> linkPurpose(UUID clientId, PurposeAdditionDetailsSeed purposeAdditionDetailsSeed) {
        return execute(
                () -> clientsApi.addClientPurpose().clientIdPath(clientId).body(purposeAdditionDetailsSeed).execute(Function.identity()),
                Void.class
        );
    }

    public TestChain<Client> setAdmin(UUID clientId, UUID adminId) {
        SetAdminToClientRequest setAdminToClientRequest =
                new SetAdminToClientRequest().adminId(adminId);

        return execute(
                () -> clientsApi.setAdminToClient()
                        .clientIdPath(clientId)
                        .body(setAdminToClientRequest)
                        .execute(Function.identity()),
                Client.class
        );
    }
}

package it.pagopa.interop.m2m.client.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.client.application.ClientGateway;
import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.ClientRef;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRef;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.ClientUpdateSeed;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class M2MClientGateway implements ClientGateway {

    private final M2MClientRestClient restClient;
    private final EntityStore entityStore;
    private final LastApiResponseStore lastApiResponseStore;

    @Override
    public Client getClient(ClientRef ref) {
        return restClient.getClient(ref.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(client -> toDomain(client, entityStore.getById(ref.id(), Client.class).orElse(null)))
                .updateContext()
                .get();
    }

    @Override
    public Client createClient(ClientCreationCommand creationCommand) {
        throw unsupportedOperation();
    }

    @Override
    public Client addKey(Client client, ClientKeyCreationCommand keyCreationCommand) {
        throw unsupportedOperation();
    }

    @Override
    public Client addKeys(Client client, List<ClientKeyCreationCommand> keyCreationCommands) {
        throw unsupportedOperation();
    }

    @Override
    public Client addUsersToClient(Client client, List<User> users) {
        throw unsupportedOperation();
    }

    @Override
    public Client addPurpose(Client client, Purpose purpose) {
        throw unsupportedOperation();
    }

    @Override
    public Client updateMetadata(Client client, String name, String description) {
        ClientUpdateSeed updateSeed = new ClientUpdateSeed()
                .name(name)
                .description(description);

        restClient.updateClient(client.getId(), updateSeed)
                .withPolling(PollingStrategy.UNTIL_SUCCESS);

        return PollingUtils.pollUntil(
                () -> getClient(ClientRef.of(client.getId())),
                updatedClient -> (name == null || Objects.equals(name, updatedClient.getName()))
                        && (description == null || Objects.equals(description, updatedClient.getDescription())),
                "Metadati attesi non ancora visibili per il client %s (nome: %s, descrizione: %s)"
                        .formatted(client.getId(), name, description)
        );
    }

    @Override
    public void tryToUpdateMetadata(Client client, String name, String description) {
        ClientUpdateSeed updateSeed = new ClientUpdateSeed()
                .name(name)
                .description(description);

        restClient.updateClient(client.getId(), updateSeed)
                .withoutPolling();
    }

    @Override
    public void shouldFailToUpdateMetadataAsForbidden() {
        assertThat(lastApiResponseStore.getLastResponse())
                .as("Risposta al tentativo di modifica dei metadati del client")
                .isNotNull()
                .extracting(ApiResponse::getStatusCode)
                .as("Modifica dei metadati del client vietata")
                .isEqualTo(403);
    }

    @Override
    public void shouldFailToUpdateMetadataAsMalformed() {
        assertThat(lastApiResponseStore.getLastResponse())
                .as("Risposta al tentativo di modifica dei metadati del client")
                .isNotNull()
                .extracting(ApiResponse::getStatusCode)
                .as("Modifica dei metadati del client vietata perché malformata")
                .isEqualTo(400);
    }

    @Override
    public Client setAdmin(Client client, UserRef admin) {
        throw unsupportedOperation();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.M2M_V3;
    }

    private static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("This client operation is not supported on M2M v3");
    }

    private static Client toDomain(
            it.pagopa.interop.generated.openapi.clients.m2m.v3.model.Client source,
            Client existingClient
    ) {
        Client.ClientBuilder client = existingClient == null
                ? Client.builder()
                : existingClient.toBuilder();

        return client
                .id(source.getId())
                .consumerId(source.getConsumerId())
                .name(source.getName())
                .description(source.getDescription())
                .build();
    }
}

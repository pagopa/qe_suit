package it.pagopa.interop.bff.client.infrastructure;

import it.pagopa.interop.bff.client.application.BffClientCreationCommand;
import it.pagopa.interop.bff.client.application.BffClientKeyCreationCommand;
import it.pagopa.interop.common.client.application.ClientGateway;
import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.template.action.TestChain;
import it.pagopa.interop.common.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.AddUsersToClientRequest;
import it.pagopa.interop.generated.openapi.clients.bff.model.CreatedResource;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeAdditionDetailsSeed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BffClientGateway implements ClientGateway {

    private final BffClientRestClient restClient;
    private final BffClientMapper mapper;

    @Override
    public Client getClient(ClientRef ref) {
        return restClient.getClient(ref.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toClient)
                .updateContext()
                .get();
    }

    @Override
    public Client createClient(ClientCreationCommand creationCommand) {
        if (!(creationCommand instanceof BffClientCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffClientCreationCommand");

        TestChain<CreatedResource> createClientChain = switch (bffCommand.getClientKind()) {
            case API -> restClient.createApiClient(bffCommand.getClientSeed());
            case CONSUMER -> restClient.createConsumerClient(bffCommand.getClientSeed());
        };

        return createClientChain
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ref -> {
                    Client client = getClient(ClientRef.of(ref.getId()));

                    // Associo le chiavi se specificato dal command
                    bffCommand.getKeysCommands().forEach(keyCommand -> {
                        addKeys(client, keyCommand.getKeySeed());
                        client.getKeys().add(keyCommand.getKey());
                    });

                    // Associo i membri al client
                    List<UserRef> linkedUsers = bffCommand.getUsers();
                    client.getUsers().addAll(linkedUsers);

                    return client;
                })
                .updateContext()
                .get();
    }

    @Override
    public Client addKey(Client client, ClientKeyCreationCommand keyCreationCommand) {
        if (!(keyCreationCommand instanceof BffClientKeyCreationCommand bffCommand))
            throw new IllegalArgumentException("Command must be an instance of BffClientKeyCreationCommand");

        return restClient.addKey(client.getId(), bffCommand.getKeySeed())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map((Void) -> getClient(ClientRef.of(client.getId())))
                .updateContext()
                .get();
    }

    @Override
    public Client addKeys(Client client, List<ClientKeyCreationCommand> keyCreationCommands) {
        var requests = keyCreationCommands.stream()
                .map(cmd -> (BffClientKeyCreationCommand) cmd)
                .map(BffClientKeyCreationCommand::getKeySeed)
                .toArray(KeySeed[]::new);

        addKeys(client, requests);
        return getClient(ClientRef.of(client.getId()));
    }

    @Override
    public Client addUsersToClient(Client client, List<User> users) {
        AddUsersToClientRequest request = new AddUsersToClientRequest()
                .userIds(users.stream().map(User::getUserId).toList());

        return restClient.addUsers(client.getId(), request)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map((res) -> getClient(ClientRef.of(res.getId())))
                .updateContext()
                .get();
    }

    @Override
    public Client addPurpose(Client client, Purpose purpose) {
        PurposeAdditionDetailsSeed purposeSeed = new PurposeAdditionDetailsSeed()
                .purposeId(purpose.getId());

        return restClient.linkPurpose(client.getId(), purposeSeed)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map((Void) -> getClient(ClientRef.of(client.getId())))
                .updateContext()
                .get();
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }

    private void addKeys(Client client, KeySeed... keySeeds) {
        Arrays.stream(keySeeds).forEach(keySeed ->
                restClient.addKey(client.getId(), keySeed)
                        .withPolling(PollingStrategy.UNTIL_SUCCESS)
        );
    }
}

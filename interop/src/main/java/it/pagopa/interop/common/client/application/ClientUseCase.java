package it.pagopa.interop.common.client.application;

import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ClientUseCase {

    private final ClientGateway clientGateway;
    private final ClientCommandFactory clientCommandFactory;
    private final ClientRequestFactory clientRequestFactory;

    public Client createClient(Consumer<ClientCreationCommand> creationCommand) {
        ClientCreationCommand command = clientCommandFactory.creationCommand();
        creationCommand.accept(command);
        return clientGateway.createClient(command);
    }

    public Client linkPurpose(Client client, Purpose purpose) {
        return clientGateway.addPurpose(client, purpose);
    }

    public void attemptClientKeyDeletion(Client client) {
        clientGateway.attemptClientKeyDeletion(client, client.getKeys().iterator().next());
    }

    public void attemptClientCreation(ClientKind kind, String name) {
        var command = clientRequestFactory.creationRequest().kind(kind).name(name);
        clientGateway.attemptClientCreation(command);
    }
}

package it.pagopa.interop.m2m.client.infrastructure;

import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.interop.common.client.application.ClientGateway;
import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.ClientRef;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRef;
import it.pagopa.interop.common.purpose.domain.Purpose;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class M2MClientGateway implements ClientGateway {

    private RawResponse lastResponse;

    @Override
    public Client getClient(ClientRef ref) {
        throw unsupportedOperation();
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
        throw unsupportedOperation();
    }

    @Override
    public void tryToUpdateMetadata(Client client, String name, String description) {
        // TODO PIN-11002: dopo il rilascio in DEV o QA e l'aggiornamento dello YAML M2M V3,
        //  invocare l'endpoint senza polling e salvare la risposta (attesa 403) in lastResponse.
        throw new UnsupportedOperationException(
                "Client metadata update attempts are not supported on M2M v3 until PIN-11002 is released in DEV or QA"
        );
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
}

package it.pagopa.interop.common.client.application;

import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.ClientRef;
import it.pagopa.interop.common.kernel.domain.KeyRegistrationMaterial;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRef;
import it.pagopa.interop.common.purpose.domain.Purpose;
import org.springframework.plugin.core.Plugin;
import it.pagopa.kernel.security.Key;

import java.util.List;

public interface ClientGateway extends Plugin<Channel> {
    Client getClient(ClientRef ref);

    Client createClient(ClientCreationCommand creationCommand);

    Client addKey(Client client, ClientKeyCreationCommand keyCreationCommand);

    Client addKeys(Client client, List<ClientKeyCreationCommand> keyCreationCommands);

    Client addUsersToClient(Client client, List<User> users);

    default Client addUserToClient(Client client, User user) {
        return addUsersToClient(client, List.of(user));
    }

    Client addPurpose(Client client, Purpose purpose);

    Client setAdmin(Client client, UserRef admin);

    void attemptClientKeyDeletion(Client client, Key key);

    void attemptClientCreation(ClientCreationCommand command);

    void attemptKeyRegistration(Client client, KeyRegistrationMaterial material, String name);

    void attemptPurposeAssociation(Client client, Purpose purpose);
}

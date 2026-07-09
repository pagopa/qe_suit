package it.pagopa.interop.new_arch.common.client.application;

import it.pagopa.interop.new_arch.common.client.domain.Client;
import it.pagopa.interop.new_arch.common.client.domain.ClientKind;
import it.pagopa.interop.new_arch.common.client.domain.ClientRef;
import it.pagopa.interop.new_arch.common.infrastructure.security.crypto.Key;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import it.pagopa.interop.new_arch.common.kernel.domain.User;
import it.pagopa.interop.new_arch.common.purpose.domain.Purpose;
import jakarta.annotation.Nullable;
import org.springframework.plugin.core.Plugin;

import java.util.List;

public interface ClientGateway extends Plugin<Channel> {
    Client getClient(ClientRef ref);

    Client createClient(ClientKind kind, @Nullable List<User> members);

    Client addKey(Client client, Key key);

    Client addUsersToClient(Client client, List<User> users);

    default Client addUserToClient(Client client, User user) {
        return addUsersToClient(client, List.of(user));
    }

    Client addPurpose(Client client, Purpose purpose);
}

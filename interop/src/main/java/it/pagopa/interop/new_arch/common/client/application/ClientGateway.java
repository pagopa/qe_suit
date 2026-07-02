package it.pagopa.interop.new_arch.common.client.application;

import it.pagopa.interop.new_arch.common.client.domain.Client;
import it.pagopa.interop.new_arch.common.client.domain.ClientKind;
import it.pagopa.interop.new_arch.common.client.domain.ClientRef;
import it.pagopa.interop.new_arch.common.infrastructure.security.Key;
import it.pagopa.interop.new_arch.common.kernel.domain.User;
import it.pagopa.interop.new_arch.common.purpose.domain.Purpose;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;

public interface ClientGateway {
    Client getClient(ClientRef ref);

    ClientRef createClient(ClientKind kind, @Nullable List<User> members);

    Optional<ClientRef> addKey(Client client, Key key);

    Optional<ClientRef> addUsersToClient(Client client, List<User> users);

    default Optional<ClientRef> addUserToClient(Client client, User user) {
        return addUsersToClient(client, List.of(user));
    }

    Optional<ClientRef> addPurpose(Client client, Purpose purpose);
}

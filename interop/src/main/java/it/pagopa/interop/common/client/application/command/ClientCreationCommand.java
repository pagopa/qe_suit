package it.pagopa.interop.common.client.application.command;

import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.kernel.domain.UserRef;

import java.util.List;
import java.util.function.Consumer;

public interface ClientCreationCommand {
    ClientCreationCommand name(String name);

    ClientCreationCommand kind(ClientKind kind);

    ClientCreationCommand users(List<UserRef> users);

    default ClientCreationCommand users(UserRef... users) {
        return users(List.of(users));
    }

    ClientCreationCommand keys(
            List<Consumer<ClientKeyCreationCommand>> keys
    );
}

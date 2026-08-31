package it.pagopa.interop.bff.client.application;

import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class BffClientCreationCommand implements ClientCreationCommand {

    @NotNull
    private final ClientSeed clientSeed = new ClientSeed();
    private final List<UserRef> users = new ArrayList<>();
    private final List<BffClientKeyCreationCommand> keysCommands = new ArrayList<>();

    @NotNull
    private ClientKind clientKind;

    @Override
    public ClientCreationCommand name(String name) {
        clientSeed.name(name);
        return this;
    }

    @Override
    public ClientCreationCommand kind(ClientKind kind) {
        clientKind = kind;
        return this;
    }

    @Override
    public ClientCreationCommand users(List<UserRef> users) {
        this.users.addAll(users);
        clientSeed.members(users.stream().map(UserRef::getUser).map(User::getUserId).toList());
        return this;
    }

    @Override
    public ClientCreationCommand keys(List<Consumer<ClientKeyCreationCommand>> keys) {
        this.keysCommands.addAll(
                keys.stream()
                        .map(keyConfigurer -> {
                            BffClientKeyCreationCommand command = new BffClientKeyCreationCommand();
                            keyConfigurer.accept(command);
                            return command;
                        })
                        .toList()
        );

        return this;
    }
}

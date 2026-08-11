package it.pagopa.interop.bff.client.application;

import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class BffClientCreationCommand implements ClientCreationCommand {

    @NotNull
    private final ClientSeed clientSeed = new ClientSeed();

    private final List<KeySeed> keys = new ArrayList<>();

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
    public ClientCreationCommand users(List<User> users) {
        clientSeed.members(users.stream().map(User::getUserId).toList());
        return this;
    }

    @Override
    public ClientCreationCommand keys(List<Consumer<ClientKeyCreationCommand>> keys) {
        this.keys.addAll(
                keys.stream()
                        .map(keyConfigurer -> {
                            BffClientKeyCreationCommand command = new BffClientKeyCreationCommand();
                            keyConfigurer.accept(command);
                            return command.getKeySeed();
                        })
                        .toList()
        );

        return this;
    }
}

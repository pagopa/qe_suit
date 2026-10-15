package it.pagopa.interop.bff.client.infrastructure;

import it.pagopa.interop.bff.client.application.BffClientCreationCommand;
import it.pagopa.interop.bff.client.application.BffClientKeyCreationCommand;
import it.pagopa.interop.common.client.application.ClientRequestFactory;
import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.generated.openapi.clients.bff.model.ClientSeed;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.instancio.Select.field;

@Component
@RequiredArgsConstructor
public class BffClientRequestFactory implements ClientRequestFactory {

    private static final UUID DEFAULT_MEMBER_ID = User.S_MATTIA.getUserId();

    private final CurrentUserSession currentUserSession;

    @Override
    public ClientCreationCommand creationRequest() {
        User user = getUser();
        UUID userId = user != null ? user.getUserId() : DEFAULT_MEMBER_ID;

        ClientSeed seed = Instancio.of(ClientSeed.class)
                .set(field(ClientSeed::getName), randomAlphanumericName("client", 24))
                .set(field(ClientSeed::getDescription), randomAlphanumericName("description", 32))
                .set(field(ClientSeed::getMembers), List.of(userId))
                .create();

        return BffClientCreationCommand.from(seed);
    }

    @Override
    public ClientKeyCreationCommand keyCreationRequest() {
        BffClientKeyCreationCommand command = new BffClientKeyCreationCommand();
        command.randomClientKey();
        return command;
    }

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.BFF;
    }

    private User getUser() {
        try {
            return currentUserSession.getUser();
        } catch (IllegalStateException e) {
            // Se utente non settato, restituisci null
            return null;
        }
    }
}

package it.pagopa.interop.bff.producer_keychain.infrastructure;

import it.pagopa.interop.bff.producer_keychain.application.BffProducerKeyCreationCommand;
import it.pagopa.interop.bff.producer_keychain.application.BffProducerKeychainCreationCommand;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainFactory;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeyCreationCommand;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BffProducerKeychainCommandFactory implements ProducerKeychainFactory {

    private static final UUID DEFAULT_USER_ID = User.S_MATTIA.getUserId();

    private final CurrentUserSession currentUserSession;

    @Override
    public ProducerKeychainCreationCommand creationCommand() {
        User user = getUser();
        UUID userId = user != null ? user.getUserId() : DEFAULT_USER_ID;
        return creationCommand(List.of(userId));
    }

    private User getUser() {
        try {
            return currentUserSession.getUser();
        } catch (IllegalStateException e) {
            // Se utente non settato, restituisci null
            return null;
        }
    }

    @Override
    public ProducerKeychainCreationCommand creationCommand(List<UUID> members) {
        return new BffProducerKeychainCreationCommand()
                .name(RandomUtils.randomAlphanumericName("producer-keychain", 24))
                .description(RandomUtils.randomAlphanumericName("description", 32))
                .members(members);
    }

    @Override
    public ProducerKeyCreationCommand keyCreationCommand() {
        return new BffProducerKeyCreationCommand().randomProducerKey();
    }

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.BFF;
    }
}


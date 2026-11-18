package it.pagopa.interop.m2m.producer_keychain.infrastructure;

import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.producer_keychain.application.ProducerKeychainFactory;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeyCreationCommand;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.interop.m2m.producer_keychain.application.M2MProducerKeyCreationCommand;
import it.pagopa.interop.m2m.producer_keychain.application.M2MProducerKeychainCreationCommand;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class M2MProducerKeychainCommandFactory implements ProducerKeychainFactory {
    private final AuthSession authSession;

    @Override
    public ProducerKeychainCreationCommand creationCommand() {
        return creationCommand(List.of(authSession.getActor().getId()));
    }

    @Override
    public ProducerKeychainCreationCommand creationCommand(List<UUID> members) {
        return new M2MProducerKeychainCreationCommand()
                .name(RandomUtils.randomAlphanumericName("producer-keychain", 24))
                .description(RandomUtils.randomAlphanumericName("description", 32))
                .members(members);
    }

    @Override
    public ProducerKeyCreationCommand keyCreationCommand() {
        return new M2MProducerKeyCreationCommand().randomProducerKey();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.M2M_V3;
    }
}

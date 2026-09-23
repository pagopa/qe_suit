package it.pagopa.interop.bff.producer_keychain.infrastructure;

import it.pagopa.interop.bff.client.application.BffClientKeyCreationCommand;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;
import static org.instancio.Select.field;

@Component
public class BffProducerKeychainRequestFactory {

    private static final UUID DEFAULT_MEMBER_ID = User.S_MATTIA.getUserId();

    public ProducerKeychainSeed creationRequest() {
        return Instancio.of(ProducerKeychainSeed.class)
                .set(field(ProducerKeychainSeed::getName), randomAlphanumericName("producer-keychain", 24))
                .set(field(ProducerKeychainSeed::getDescription), randomAlphanumericName("description", 32))
                .set(field(ProducerKeychainSeed::getMembers), List.of(DEFAULT_MEMBER_ID))
                .create();
    }

    public KeySeed keyCreationRequest() {
        BffClientKeyCreationCommand command = new BffClientKeyCreationCommand();
        command.randomClientConsumerKey();
        return command.getKeySeed();
    }
}


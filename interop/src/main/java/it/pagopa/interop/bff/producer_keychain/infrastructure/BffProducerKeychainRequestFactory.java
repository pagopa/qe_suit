package it.pagopa.interop.bff.producer_keychain.infrastructure;

import it.pagopa.interop.bff.client.application.BffClientKeyCreationCommand;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.generated.openapi.clients.bff.model.KeySeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.instancio.Select.field;

@Component
public class BffProducerKeychainRequestFactory {

    private static final UUID DEFAULT_MEMBER_ID = User.S_MATTIA.getUserId();

    public ProducerKeychainSeed creationRequest() {
        return Instancio.of(ProducerKeychainSeed.class)
                .generate(field(ProducerKeychainSeed::getName), gen -> gen.string().prefix("producer-keychain-").length(24))
                .generate(field(ProducerKeychainSeed::getDescription), gen -> gen.string().prefix("description-").length(32))
                .set(field(ProducerKeychainSeed::getMembers), List.of(DEFAULT_MEMBER_ID))
                .create();
    }

    public KeySeed keyCreationRequest() {
        BffClientKeyCreationCommand command = new BffClientKeyCreationCommand();
        command.randomClientConsumerKey();
        return command.getKeySeed();
    }
}


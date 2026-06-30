package it.pagopa.interop.bff.support;

import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.instancio.Select.field;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProducerKeychainSeedFactory {

    public ProducerKeychainSeed fullCreationRequest() {
        return Instancio.of(ProducerKeychainSeed.class)
                .set(field(ProducerKeychainSeed::getName), "keychain-" + java.util.UUID.randomUUID())
                .set(field(ProducerKeychainSeed::getDescription), "descrizione test")
                .set(field(ProducerKeychainSeed::getMembers), List.of())
                .create();
    }
}

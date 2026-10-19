package it.pagopa.interop.bff.producer_keychain.application;

import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerKeychainSeed;

import java.util.List;
import java.util.UUID;
import lombok.Getter;

public class BffProducerKeychainCreationCommand implements ProducerKeychainCreationCommand {
    @Getter
    private final ProducerKeychainSeed seed = new ProducerKeychainSeed();

    @Override
    public BffProducerKeychainCreationCommand name(String name) {
        seed.setName(name);
        return this;
    }

    @Override
    public BffProducerKeychainCreationCommand description(String description) {
        seed.setDescription(description);
        return this;
    }

    @Override
    public BffProducerKeychainCreationCommand members(List<UUID> members) {
        seed.setMembers(List.copyOf(members));
        return this;
    }
}

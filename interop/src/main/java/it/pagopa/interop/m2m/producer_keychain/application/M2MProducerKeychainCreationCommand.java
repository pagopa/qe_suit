package it.pagopa.interop.m2m.producer_keychain.application;

import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import it.pagopa.interop.generated.openapi.clients.m2m.v3.model.ProducerKeychainSeed;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class M2MProducerKeychainCreationCommand implements ProducerKeychainCreationCommand {
    private final ProducerKeychainSeed seed = new ProducerKeychainSeed();

    @Override
    public M2MProducerKeychainCreationCommand name(String name) {
        seed.setName(name);
        return this;
    }

    @Override
    public M2MProducerKeychainCreationCommand description(String description) {
        seed.setDescription(description);
        return this;
    }

    @Override
    public M2MProducerKeychainCreationCommand members(List<UUID> members) {
        seed.setMembers(List.copyOf(members));
        return this;
    }
}

package it.pagopa.interop.common.producer_keychain.application.command;

import java.util.List;
import java.util.UUID;

public interface ProducerKeychainCreationCommand {
    ProducerKeychainCreationCommand name(String name);
    ProducerKeychainCreationCommand description(String description);
    ProducerKeychainCreationCommand members(List<UUID> members);
}

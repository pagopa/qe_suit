package it.pagopa.interop.common.producer_keychain.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeyCreationCommand;
import it.pagopa.interop.common.producer_keychain.application.command.ProducerKeychainCreationCommand;
import org.springframework.plugin.core.Plugin;

import java.util.List;
import java.util.UUID;

public interface ProducerKeychainFactory extends Plugin<Channel> {
    ProducerKeychainCreationCommand creationCommand();
    ProducerKeychainCreationCommand creationCommand(List<UUID> members);
    ProducerKeyCreationCommand keyCreationCommand();
}

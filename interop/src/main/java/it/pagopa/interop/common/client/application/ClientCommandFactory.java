package it.pagopa.interop.common.client.application;

import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;
import org.springframework.stereotype.Component;

@Component
public interface ClientCommandFactory extends Plugin<Channel> {
    ClientCreationCommand creationCommand();
}

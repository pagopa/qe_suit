package it.pagopa.interop.common.client.application;

import it.pagopa.interop.common.client.application.command.ClientCreationCommand;
import it.pagopa.interop.common.client.application.command.ClientKeyCreationCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface ClientRequestFactory extends Plugin<Channel> {
    ClientCreationCommand creationRequest();
    ClientKeyCreationCommand keyCreationRequest();
}

package it.pagopa.interop.common.attribute.application;

import it.pagopa.interop.common.attribute.application.command.AttributeCreationCommand;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface AttributeRequestFactory extends Plugin<Channel> {
    AttributeCreationCommand defaultDeclaredAttributeCreationCommand();
}


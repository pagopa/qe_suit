package it.pagopa.interop.new_arch.common.eservice.application;

import it.pagopa.interop.new_arch.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.new_arch.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.new_arch.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface EServiceRequestFactory extends Plugin<Channel> {
    EServiceCreationCommand defaultCreationEServiceCommand();
    UpdateEServiceDescriptorCommand defaultUpdateDescriptorCommand();
}

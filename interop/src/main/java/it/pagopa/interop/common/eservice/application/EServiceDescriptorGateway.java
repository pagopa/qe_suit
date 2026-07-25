package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

public interface EServiceDescriptorGateway extends Plugin<Channel> {

    EServiceDescriptor getEServiceDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    EServiceDescriptor publishDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    EServiceDescriptor updateDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, UpdateEServiceDescriptorCommand command);
}

package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

import java.io.File;

public interface EServiceDescriptorGateway extends Plugin<Channel> {

    EServiceDescriptor getEServiceDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    EServiceDescriptor addDescriptor(EServiceRef eServiceRef);

    EServiceDescriptor publishDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef);

    EServiceDescriptor updateDescriptor(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, UpdateEServiceDescriptorCommand command);

    EServiceDescriptor linkOpenApiInterface(EServiceRef eServiceRef, EServiceDescriptorRef descriptorRef, String openApiInterfacePath);
}

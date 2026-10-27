package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.application.command.EServiceTemplateInstanceUpdateDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.springframework.plugin.core.Plugin;

public interface EServiceTemplateInstanceGateway extends Plugin<Channel> {
    EService createEServiceInstance(EServiceTemplate template, EServiceTemplateInstanceCreationCommand command);

    EService updateEServiceInstanceDescriptor(EServiceRef eServiceInstanceRef, EServiceDescriptorRef descriptorRef, EServiceDescriptorState currentDescriptorState, EServiceTemplateInstanceUpdateDescriptorCommand command);
}

package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.application.command.EServiceTemplateInstanceUpdateDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EServiceTemplateInstanceUseCase {
    private final EServiceTemplateInstanceGateway gateway;

    public EService createEServiceInstance(EServiceTemplate template) {
        return gateway.createEServiceInstance(template);
    }

    public EService updateEServiceInstanceDescriptor(
            EServiceRef eServiceRef,
            EServiceDescriptorRef descriptorRef,
            EServiceDescriptorState currentDescriptorState,
            EServiceTemplateInstanceUpdateDescriptorCommand command
    ) {
        return gateway.updateEServiceInstanceDescriptor(eServiceRef, descriptorRef, currentDescriptorState, command);
    }
}

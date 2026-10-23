package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EServiceTemplateInstanceUseCase {
    private final EServiceTemplateInstanceGateway gateway;

    public EService createEServiceInstance(EServiceTemplate template) {
        return gateway.createEServiceInstance(template);
    }

    public EServiceDescriptor prepareDescriptorForPublication(EService instance, EServiceDescriptor descriptor) {
        if (descriptor.getState() != EServiceDescriptorState.DRAFT) {
            throw new IllegalStateException("Cannot prepare a template instance descriptor in state " + descriptor.getState());
        }
        return gateway.updateDescriptor(instance, descriptor);
    }
}

package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EServiceDescriptorUseCase {
    private final EServiceDescriptorGateway eServiceDescriptorGateway;
    private final EServiceRequestFactory requestFactory;

    public EServiceDescriptor getDescriptor(EService eService, EServiceDescriptor descriptor) {
        return eServiceDescriptorGateway.getEServiceDescriptor(eService.getRef(), descriptor.getRef());
    }

    public EServiceDescriptor publishDescriptor(EService eService, EServiceDescriptor descriptor) {
        if (descriptor.getState() == EServiceDescriptorState.PUBLISHED) return descriptor;

        if (descriptor.getState() != EServiceDescriptorState.DRAFT)
            throw new IllegalStateException("Cannot publish a descriptor that is in state " + descriptor.getState());

        return eServiceDescriptorGateway.publishDescriptor(eService.getRef(), descriptor.getRef());
    }

    public EServiceDescriptor updateDescriptor(EService eService, EServiceDescriptor descriptor, UpdateEServiceDescriptorCommand command) {
        return eServiceDescriptorGateway.updateDescriptor(eService.getRef(), descriptor.getRef(), command);
    }

    public EServiceDescriptor linkOpenApiInterface(EService eService, EServiceDescriptor descriptor, String openApiInterfacePath) {
        return eServiceDescriptorGateway.linkOpenApiInterface(eService.getRef(), descriptor.getRef(), openApiInterfacePath);
    }

    public EServiceDescriptor prepareDescriptorForPublication(EService eService, EServiceDescriptor descriptor,  Consumer<UpdateEServiceDescriptorCommand> config) {
        UpdateEServiceDescriptorCommand command = requestFactory.defaultUpdateDescriptorCommand();
        config.accept(command);
        return updateDescriptor(eService, descriptor, command);
    }

    public EServiceDescriptor prepareDescriptorForPublication(EService eService, EServiceDescriptor descriptor){
        UpdateEServiceDescriptorCommand command = requestFactory.defaultUpdateDescriptorCommand();
        EServiceDescriptor updatedDescriptor = updateDescriptor(eService, descriptor, command);

        return linkOpenApiInterface(eService, updatedDescriptor, "assets/origin-interface.yaml");
    }
}

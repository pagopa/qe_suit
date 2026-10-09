package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EServiceDescriptorUseCase {
    private final EServiceDescriptorGateway eServiceDescriptorGateway;
    private final EServiceRequestFactory requestFactory;

    public EServiceDescriptor getDescriptor(EService eService, EServiceDescriptor descriptor) {
        return eServiceDescriptorGateway.getEServiceDescriptor(eService.getRef(), descriptor.getRef());
    }

    public EServiceDescriptor addDescriptor(EService eService){
        return eServiceDescriptorGateway.addDescriptor(eService.getRef());
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

    public EServiceDescriptor archiveDescriptor(EService eService, EServiceDescriptor descriptor, GracePeriodDays gracePeriodDays) {
        eServiceDescriptorGateway.archiveDescriptor(eService.getRef(), descriptor.getRef(), gracePeriodDays);
        return descriptor;
    }

    public EServiceDescriptor prepareDescriptorForPublication(EService eService, EServiceDescriptor descriptor,  Consumer<UpdateEServiceDescriptorCommand> config) {
        UpdateEServiceDescriptorCommand command = requestFactory.defaultUpdateDescriptorCommand();
        config.accept(command);
        return updateDescriptor(eService, descriptor, command);
    }

    public EServiceDescriptor prepareDescriptorForPublication(EService eService, EServiceDescriptor descriptor, UpdateEServiceDescriptorCommand... command){
        if (command.length == 0)
            command = new UpdateEServiceDescriptorCommand[] { requestFactory.defaultUpdateDescriptorCommand() };

        EServiceDescriptor updatedDescriptor = updateDescriptor(eService, descriptor, command[0]);

        return linkOpenApiInterface(eService, updatedDescriptor, "assets/origin-interface.yaml");
    }

    public EServiceCreationCommand getDefaultEServiceCreationCommand(boolean async) {
        EServiceCreationCommand command = requestFactory.defaultCreationEServiceCommand();
        command.isAsync(async);
        return command;
    }

    public UpdateEServiceDescriptorCommand getUpdateEServiceDescriptorCommand(
            int consumerThreshold,
            int totalThreshold
    ) {
        UpdateEServiceDescriptorCommand command = requestFactory.defaultUpdateDescriptorCommand();
        command.dailyCallsPerConsumer(consumerThreshold);
        command.dailyCallsTotal(totalThreshold);
        return command;
    }
}

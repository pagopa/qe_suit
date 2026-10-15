package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

    public EServiceDescriptor prepareDescriptorForPublication(EService eService, EServiceDescriptor descriptor, UpdateEServiceDescriptorCommand command){
        if (command == null) command = requestFactory.defaultUpdateDescriptorCommand();
        EServiceDescriptor updatedDescriptor = updateDescriptor(eService, descriptor, command);

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

    public EServiceDescriptor configureTemplateInstanceSOAPInterface(
            EService eService,
            EServiceDescriptor descriptor,
            List<SoapAddressConfiguration> addresses
    ) {
        return eServiceDescriptorGateway.configureTemplateInstanceSOAPInterface(
                eService.getRef(), descriptor.getRef(), List.copyOf(addresses)
        );
    }

    public void shouldFailToConfigureSOAPInterface(
            EService eService,
            EServiceDescriptor descriptor,
            List<SoapAddressConfiguration> addresses,
            EServiceTemplateSOAPInterfaceFailureReason reason
    ) {
        var before = eServiceDescriptorGateway.readInterfaceSnapshot(eService.getRef(), descriptor.getRef());
        before.filter(snapshot -> !snapshot.hasDownloadedContent()).ifPresent(snapshot -> {
            throw new IllegalStateException("Snapshot interfaccia privo del contenuto binario scaricato");
        });

        eServiceDescriptorGateway.shouldFailToConfigureSOAPInterface(
                eService.getRef(), descriptor.getRef(), List.copyOf(addresses), reason
        );

        var after = eServiceDescriptorGateway.readInterfaceSnapshotAfterDelay(eService.getRef(), descriptor.getRef());
        assertSameSnapshot(before, after, reason);
    }

    public EServiceDescriptor configureTemplateInstanceRESTInterface(
            EService eService,
            EServiceDescriptor descriptor,
            RestInterfaceConfiguration configuration
    ) {
        return eServiceDescriptorGateway.configureTemplateInstanceRESTInterface(
                eService.getRef(), descriptor.getRef(), configuration
        );
    }

    public EServiceDescriptor downloadInterface(EService eService, EServiceDescriptor descriptor) {
        return eServiceDescriptorGateway.downloadInterface(eService.getRef(), descriptor.getRef());
    }

    private static void assertSameSnapshot(
            java.util.Optional<it.pagopa.interop.common.eservice.domain.EServiceDescriptorInterface> before,
            java.util.Optional<it.pagopa.interop.common.eservice.domain.EServiceDescriptorInterface> after,
            EServiceTemplateSOAPInterfaceFailureReason reason
    ) {
        if (before.isEmpty() != after.isEmpty()) {
            throw new AssertionError("La presenza dell'interfaccia è cambiata dopo il rifiuto (" + reason + ")");
        }
        if (before.isEmpty()) {
            return;
        }
        var original = before.orElseThrow();
        var current = after.orElseThrow();
        if (!original.hasDownloadedContent() || !current.hasDownloadedContent()) {
            throw new IllegalStateException("Snapshot interfaccia privo del contenuto binario scaricato");
        }
        if (!Objects.equals(original.getId(), current.getId())
                || !Objects.equals(original.getChecksum(), current.getChecksum())
                || !Objects.equals(original.getName(), current.getName())
                || !Objects.equals(original.getPrettyName(), current.getPrettyName())
                || !Objects.equals(original.getContentType(), current.getContentType())
                || !Objects.equals(original.getEServiceRef(), current.getEServiceRef())
                || !Objects.equals(original.getDescriptorRef(), current.getDescriptorRef())
                || !java.util.Arrays.equals(original.getContent(), current.getContent())) {
            throw new AssertionError("L'interfaccia è cambiata dopo il rifiuto (" + reason + ")");
        }
    }
}

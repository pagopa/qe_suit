package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorInterface;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BffEServiceDescriptorLifecycleTest {
    private final BffEServiceRestClient restClient = mock(BffEServiceRestClient.class, RETURNS_DEEP_STUBS);
    private final EntityStore store = mock(EntityStore.class);
    private final BffEServiceTemplateInstanceDescriptorRestClient instanceClient =
            mock(BffEServiceTemplateInstanceDescriptorRestClient.class, RETURNS_DEEP_STUBS);
    private final BffEServiceDescriptorInterfaceGateway interfaceGateway =
            mock(BffEServiceDescriptorInterfaceGateway.class);
    private final BffEServiceDescriptorGateway gateway = spy(new BffEServiceDescriptorGateway(
            restClient, store, mock(BffEServiceDescriptorMapper.class),
            interfaceGateway, mock(BffEServiceDescriptorArchiveGateway.class),
            new BffEServiceDescriptorUpdateGateway(restClient, instanceClient, interfaceGateway, store)
    ));
    private final EServiceRef serviceRef = EServiceRef.of(UUID.randomUUID());
    private final EServiceDescriptorRef descriptorRef = EServiceDescriptorRef.of(UUID.randomUUID());

    @ParameterizedTest
    @CsvSource({"PUBLISHED,SUSPENDED", "DEPRECATED,SUSPENDED", "ARCHIVING,ARCHIVING_SUSPENDED"})
    void waitsForTheSuspensionStateCorrespondingToTheSource(
            EServiceDescriptorState source, EServiceDescriptorState target
    ) {
        EServiceDescriptor before = EServiceDescriptor.builder().id(descriptorRef.id()).state(source).build();
        EServiceDescriptor after = before.toBuilder().state(target).build();
        doReturn(before, after).when(gateway).getEServiceDescriptor(serviceRef, descriptorRef);

        assertThat(gateway.suspendDescriptor(serviceRef, descriptorRef)).isSameAs(after);
        verify(restClient).suspendDescriptor(serviceRef.id(), descriptorRef.id());
    }

    @Test
    void pollsTheDescriptorUntilTheExpectedLifecycleStateIsVisible() {
        EServiceDescriptor published = EServiceDescriptor.builder()
                .id(descriptorRef.id()).state(EServiceDescriptorState.PUBLISHED).build();
        EServiceDescriptor deprecated = published.toBuilder()
                .state(EServiceDescriptorState.DEPRECATED).build();
        doReturn(published, deprecated).when(gateway).getEServiceDescriptor(serviceRef, descriptorRef);

        assertThat(gateway.awaitDescriptorState(serviceRef, descriptorRef, EServiceDescriptorState.DEPRECATED))
                .isSameAs(deprecated);

        verify(gateway, times(2)).getEServiceDescriptor(serviceRef, descriptorRef);
    }

    @Test
    void delegatedPublicationPollsForWaitingForApprovalInsteadOfPublished() {
        EServiceDescriptor draft = EServiceDescriptor.builder()
                .id(descriptorRef.id()).state(EServiceDescriptorState.DRAFT).build();
        EServiceDescriptor waiting = draft.toBuilder()
                .state(EServiceDescriptorState.WAITING_FOR_APPROVAL).build();
        doReturn(draft, waiting).when(gateway).getEServiceDescriptor(serviceRef, descriptorRef);

        assertThat(gateway.publishDescriptorAwaitingApproval(serviceRef, descriptorRef)).isSameAs(waiting);

        verify(restClient).publishDescriptor(serviceRef.id(), descriptorRef.id());
        verify(gateway, times(2)).getEServiceDescriptor(serviceRef, descriptorRef);
    }

    @Test
    void updateWithoutInterfacePathDoesNotUploadAnInterface() {
        BffUpdateEServiceDescriptorCommand command = updateCommand();
        EServiceDescriptor updated = EServiceDescriptor.builder()
                .id(descriptorRef.id()).dailyCallsPerConsumer(1).dailyCallsTotal(10)
                .interfaceDocument(mock(EServiceDescriptorInterface.class)).build();
        doReturn(updated).when(gateway).getEServiceDescriptor(serviceRef, descriptorRef);

        assertThat(gateway.updateDescriptor(serviceRef, descriptorRef, command)).isSameAs(updated);
        verify(gateway, never()).linkOpenApiInterface(any(), any(), anyString());
    }

    @Test
    void updateUploadsTheInterfaceSpecifiedByTheCommand() {
        BffUpdateEServiceDescriptorCommand command = updateCommand();
        command.interfaceDocumentPath("assets/custom-interface.yaml");
        EServiceDescriptor updated = EServiceDescriptor.builder().id(descriptorRef.id())
                .dailyCallsPerConsumer(1).dailyCallsTotal(10).build();
        EServiceDescriptor linked = updated.toBuilder()
                .interfaceDocument(mock(EServiceDescriptorInterface.class)).build();
        doReturn(updated).when(gateway).getEServiceDescriptor(serviceRef, descriptorRef);
        doReturn(linked).when(gateway).linkOpenApiInterface(
                serviceRef, descriptorRef, "assets/custom-interface.yaml"
        );

        assertThat(gateway.updateDescriptor(serviceRef, descriptorRef, command)).isSameAs(linked);
    }

    private BffUpdateEServiceDescriptorCommand updateCommand() {
        when(store.getById(serviceRef.id(), EService.class)).thenReturn(java.util.Optional.of(
                EService.builder().id(serviceRef.id()).asyncExchange(false)
                        .descriptors(java.util.List.of(EServiceDescriptor.builder().id(descriptorRef.id()).build())).build()
        ));
        BffUpdateEServiceDescriptorCommand command = new BffUpdateEServiceDescriptorCommand();
        command.dailyCallsPerConsumer(1).dailyCallsTotal(10);
        return command;
    }
}

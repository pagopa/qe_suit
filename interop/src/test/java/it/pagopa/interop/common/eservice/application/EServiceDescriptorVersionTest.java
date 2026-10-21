package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorInterface;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceDescriptorVersionTest {
    private final EServiceDescriptorGateway gateway = mock(EServiceDescriptorGateway.class);
    private final EServiceRequestFactory factory = mock(EServiceRequestFactory.class);
    private final EServiceDescriptorUseCase useCase =
            new EServiceDescriptorUseCase(gateway, factory);
    private final EServiceDescriptor published = EServiceDescriptor.builder()
            .id(UUID.randomUUID()).version("3").state(PUBLISHED).build();
    private final EService eService = EService.builder().id(UUID.randomUUID())
            .descriptors(new ArrayList<>(List.of(published))).build();

    @Test
    void returnsExistingVersionWithoutCreatingAnything() {
        assertThat(useCase.getOrCreateDescriptor(eService, 3)).isSameAs(published);
        verifyNoInteractions(gateway);
    }

    @Test
    void createsNextVersionUsingMaximumVersionRatherThanCount() {
        EServiceDescriptor next = published.toBuilder().id(UUID.randomUUID()).version("4").state(DRAFT).build();
        when(gateway.addDescriptor(eService.getRef())).thenReturn(next);
        assertThat(useCase.getOrCreateDescriptor(eService, 4)).isSameAs(next);
        assertThat(eService.getDescriptorOrThrow(4)).isSameAs(next);
        verify(gateway).addDescriptor(eService.getRef());
    }

    @Test
    void rejectsSkippedVersionsWithSpecificMessage() {
        assertThatThrownBy(() -> useCase.getOrCreateDescriptor(eService, 5))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("latest available version is 3");
        verifyNoInteractions(gateway);
    }

    @Test
    void rejectsCreationWhileAnotherDraftExists() {
        eService.upsertDescriptor(published.toBuilder().state(DRAFT).build());
        assertThatThrownBy(() -> useCase.getOrCreateDescriptor(eService, 4))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("draft status");
        verifyNoInteractions(gateway);
    }

    @Test
    void preparesPublicationThroughFactoryAndOrdinaryUpdate() {
        var draft = published.toBuilder().state(DRAFT).build();
        var command = mock(UpdateEServiceDescriptorCommand.class);
        when(factory.prepareDescriptorForPublicationCommand()).thenReturn(command);
        when(gateway.updateDescriptor(eService.getRef(), draft.getRef(), command)).thenReturn(draft);

        assertThat(useCase.prepareDescriptorForPublication(eService, draft,
                (UpdateEServiceDescriptorCommand) null))
                .isSameAs(draft);
        verify(gateway).updateDescriptor(eService.getRef(), draft.getRef(), command);
        verifyNoInteractions(command);
    }

    @Test
    void defaultPreparationPreservesExistingInterface() {
        var draft = published.toBuilder().state(DRAFT)
                .interfaceDocument(mock(EServiceDescriptorInterface.class)).build();
        var command = mock(UpdateEServiceDescriptorCommand.class);
        when(factory.prepareDescriptorForPublicationCommand()).thenReturn(command);

        useCase.prepareDescriptorForPublication(eService, draft, (UpdateEServiceDescriptorCommand) null);

        verify(command).interfaceDocumentPath(null);
        verify(gateway).updateDescriptor(eService.getRef(), draft.getRef(), command);
    }

    @Test
    void explicitInterfacePathIsRetainedEvenWhenAnInterfaceExists() {
        var draft = published.toBuilder().state(DRAFT)
                .interfaceDocument(mock(EServiceDescriptorInterface.class)).build();
        var command = mock(UpdateEServiceDescriptorCommand.class);
        when(command.getInterfaceDocumentPath()).thenReturn("custom-interface.yaml");

        useCase.prepareDescriptorForPublication(eService, draft, command);

        verify(command, never()).interfaceDocumentPath(any());
        verifyNoInteractions(factory);
        verify(gateway).updateDescriptor(eService.getRef(), draft.getRef(), command);
    }
}

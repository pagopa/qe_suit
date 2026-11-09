package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceDescriptorRequiredAttributesTest {
    @Test
    void publicationPreparationPreservesPreviouslyConfiguredRequirements() {
        var gateway = mock(EServiceDescriptorGateway.class);
        var factory = mock(EServiceRequestFactory.class);
        var command = mock(UpdateEServiceDescriptorCommand.class);
        var attributes = Attributes.builder().certified(
                it.pagopa.interop.common.attribute.domain.Attribute.builder().id(UUID.randomUUID()).build()).build();
        var descriptor = EServiceDescriptor.builder().id(UUID.randomUUID())
                .state(EServiceDescriptorState.DRAFT).attributes(attributes).build();
        var service = EService.builder().id(UUID.randomUUID()).build();
        when(factory.prepareDescriptorForPublicationCommand()).thenReturn(command);

        new EServiceDescriptorUseCase(gateway, factory).prepareDescriptorForPublication(
                service, descriptor, (UpdateEServiceDescriptorCommand) null);

        verify(command).attributes(attributes);
        verify(gateway).updateDescriptor(service.getRef(), descriptor.getRef(), command);
    }

    @Test
    void updatesRequiredAttributesOnDraftUsingTheRoutedRequestFactory() {
        var gateway = mock(EServiceDescriptorGateway.class);
        var factory = mock(EServiceRequestFactory.class);
        var command = mock(UpdateEServiceDescriptorCommand.class, RETURNS_SELF);
        var service = EService.builder().id(UUID.randomUUID()).build();
        var descriptor = EServiceDescriptor.builder().id(UUID.randomUUID()).version("1")
                .state(EServiceDescriptorState.DRAFT).build();
        var attributes = Attributes.builder().build();
        var updated = descriptor.toBuilder().build();
        when(factory.defaultUpdateDescriptorCommand()).thenReturn(command);
        when(gateway.updateDescriptor(service.getRef(), descriptor.getRef(), command)).thenReturn(updated);

        assertThat(new EServiceDescriptorUseCase(gateway, factory)
                .updateRequiredAttributes(service, descriptor, attributes)).isSameAs(updated);

        verify(factory).defaultUpdateDescriptorCommand();
        verify(command).attributes(attributes);
        verify(gateway).updateDescriptor(service.getRef(), descriptor.getRef(), command);
        verifyNoMoreInteractions(factory, command, gateway);
    }

    @Test
    void refusesToConfigureANonDraftDescriptor() {
        var gateway = mock(EServiceDescriptorGateway.class);
        var factory = mock(EServiceRequestFactory.class);
        var service = EService.builder().id(UUID.randomUUID()).build();
        var descriptor = EServiceDescriptor.builder().id(UUID.randomUUID()).version("1")
                .state(EServiceDescriptorState.PUBLISHED).build();

        assertThatThrownBy(() -> new EServiceDescriptorUseCase(gateway, factory)
                .updateRequiredAttributes(service, descriptor, Attributes.builder().build()))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("draft descriptor");
        verifyNoInteractions(factory, gateway);
    }
}

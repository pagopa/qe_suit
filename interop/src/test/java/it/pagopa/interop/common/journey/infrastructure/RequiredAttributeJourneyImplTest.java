package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.attribute.domain.AttributeOwnershipSelection;
import it.pagopa.interop.common.attribute.domain.Attributes;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.RequiredAttributeUseCase;
import it.pagopa.interop.common.eservice.application.command.RequiredAttributeCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequiredAttributeJourneyImplTest {
    private static final TenantRef PRODUCER = TenantRef.of(Tenant.COMUNE_DI_MILANO.getOrganizationId());

    @Test
    void resolvesReadOnlySelectionsThenUpdatesOnlyTheSelectedDraftDescriptor() {
        var descriptor = EServiceDescriptor.builder().id(UUID.randomUUID()).version("1")
                .state(EServiceDescriptorState.DRAFT).build();
        var service = EService.builder().id(UUID.randomUUID())
                .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId().toString())
                .descriptors(List.of(descriptor)).build();
        var requests = List.of(new RequiredAttributeCommand(AttributeKind.CERTIFIED, 1,
                AttributeOwnershipSelection.OWNED_BY_PAGOPA));
        var attributes = Attributes.builder().certified(Attribute.builder()
                .id(UUID.randomUUID()).kind(AttributeKind.CERTIFIED).group(1).build()).build();
        var resolver = mock(RequiredAttributeUseCase.class);
        var descriptors = mock(EServiceDescriptorUseCase.class);
        var session = mock(CurrentUserSession.class);
        User producerAdmin = User.getTenantAdmin(Tenant.COMUNE_DI_MILANO);
        when(session.getUser()).thenReturn(producerAdmin);
        when(session.getTenant()).thenReturn(Tenant.COMUNE_DI_MILANO);
        when(resolver.resolve(PRODUCER, requests)).thenReturn(attributes);
        var journey = new RequiredAttributeJourneyImpl(resolver, descriptors, session);

        assertThat(journey.configureRequiredAttributes(service, 1, PRODUCER, requests)).isSameAs(journey);

        var order = inOrder(session, resolver, descriptors);
        order.verify(session).getUser();
        order.verify(session).getTenant();
        order.verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
        order.verify(resolver).resolve(PRODUCER, requests);
        order.verify(session).set(producerAdmin, Tenant.COMUNE_DI_MILANO);
        order.verify(descriptors).updateRequiredAttributes(service, descriptor, attributes);
        order.verifyNoMoreInteractions();
    }

    @Test
    void rejectsProducerMismatchBeforeReadingAssignments() {
        var descriptor = EServiceDescriptor.builder().id(UUID.randomUUID()).version("1")
                .state(EServiceDescriptorState.DRAFT).build();
        var service = EService.builder().id(UUID.randomUUID()).producerId(UUID.randomUUID().toString())
                .descriptors(List.of(descriptor)).build();
        var resolver = mock(RequiredAttributeUseCase.class);
        var descriptors = mock(EServiceDescriptorUseCase.class);
        var session = mock(CurrentUserSession.class);

        assertThatThrownBy(() -> new RequiredAttributeJourneyImpl(resolver, descriptors, session)
                .configureRequiredAttributes(service, 1, PRODUCER, List.of()))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("does not own");
        verifyNoInteractions(resolver, descriptors, session);
    }
}

package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.*;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceJourneyLifecycleTest {
    private final EServiceUseCase services = mock(EServiceUseCase.class);
    private final EServiceDescriptorUseCase descriptors = mock(EServiceDescriptorUseCase.class);
    private final EServiceDescriptorLifecycleUseCase lifecycle = mock(EServiceDescriptorLifecycleUseCase.class);
    private final EntityStore store = mock(EntityStore.class);
    private final EServiceRequestFactory requestFactory = mock(EServiceRequestFactory.class);
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    private final EServiceDescriptor draft = EServiceDescriptor.builder()
            .id(UUID.randomUUID()).version("1").state(DRAFT).build();
    private final EService eService = EService.builder().id(UUID.randomUUID())
            .descriptors(new ArrayList<>(List.of(draft))).build();

    @Test
    void serviceJourneyCallsLifecycleDirectlyAndStoresItsResult() {
        var journey = new EServiceJourneyImpl(services, descriptors, lifecycle, store,
                mock(EServiceRiskAnalysisUseCase.class), mock(CurrentUserSession.class),
                mock(EServiceRequestFactory.class));
        var command = mock(EServiceCreationCommand.class);
        var suspended = draft.toBuilder().state(SUSPENDED).build();
        when(services.createEService(command)).thenReturn(eService);
        when(lifecycle.bringToState(eService, draft, SUSPENDED)).thenReturn(suspended);

        assertThat(journey.createEService(command, SUSPENDED)).isSameAs(journey);

        var order = inOrder(lifecycle, services, store);
        order.verify(lifecycle).validateCanReachState(DRAFT, SUSPENDED);
        order.verify(services).createEService(command);
        order.verify(lifecycle).bringToState(eService, draft, SUSPENDED);
        order.verify(store).upsert(eService);
        assertThat(eService.getDescriptorOrThrow(1)).isSameAs(suspended);
    }

    @Test
    void descriptorJourneySeparatesVersionResolutionFromLifecycle() {
        var journey = new EServiceDescriptorJourneyImpl(descriptors, lifecycle, store, requestFactory, session);
        when(descriptors.getOrCreateDescriptor(eService, 1)).thenReturn(draft);
        var suspended = draft.toBuilder().state(SUSPENDED).build();
        when(lifecycle.bringToState(eService, draft, SUSPENDED, null)).thenReturn(suspended);

        journey.bringEServiceDescriptorToState(eService, 1, SUSPENDED);

        var order = inOrder(descriptors, lifecycle, store);
        order.verify(descriptors).getOrCreateDescriptor(eService, 1);
        order.verify(lifecycle).bringToState(eService, draft, SUSPENDED, null);
        order.verify(store).upsert(eService);
        assertThat(eService.getDescriptorOrThrow(1)).isSameAs(suspended);
    }

    @Test
    void rejectsUnsupportedArchivePathBeforeResolvingOrCreatingMissingVersion() {
        var journey = new EServiceDescriptorJourneyImpl(descriptors, lifecycle, store, requestFactory, session);
        doThrow(new UnsupportedOperationException("unsupported"))
                .when(lifecycle).validateCanReachState(DRAFT, ARCHIVED);

        assertThatThrownBy(() -> journey.bringEServiceDescriptorToState(eService, 2, ARCHIVED))
                .isInstanceOf(UnsupportedOperationException.class);

        verifyNoInteractions(descriptors, store);
    }

    @Test
    void requiresADelegateActorBeforeCreatingPendingDescriptor() {
        var journey = new EServiceDescriptorJourneyImpl(descriptors, lifecycle, store, requestFactory, session);
        EService owned = eService.toBuilder()
                .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId().toString()).build();
        when(session.getTenant()).thenReturn(Tenant.COMUNE_DI_MILANO);

        assertThatThrownBy(() -> journey.addDescriptor(owned, WAITING_FOR_APPROVAL))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("active producer delegation");

        verifyNoInteractions(descriptors, store, lifecycle);
    }

    @Test
    void allowsPendingDescriptorSetupUnderADelegateActor() {
        var journey = new EServiceDescriptorJourneyImpl(descriptors, lifecycle, store, requestFactory, session);
        EService owned = eService.toBuilder()
                .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId().toString()).build();
        when(session.getTenant()).thenReturn(Tenant.COMUNE_DI_POZZALLO);
        when(descriptors.addDescriptor(owned)).thenReturn(draft);
        when(lifecycle.bringToState(owned, draft, WAITING_FOR_APPROVAL, null))
                .thenReturn(draft.toBuilder().state(WAITING_FOR_APPROVAL).build());

        journey.addDescriptor(owned, WAITING_FOR_APPROVAL);

        verify(lifecycle).validateCanReachState(DRAFT, WAITING_FOR_APPROVAL);
        verify(lifecycle).bringToState(owned, draft, WAITING_FOR_APPROVAL, null);
        verify(store).upsert(owned);
    }

    @Test
    void rejectsDirectServiceCreationIntoApprovalBeforeCreatingTheService() {
        var journey = new EServiceJourneyImpl(services, descriptors, lifecycle, store,
                mock(EServiceRiskAnalysisUseCase.class), session, mock(EServiceRequestFactory.class));

        assertThatThrownBy(() -> journey.createEService(WAITING_FOR_APPROVAL))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("active producer delegation");

        verifyNoInteractions(services, lifecycle);
    }
}

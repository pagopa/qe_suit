package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EServiceDescriptorSelectionTest {
    @Test
    void resolvesVersionWithoutCreatingItAndSelectsProducerBeforeLifecycle() {
        EntityStore store = mock(EntityStore.class);
        EService eService = EService.builder().id(UUID.randomUUID()).descriptors(new ArrayList<>()).build();
        when(store.getByAliasOrThrow("ES-2", EService.class)).thenReturn(eService);
        var parameterTypes = new EServiceDescriptorParameterTypes(store);

        var selection = parameterTypes.eServiceDescriptorByVersionAndAlias("1", "ES-2");

        assertThat(selection.getVersion()).isEqualTo(1);
        verify(store).getByAliasOrThrow("ES-2", EService.class);
        verifyNoMoreInteractions(store);
        InteropJourney journey = mock(InteropJourney.class, RETURNS_SELF);
        new EServiceLifecycleJourneySteps(journey).bringEServiceDescriptorToState(
                selection, Tenant.COMUNE_DI_MILANO, EServiceDescriptorState.SUSPENDED
        );
        var order = inOrder(journey);
        order.verify(journey).withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
        order.verify(journey).bringEServiceDescriptorToState(eService, 1, EServiceDescriptorState.SUSPENDED);
        order.verifyNoMoreInteractions();
    }
}

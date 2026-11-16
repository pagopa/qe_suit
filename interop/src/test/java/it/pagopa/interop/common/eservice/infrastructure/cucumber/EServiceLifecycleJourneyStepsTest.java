package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.SelectedEServiceDescriptor;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.WAITING_FOR_APPROVAL;
import static org.mockito.Mockito.*;

class EServiceLifecycleJourneyStepsTest {
    @Test
    void pendingVersionSetupUsesTheExplicitDelegateRatherThanTheServiceOwner() {
        InteropJourney journey = mock(InteropJourney.class, RETURNS_SELF);
        EService service = EService.builder().id(UUID.randomUUID())
                .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId().toString()).build();
        var steps = new EServiceLifecycleJourneySteps(journey);

        steps.bringEServiceDescriptorToState(
                new SelectedEServiceDescriptor(service, 2),
                Tenant.COMUNE_DI_POZZALLO, WAITING_FOR_APPROVAL
        );

        var order = inOrder(journey);
        order.verify(journey).withProducer(Tenant.COMUNE_DI_POZZALLO, UserRole.ADMIN);
        order.verify(journey).bringEServiceDescriptorToState(service, 2, WAITING_FOR_APPROVAL);
        order.verifyNoMoreInteractions();
    }
}

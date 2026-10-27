package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.interop.common.eservice.application.EServiceArchivingUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class EServiceArchivingStepsTest {
    @Test
    void selectsTheExplicitProducerAndDelegatesWholeServiceArchiving() {
        var archiving = mock(EServiceArchivingUseCase.class);
        var session = mock(CurrentUserSession.class);
        var service = EService.builder().id(UUID.randomUUID()).build();
        var steps = new EServiceArchivingSteps(archiving, session);

        steps.archiveEntireEService(service, Tenant.COMUNE_DI_MILANO);

        var order = inOrder(session, archiving);
        order.verify(session).set(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO);
        order.verify(archiving).archiveEntireEService(service);
        order.verifyNoMoreInteractions();
    }
}

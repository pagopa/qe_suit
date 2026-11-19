package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class EServiceArchivedStepsTest {
    private static final UUID ARCHIVED_ESERVICE_ID = UUID.fromString("45d822b9-066c-4a77-b4a1-331135496907");

    private final EServiceUseCase eServiceUseCase = mock(EServiceUseCase.class);
    private final EntityStore entityStore = mock(EntityStore.class);
    private final AuthSession session = mock(AuthSession.class);
    private final EServiceArchivedSteps steps = new EServiceArchivedSteps(eServiceUseCase, entityStore, session);

    @Test
    void loadsPreconfiguredArchivedEServiceAsProducerAndRegistersAlias() {
        var archived = EService.builder().id(ARCHIVED_ESERVICE_ID).build();
        when(eServiceUseCase.getEService(EServiceRef.of(ARCHIVED_ESERVICE_ID))).thenReturn(archived);

        steps.eServiceEntirelyArchived("ES-1", Tenant.COMUNE_DI_MILANO);

        var order = inOrder(session, eServiceUseCase, entityStore);
        order.verify(session).set(Tenant.COMUNE_DI_MILANO);
        order.verify(eServiceUseCase).getEService(EServiceRef.of(ARCHIVED_ESERVICE_ID));
        order.verify(entityStore).upsert("ES-1", archived);
        verifyNoMoreInteractions(session, eServiceUseCase, entityStore);
    }
}

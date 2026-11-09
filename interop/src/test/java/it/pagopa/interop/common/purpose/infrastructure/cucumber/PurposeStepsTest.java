package it.pagopa.interop.common.purpose.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.m2m.kernel.context.CurrentM2MSession;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PurposeStepsTest {
    private final EntityStore entities = mock(EntityStore.class);
    private final PurposeUseCase useCase = mock(PurposeUseCase.class);
    private final CurrentUserSession userSession = mock(CurrentUserSession.class);
    private final CurrentM2MSession m2mSession = mock(CurrentM2MSession.class);
    private final PurposeSteps steps = new PurposeSteps(entities, useCase, userSession, m2mSession);
    private final PurposeVersion waiting = PurposeVersion.builder()
            .id(UUID.randomUUID()).createdAt(Instant.EPOCH)
            .purposeVersionState(PurposeVersionState.WAITING_FOR_APPROVAL).build();
    private final Purpose purpose = Purpose.builder().id(UUID.randomUUID()).version(waiting).build();

    @Test
    void approvesWaitingVersionAsRequestedTenant() {
        when(entities.getLastOrThrow(Purpose.class)).thenReturn(purpose);
        when(useCase.activatePurpose(purpose, waiting, null)).thenReturn(result(PurposeVersionState.ACTIVE));

        steps.approvePurpose(Tenant.COMUNE_DI_MILANO, PurposeVersionState.ACTIVE);

        verify(userSession).set(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO);
        verify(m2mSession).set(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN);
        verify(useCase).activatePurpose(purpose, waiting, null);
    }

    @Test
    void rejectsWaitingVersionWithAgreedReason() {
        when(entities.getLastOrThrow(Purpose.class)).thenReturn(purpose);
        when(useCase.rejectPurpose(purpose, waiting, "Rifiutata nell'ambito del test"))
                .thenReturn(result(PurposeVersionState.REJECTED));

        steps.rejectPurpose(Tenant.COMUNE_DI_MILANO, PurposeVersionState.REJECTED);

        verify(userSession).set(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO);
        verify(m2mSession).set(Tenant.COMUNE_DI_MILANO, M2MRole.M2M_ADMIN);
        verify(useCase).rejectPurpose(purpose, waiting, "Rifiutata nell'ambito del test");
    }

    @Test
    void failsBeforeOperationWhenNoWaitingVersionExists() {
        when(entities.getLastOrThrow(Purpose.class)).thenReturn(result(PurposeVersionState.ACTIVE));

        assertThatThrownBy(() -> steps.rejectPurpose(Tenant.COMUNE_DI_MILANO, PurposeVersionState.REJECTED))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("waiting for approval");
        verifyNoInteractions(useCase);
    }

    @Test
    void checksRequestedStateOfTheProcessedVersion() {
        when(entities.getLastOrThrow(Purpose.class)).thenReturn(purpose);
        when(useCase.rejectPurpose(purpose, waiting, "Rifiutata nell'ambito del test"))
                .thenReturn(result(PurposeVersionState.ACTIVE));

        assertThatThrownBy(() -> steps.rejectPurpose(Tenant.COMUNE_DI_MILANO, PurposeVersionState.REJECTED))
                .isInstanceOf(AssertionError.class);
    }

    private Purpose result(PurposeVersionState state) {
        return purpose.toBuilder().clearVersions()
                .version(waiting.toBuilder().purposeVersionState(state).build()).build();
    }
}

package it.pagopa.interop.bff.infrastructure.cucumber.step;

import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class BffUserStepsTest {
    @Test
    void setsTheUserRoleOnTheSelectedSession() {
        var session = mock(AuthSession.class);
        new BffUserSteps(session).setUser(UserRole.ADMIN, Tenant.COMUNE_DI_MILANO);
        verify(session).set(UserRole.ADMIN, Tenant.COMUNE_DI_MILANO);
        verifyNoMoreInteractions(session);
    }
}

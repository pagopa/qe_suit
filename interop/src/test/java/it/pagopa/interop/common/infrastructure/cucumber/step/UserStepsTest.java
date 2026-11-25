package it.pagopa.interop.common.infrastructure.cucumber.step;

import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UserStepsTest {

    @Test
    void setsTenantAdminOnTheChannelRoutedAuthSession() {
        AuthSession authSession = mock(AuthSession.class);

        new UserSteps(authSession).setAdmin(Tenant.COMUNE_DI_MILANO);

        verify(authSession).set(Tenant.COMUNE_DI_MILANO);
        verifyNoMoreInteractions(authSession);
    }
}

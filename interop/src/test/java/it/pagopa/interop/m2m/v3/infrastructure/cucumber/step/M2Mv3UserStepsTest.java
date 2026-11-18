package it.pagopa.interop.m2m.v3.infrastructure.cucumber.step;

import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class M2Mv3UserStepsTest {
    @Test
    void setsTheMachineRoleOnTheSelectedSession() {
        var session = mock(AuthSession.class);
        new M2Mv3UserSteps(session).setMachine(M2MRole.M2M_ADMIN, Tenant.COMUNE_DI_MILANO);
        verify(session).set(M2MRole.M2M_ADMIN, Tenant.COMUNE_DI_MILANO);
        verifyNoMoreInteractions(session);
    }

    @Test
    void propagatesProvisioningFailures() {
        var session = mock(AuthSession.class);
        var failure = new IllegalStateException("client creation failed");
        doThrow(failure).when(session).set(M2MRole.M2M_ADMIN, Tenant.COMUNE_DI_MILANO);
        assertThatThrownBy(() -> new M2Mv3UserSteps(session)
                .setMachine(M2MRole.M2M_ADMIN, Tenant.COMUNE_DI_MILANO))
                .isSameAs(failure);
    }
}

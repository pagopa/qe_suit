package it.pagopa.interop.common.infrastructure.cucumber.step;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.m2m.kernel.domain.M2MRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserStepsTest {
    @Test
    @SuppressWarnings("unchecked")
    void restoresChannelAndPreservesBothFailures() {
        var session = mock(AuthSession.class);
        CurrentChannel<Channel> channel = mock(CurrentChannel.class);
        var creationFailure = new IllegalStateException("creation failed");
        var restorationFailure = new IllegalStateException("restoration failed");
        when(channel.getCurrentChannel()).thenReturn(Channel.BFF);
        doThrow(creationFailure).when(session).set(M2MRole.M2M_ADMIN, Tenant.COMUNE_DI_MILANO);
        doThrow(restorationFailure).when(channel).setCurrentChannel(Channel.BFF);

        assertThatThrownBy(() -> new UserSteps(session, channel)
                .setUser(M2MRole.M2M_ADMIN, Tenant.COMUNE_DI_MILANO))
                .isSameAs(creationFailure)
                .satisfies(failure -> assertThat(failure.getSuppressed()).containsExactly(restorationFailure));
        var order = inOrder(channel, session);
        order.verify(channel).setCurrentChannel(Channel.M2M_V3);
        order.verify(session).set(M2MRole.M2M_ADMIN, Tenant.COMUNE_DI_MILANO);
        order.verify(channel).setCurrentChannel(Channel.BFF);
    }
}

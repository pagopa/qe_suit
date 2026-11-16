package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.tenant.application.TenantGateway;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantDelegationSettingsJourneyTest {
    @ParameterizedTest
    @CsvSource({
            "COMUNE_DI_POZZALLO,false", "COMUNE_DI_POZZALLO,true",
            "SOGECAP,false", "SOGECAP,true"
    })
    void usesRecipientAdminAndBffAndRestoresCallerOnSuccessOrFailure(Tenant recipient, boolean fail) {
        var session = mock(CurrentUserSession.class);
        @SuppressWarnings("unchecked")
        CurrentChannel<Channel> channel = mock(CurrentChannel.class);
        var gateway = mock(TenantGateway.class);
        var useCase = new TenantUseCase(gateway);
        var journey = new TenantJourneyImpl(useCase, mock(EntityStore.class), session, channel);
        var originalTenant = Tenant.COMUNE_DI_MILANO;
        var originalUser = User.getTenantAdmin(originalTenant);
        when(session.getTenant()).thenReturn(originalTenant);
        when(session.getUser()).thenReturn(originalUser);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);
        var ref = TenantRef.of(recipient.getOrganizationId());
        var failure = new IllegalStateException("configuration failed");
        if (fail) {
            doThrow(failure).when(gateway).ensureProducerDelegationEnabled(ref);
            assertThatThrownBy(() -> journey.ensureProducerDelegationEnabled(recipient)).isSameAs(failure);
        } else {
            assertThat(journey.ensureProducerDelegationEnabled(recipient)).isSameAs(journey);
        }

        var order = inOrder(channel, session, gateway);
        order.verify(channel).setCurrentChannel(Channel.BFF);
        order.verify(session).set(User.getTenantAdmin(recipient), recipient);
        order.verify(gateway).ensureProducerDelegationEnabled(ref);
        order.verify(session).set(originalUser, originalTenant);
        order.verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
    }
}

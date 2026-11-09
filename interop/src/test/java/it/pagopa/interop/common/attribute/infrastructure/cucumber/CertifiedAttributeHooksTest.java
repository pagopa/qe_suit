package it.pagopa.interop.common.attribute.infrastructure.cucumber;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.attribute.domain.AttributeKind;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.tenant.application.TenantUseCase;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class CertifiedAttributeHooksTest {
    @Test
    void revokesOnlyRecordedAssignmentsAsTheAttributeIssuerAndRestoresSession() {
        var fixtures = new CertifiedAttributeFixtures();
        var attribute = Attribute.builder().id(UUID.randomUUID()).kind(AttributeKind.CERTIFIED).build();
        TenantRef issuer = TenantRef.of(Tenant.COMUNE_DI_MILANO.getOrganizationId());
        TenantRef consumer = TenantRef.of(Tenant.PAGO_PA.getOrganizationId());
        fixtures.registerCreated(attribute, issuer);
        fixtures.registerAssignment(issuer, consumer, attribute.getRef());
        var useCase = mock(TenantUseCase.class);
        var session = mock(CurrentUserSession.class);
        var channel = mock(CurrentChannel.class);
        User originalUser = User.getTenantAdmin(Tenant.PAGO_PA);
        when(session.getUser()).thenReturn(originalUser);
        when(session.getTenant()).thenReturn(Tenant.PAGO_PA);
        when(channel.getCurrentChannel()).thenReturn(Channel.WEB_BROWSER);

        new CertifiedAttributeHooks(fixtures, useCase, session, channel).revokeScenarioAssignments();

        var order = inOrder(channel, session, useCase);
        order.verify(channel).setCurrentChannel(Channel.BFF);
        order.verify(session).set(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO);
        order.verify(useCase).revokeCertifiedAttribute(consumer, attribute.getRef());
        order.verify(session).set(originalUser, Tenant.PAGO_PA);
        order.verify(channel).setCurrentChannel(Channel.WEB_BROWSER);
        order.verifyNoMoreInteractions();
    }
}

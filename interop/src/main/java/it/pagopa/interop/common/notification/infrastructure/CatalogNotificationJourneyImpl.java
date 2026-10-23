package it.pagopa.interop.common.notification.infrastructure;

import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.notification.application.CatalogNotificationGateway;
import it.pagopa.interop.common.notification.application.CatalogNotificationJourney;
import it.pagopa.interop.common.eservice.domain.EService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CatalogNotificationJourneyImpl implements CatalogNotificationJourney {
    private final CatalogNotificationGateway gateway;
    private final CurrentUserSession session;
    private final CurrentChannel<Channel> channel;
    @Override
    public void enableFor(Tenant consumer) {
        asConsumer(consumer, () -> gateway.ensureEnabled(TenantRef.of(consumer.getOrganizationId())));
    }
    @Override
    public void verifyUnreadFor(Tenant consumer, EService eService) {
        asConsumer(consumer, () -> gateway.awaitUnreadNotification(
                TenantRef.of(consumer.getOrganizationId()), eService.getRef()));
    }
    private void asConsumer(Tenant consumer, Runnable operation) {
        var originalUser = session.getUser();
        var originalTenant = session.getTenant();
        var originalChannel = channel.getCurrentChannel();
        try {
            channel.setCurrentChannel(Channel.BFF);
            session.set(User.getTenantAdmin(consumer), consumer);
            operation.run();
        } finally {
            try {
                session.set(originalUser, originalTenant);
            } finally {
                channel.setCurrentChannel(originalChannel);
            }
        }
    }
}

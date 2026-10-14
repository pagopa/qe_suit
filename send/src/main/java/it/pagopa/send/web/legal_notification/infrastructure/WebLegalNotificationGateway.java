package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.send.bff.delivery.infrastructure.BffDeliveryRestClient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.web.notification_creation.infrastructure.cucumber.NotificationContext;
import it.pagopa.send.common.legal_notification.application.LegalNotificationGateway;
import it.pagopa.send.bff.legal_notification.infrastructure.LegalNotificationRestClient;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.web.notification_details.infrastructure.NotificationDetailsProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebLegalNotificationGateway implements LegalNotificationGateway {

    private static final Duration DEFAULT_STATUS_TIMEOUT = Duration.ofMinutes(6);
    private static final Duration DEFAULT_STATUS_INTERVAL = Duration.ofSeconds(10);

    private final NotificationDetailsProxy notificationDetailsProxy;

    private final LegalNotificationRestClient restClient;
    private final BffDeliveryRestClient deliveryRestClient;
    private final NotificationContext notificationContext;
    private final EntityStore entityStore;

    @Override
    public void prepareNotification(Map<String, String> data) {
        throw new UnsupportedOperationException("prepareNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public void addRecipient(RecipientSpec recipient) {
        throw new UnsupportedOperationException("addRecipient non è stato ancora implementato per il canale Web");
    }

    @Override
    public void sendNotification(Tenant sender, NotificationStatus targetStatus) {
        throw new UnsupportedOperationException("sendNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public void waitForNotificationStatus(NotificationStatus targetStatus) {
        throw new UnsupportedOperationException("waitForNotificationStatus non è stato ancora implementato per il canale Web");
    }

    @Override
    public void deleteNotification(String iun) {
        throw new UnsupportedOperationException("deleteNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public LegalNotificationDomain readNotification(String iun) {
        throw new UnsupportedOperationException("readNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public List<LegalNotificationDomain> searchNotification(Map<String, String> overrides) {
        throw new UnsupportedOperationException("searchNotification non è stato ancora implementato per il canale Web");
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}

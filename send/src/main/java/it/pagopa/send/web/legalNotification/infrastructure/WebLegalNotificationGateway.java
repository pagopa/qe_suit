package it.pagopa.send.web.legalNotification.infrastructure;

import it.pagopa.send.b2b.delivery.infrastructure.B2BDeliveryRestClient;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.controller.creazione_notifica.NotificationContext;
import it.pagopa.send.generated.openapi.clients.bff.model.BffFullNotificationV1;
import it.pagopa.send.generated.openapi.clients.bff.model.BffLegalNotificationsResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.infrastructure.template.ApiResponse;
import it.pagopa.send.infrastructure.template.PollingStrategy;
import it.pagopa.send.infrastructure.template.PollingUtils;
import it.pagopa.send.legalnotification.application.LegalNotificationGateway;
import it.pagopa.send.legalnotification.infrastructure.LegalNotificationRestClient;
import it.pagopa.send.web.notification_details.infrastructure.NotificationDetailsProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebLegalNotificationGateway implements LegalNotificationGateway {

    private static final Duration DEFAULT_STATUS_TIMEOUT = Duration.ofMinutes(6);
    private static final Duration DEFAULT_STATUS_INTERVAL = Duration.ofSeconds(10);

    private final NotificationDetailsProxy notificationDetailsProxy;

    private final LegalNotificationRestClient restClient;
    private final B2BDeliveryRestClient deliveryRestClient;
    private final NotificationContext notificationContext;

    @Override
    public void sendNotification(BffNewNotificationRequest request, BffNotificationStatus targetStatus) {
        // TODO: implement the logic to send notification for web channel, similar to B2BLegalNotificationGateway
    }

    @Override
    public void deleteNotification(String iun) {
        // TODO: implement the logic to delete notification for web channel, similar to B2BLegalNotificationGateway
    }

    @Override
    public BffFullNotificationV1 readNotification(String iun) {
        notificationDetailsProxy.searchNotification(Map.of("iun", iun));
        notificationDetailsProxy.goToNotificationDetails();
        return null;
    }

    @Override
    public BffLegalNotificationsResponse searchNotification(Map<String, String> overrides) {
        return restClient.search(overrides)
                .withoutPolling()
                .get();
    }


    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}

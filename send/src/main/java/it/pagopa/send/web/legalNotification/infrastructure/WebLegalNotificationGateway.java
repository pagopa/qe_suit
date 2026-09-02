package it.pagopa.send.web.legalNotification.infrastructure;

import it.pagopa.send.b2b.delivery.infrastructure.B2BDeliveryRestClient;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.notification.domain.NotificationStatus;
import it.pagopa.send.controller.creazione_notifica.NotificationContext;
import it.pagopa.send.legalnotification.application.LegalNotificationGateway;
import it.pagopa.send.legalnotification.infrastructure.LegalNotificationRestClient;
import it.pagopa.send.model.RecipientSpec;
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
    public void prepareNotification(Map<String, String> data) {
        // TODO: implement the logic to prepare notification for web channel, similar to B2BLegalNotificationGateway
    }

    @Override
    public void addRecipient(RecipientSpec recipient) {
        // TODO: implement the logic to add a recipient for web channel, similar to B2BLegalNotificationGateway
    }

    @Override
    public void sendNotification(Tenant sender, NotificationStatus targetStatus) {
        // TODO: implement the logic to send notification for web channel, similar to B2BLegalNotificationGateway
    }

    @Override
    public void deleteNotification(String iun) {
        // TODO: implement the logic to delete notification for web channel, similar to B2BLegalNotificationGateway
    }

    @Override
    public LegalNotificationDomain readNotification(String iun) {
        notificationDetailsProxy.searchNotification(Map.of("iun", iun));
        notificationDetailsProxy.goToNotificationDetails();
        LegalNotificationDomain.builder()
//                .status()
                .build();
        return LegalNotificationDomain.builder().build();
    }

    @Override
    public LegalNotificationDomain searchNotification(Map<String, String> overrides) {
//        restClient.search(overrides)
//                .withoutPolling()
//                .get();
        return LegalNotificationDomain.builder().build();
    }


    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}

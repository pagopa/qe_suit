package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.DeleteNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/** Annulla la notifica dal suo dettaglio nel portale della PA e attende che risulti annullata, come il canale BFF. */
@Service
@RequiredArgsConstructor
public class WebDeleteNotificationGateway implements DeleteNotificationGateway {

    private static final Duration STATUS_TIMEOUT = Duration.ofMinutes(10);
    private static final Duration STATUS_INTERVAL = Duration.ofSeconds(15);

    private final MittenteNotificationDetailsPage notificationDetailsPage;
    private final WebReadNotificationGateway readNotificationGateway;

    @Override
    public void deleteNotification(String iun) {
        notificationDetailsPage.navigateTo(iun);
        notificationDetailsPage.assertLoaded();
        notificationDetailsPage.cancelNotificationButton().click();
        notificationDetailsPage.confirmCancelNotificationButton().click();
        PollingUtils.pollUntil(
                () -> readNotificationGateway.readNotification(iun).getStatus(),
                status -> status == NotificationStatus.CANCELLED,
                STATUS_TIMEOUT,
                STATUS_INTERVAL,
                String.format("La notifica %s non risulta annullata", iun));
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}

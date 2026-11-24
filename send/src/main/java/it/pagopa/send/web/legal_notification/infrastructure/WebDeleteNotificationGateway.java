package it.pagopa.send.web.legal_notification.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
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

    private final WebPresentationGateway browser;
    private final WebReadNotificationGateway readNotificationGateway;

    @Override
    public void deleteNotification(String iun) {
        MittenteNotificationDetailsPage details = browser.bind(MittenteNotificationDetailsPage.class);
        details.navigateTo(iun);
        details.assertLoaded();
        details.cancelNotificationButton().click();
        details.confirmCancelNotificationButton().click();
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

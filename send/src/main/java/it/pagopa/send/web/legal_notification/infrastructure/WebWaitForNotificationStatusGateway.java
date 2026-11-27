package it.pagopa.send.web.legal_notification.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.WaitForNotificationStatusGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Attende lo stato dell'ultima notifica creata, letto dal dettaglio della notifica nel portale della PA.
 * ACCEPTED è raggiunto anche se la notifica è già oltre: dal portale la si legge solo quando compare tra le
 * notifiche della PA, e negli ambienti di test può essere già perfezionata.
 */
@Service
@RequiredArgsConstructor
public class WebWaitForNotificationStatusGateway implements WaitForNotificationStatusGateway {

    private static final Duration STATUS_TIMEOUT = Duration.ofMinutes(10);
    private static final Duration STATUS_INTERVAL = Duration.ofSeconds(15);

    private final EntityStore entityStore;
    private final WebReadNotificationGateway readNotificationGateway;

    @Override
    public void waitForNotificationStatus(NotificationStatus targetStatus) {
        String iun = entityStore.getLastOrThrow(LegalNotificationDomain.class).getIun();
        PollingUtils.pollUntil(
                () -> readNotificationGateway.readNotification(iun).getStatus(),
                status -> status == targetStatus || (targetStatus == NotificationStatus.ACCEPTED && status.isAccepted()),
                STATUS_TIMEOUT,
                STATUS_INTERVAL,
                String.format("La notifica %s non ha raggiunto lo stato %s", iun, targetStatus));
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.WEB_BROWSER;
    }
}

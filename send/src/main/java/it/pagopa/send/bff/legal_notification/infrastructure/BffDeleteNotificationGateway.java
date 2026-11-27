package it.pagopa.send.bff.legal_notification.infrastructure;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.DeleteNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/** Annulla la notifica dal BFF e attende che risulti annullata. */
@Service
@RequiredArgsConstructor
public class BffDeleteNotificationGateway implements DeleteNotificationGateway {

    private static final Duration STATUS_TIMEOUT = Duration.ofMinutes(10);
    private static final Duration STATUS_INTERVAL = Duration.ofSeconds(10);

    private final LegalNotificationRestClient restClient;
    private final BffReadNotificationGateway readNotificationGateway;

    @Override
    public void deleteNotification(String iun) {
        restClient.delete(iun)
                .withoutPolling()
                .get();
        PollingUtils.pollUntil(
                () -> readNotificationGateway.readNotification(iun),
                notification -> notification.getStatus().equals(NotificationStatus.CANCELLED),
                STATUS_TIMEOUT,
                STATUS_INTERVAL
        );
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}

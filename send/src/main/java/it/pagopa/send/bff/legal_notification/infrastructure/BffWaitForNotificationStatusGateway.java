package it.pagopa.send.bff.legal_notification.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.WaitForNotificationStatusGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/** Attende lo stato dell'ultima notifica creata, letto dal BFF. */
@Slf4j
@Service
@RequiredArgsConstructor
public class BffWaitForNotificationStatusGateway implements WaitForNotificationStatusGateway {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(20);
    private static final Duration DEFAULT_INTERVAL = Duration.ofSeconds(10);

    private final EntityStore entityStore;
    private final BffLegalNotificationMapper mapper;
    private final BffReadNotificationGateway readNotificationGateway;

    @Override
    public void waitForNotificationStatus(NotificationStatus targetStatus) {
        waitForNotificationStatus(targetStatus, DEFAULT_TIMEOUT, DEFAULT_INTERVAL);
    }

    // usata anche dall'invio, con l'attesa breve dello stato subito dopo l'accettazione
    void waitForNotificationStatus(NotificationStatus targetStatus, Duration timeout, Duration interval) {
        String iun = entityStore.getLastOrThrow(LegalNotificationDomain.class).getIun();
        BffNotificationStatus bffTargetStatus = mapper.toBffStatus(targetStatus);
        PollingUtils.pollUntil(
                () -> readNotificationGateway.readNotification(iun).getStatus(),
                status -> mapper.toBffStatus(status).equals(bffTargetStatus),
                timeout,
                interval,
                String.format("Default status timeout exceeded while waiting for notification with IUN %s to reach target status %s", iun, targetStatus)
        );
        log.info("Notification with IUN {} reached target status {}", iun, targetStatus);
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}

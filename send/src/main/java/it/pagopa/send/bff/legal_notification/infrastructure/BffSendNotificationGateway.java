package it.pagopa.send.bff.legal_notification.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.send.bff.delivery.infrastructure.BffDeliveryRestClient;
import it.pagopa.send.common.infrastructure.IUNHelper;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.application.SendNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationRequestOutcome;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationRequestFactory;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.generated.openapi.clients.bff.model.NewNotificationResponse;
import it.pagopa.send.generated.openapi.clients.delivery.model.NewNotificationRequestStatusResponseV26;
import it.pagopa.send.generated.openapi.clients.delivery.model.NotificationRequestRefusedProblemError;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Invia la request preparata al BFF, attende l'esito della validazione della richiesta e, se accettata, lo stato
 * richiesto della notifica. Solo i gateway BFF (e {@link BffLegalNotificationMapper}) conoscono i DTO OpenAPI del BFF.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BffSendNotificationGateway implements SendNotificationGateway {

    private static final Duration REQUEST_STATUS_TIMEOUT = Duration.ofMinutes(10);
    private static final Duration REQUEST_STATUS_INTERVAL = Duration.ofSeconds(10);
    private static final String REQUEST_WAITING = "WAITING";
    // dopo l'accettazione lo stato della notifica si legge subito: attesa breve
    private static final Duration NOTIFICATION_STATUS_TIMEOUT = Duration.ofSeconds(20);
    private static final Duration NOTIFICATION_STATUS_INTERVAL = Duration.ofSeconds(1);

    private final LegalNotificationRequestFactory requestFactory;
    private final LegalNotificationCreationRequest request;
    private final BffLegalNotificationMapper mapper;
    private final LegalNotificationRestClient restClient;
    private final BffDeliveryRestClient deliveryRestClient;
    private final EntityStore entityStore;
    private final BffReadNotificationGateway readNotificationGateway;
    private final BffWaitForNotificationStatusGateway waitForNotificationStatusGateway;

    @Override
    public void sendNotification(User sender, NotificationStatus targetStatus) {
        requestFactory.applySender(request, sender);
        requestFactory.applyPaymentAttachments(request);

        NewNotificationResponse response = restClient.create(mapper.toBffRequest(request))
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
        String iun = IUNHelper.extractFromBffNewNotificationResponse(response);
        // la validazione della richiesta finisce con ACCEPTED o REFUSED: una richiesta rifiutata non diventa notifica
        LegalNotificationRequestOutcome outcome = toRequestOutcome(PollingUtils.pollUntil(
                () -> deliveryRestClient.retrieveNotificationRequestStatusV26(response.getNotificationRequestId())
                        .withoutPolling().get(),
                statusResponse -> !REQUEST_WAITING.equals(statusResponse.getNotificationRequestStatus()),
                REQUEST_STATUS_TIMEOUT,
                REQUEST_STATUS_INTERVAL
        ));
        entityStore.upsert(null, outcome);
        if (outcome.status() == NotificationStatus.REFUSED || targetStatus == NotificationStatus.REFUSED) {
            if (outcome.status() != targetStatus) {
                throw new IllegalStateException(String.format("Richiesta di notifica %s invece di %s, errori: %s",
                        outcome.status(), targetStatus, outcome.errorCodes()));
            }
            log.info("Notification request {} refused with errors {}", outcome.notificationRequestId(), outcome.errorCodes());
            return;
        }

        // la lettura registra la notifica come ultima creata, quella di cui si attende lo stato
        readNotificationGateway.readNotification(iun);
        waitForNotificationStatusGateway.waitForNotificationStatus(targetStatus, NOTIFICATION_STATUS_TIMEOUT,
                NOTIFICATION_STATUS_INTERVAL);
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }

    /**
     * Esito della validazione di una richiesta non più in attesa (ACCEPTED o REFUSED), con i codici degli errori:
     * l'elenco degli errori è facoltativo, una richiesta accettata può non averlo.
     */
    private static LegalNotificationRequestOutcome toRequestOutcome(NewNotificationRequestStatusResponseV26 response) {
        List<String> errorCodes = Optional.ofNullable(response.getErrors()).orElse(List.of()).stream()
                .map(NotificationRequestRefusedProblemError::getCode)
                .toList();
        return new LegalNotificationRequestOutcome(response.getNotificationRequestId(),
                NotificationStatus.valueOf(response.getNotificationRequestStatus()), errorCodes);
    }
}

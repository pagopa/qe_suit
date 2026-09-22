package it.pagopa.send.bff.legal_notification.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.send.bff.delivery.infrastructure.BffDeliveryRestClient;
import it.pagopa.send.common.user.domain.Tenant;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.legal_notification.domain.NotificationStatus;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.common.legal_notification.application.LegalNotificationGateway;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.infrastructure.IUNHelper;
import it.pagopa.send.common.legal_notification.infrastructure.factory.LegalNotificationRequestFactory;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

/**
 * Unica implementazione di {@link LegalNotificationGateway} per il canale BFF. La notifica in
 * preparazione per lo scenario corrente ({@link LegalNotificationCreationRequest}, bean
 * {@code @ScenarioScope}) viene popolata progressivamente da {@link #prepareNotification} e
 * {@link #addRecipient}; {@link #sendNotification} completa i campi noti solo al mittente, la
 * invia e salva la notifica creata in {@link EntityStore} (recuperabile via
 * {@code FinalizerJourney.get(LegalNotificationDomain.class)}), non in un context ad-hoc. Solo qui
 * (e nel {@link BffLegalNotificationMapper} co-locato) si fa riferimento al DTO OpenAPI del BFF:
 * l'interfaccia e i chiamanti conoscono solo il dominio interno.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BffLegalNotificationGateway implements LegalNotificationGateway {

    private static final Duration DEFAULT_STATUS_TIMEOUT = Duration.ofMinutes(10);
    private static final Duration DEFAULT_STATUS_INTERVAL = Duration.ofSeconds(10);

    private final LegalNotificationRestClient restClient;
    private final BffDeliveryRestClient deliveryRestClient;
    private final EntityStore entityStore;
    private final BffLegalNotificationMapper mapper;
    private final LegalNotificationRequestFactory requestFactory;
    private final LegalNotificationCreationRequest request;

    @Override
    public void prepareNotification(Map<String, String> data) {
        requestFactory.applyPreliminaryData(request, data);
    }

    @Override
    public void addRecipient(RecipientSpec recipient) {
        request.addRecipient(requestFactory.resolveRecipient(recipient));
    }

    @Override
    public void sendNotification(Tenant sender, NotificationStatus targetStatus) {
        requestFactory.applySender(request, sender);

        BffNewNotificationRequest bffRequest = mapper.toBffRequest(request);
        BffNotificationStatus bffTargetStatus = mapper.toBffStatus(targetStatus);

        BffNewNotificationResponse response = restClient.create(bffRequest)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
        String iun = IUNHelper.extractFromBffNewNotificationResponse(response);
//
        PollingUtils.pollUntil(
                () -> {
                    var responseRe = deliveryRestClient.retrieveNotificationRequestStatusV26(response.getNotificationRequestId()).withoutPolling().get();
                    System.out.println("Polling response: " + responseRe);
                    return responseRe;
                },
                statusResponse -> statusResponse.getNotificationRequestStatus().equals(bffTargetStatus.getValue()),
                DEFAULT_STATUS_TIMEOUT,
                DEFAULT_STATUS_INTERVAL
        );


        PollingUtils.pollUntil(
                () -> readNotification(iun).getStatus(),
                status -> mapper.toBffStatus(status).equals(bffTargetStatus),
                String.format("Default status timeout exceeded while waiting for notification with IUN %s to reach target status %s", iun, targetStatus)
        );
        log.info("Notification with IUN {} reached target status {}", iun, targetStatus);
    }

    @Override
    public void waitForNotificationStatus(NotificationStatus targetStatus) {
        String iun = entityStore.getLastOrThrow(LegalNotificationDomain.class).getIun();
        BffNotificationStatus bffTargetStatus = mapper.toBffStatus(targetStatus);

        PollingUtils.pollUntil(
                () -> readNotification(iun).getStatus(),
                status -> mapper.toBffStatus(status).equals(bffTargetStatus),
                Duration.ofMinutes(20),
                Duration.ofSeconds(10),
                String.format("Default status timeout exceeded while waiting for notification with IUN %s to reach target status %s", iun, targetStatus)
        );
        log.info("Notification with IUN {} reached target status {}", iun, targetStatus);
    }

    @Override
    public void deleteNotification(String iun) {
        restClient.delete(iun)
                .withoutPolling()
                .get();
        PollingUtils.pollUntil(
                () -> readNotification(iun),
                response -> response.getStatus().equals(NotificationStatus.CANCELLED),
                DEFAULT_STATUS_TIMEOUT,
                DEFAULT_STATUS_INTERVAL
        );
    }

    @Override
    public LegalNotificationDomain readNotification(String iun) {
        return restClient.read(iun)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(mapper::toDomain)
                .updateContext()
                .get();
    }

    @Override
    public LegalNotificationDomain searchNotification(Map<String, String> overrides) {
        // TODO: BffLegalNotificationsResponse è una lista di risultati sintetici (resultsPage),
        // non un singolo BffFullNotificationV1: da mappare quando questo metodo avrà un caso
        // d'uso reale (oggi non è esercitato da nessuno step).
        restClient.search(overrides)
                .withoutPolling()
                .get();
        return LegalNotificationDomain.builder().build();
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.BFF;
    }
}

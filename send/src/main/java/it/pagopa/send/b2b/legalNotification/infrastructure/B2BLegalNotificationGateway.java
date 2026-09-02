package it.pagopa.send.b2b.legalNotification.infrastructure;

import it.pagopa.send.b2b.delivery.infrastructure.B2BDeliveryRestClient;
import it.pagopa.send.common.domain.Tenant;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.common.notification.domain.LegalNotificationDomain;
import it.pagopa.send.common.notification.domain.NotificationStatus;
import it.pagopa.send.controller.creazione_notifica.NotificationContext;
import it.pagopa.send.generated.openapi.clients.bff.model.BffFullNotificationV1;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.legalnotification.application.LegalNotificationGateway;
import it.pagopa.send.legalnotification.infrastructure.LegalNotificationRestClient;
import it.pagopa.send.model.RecipientSpec;
import it.pagopa.send.utils.factory.LegalNotificationRequestFactory;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

/**
 * Unica implementazione di {@link LegalNotificationGateway} per il canale B2B. La notifica in
 * preparazione per lo scenario corrente ({@link LegalNotificationCreationRequest}, bean
 * {@code @ScenarioScope}) viene popolata progressivamente da {@link #prepareNotification} e
 * {@link #addRecipient}; {@link #sendNotification} completa i campi noti solo al mittente e la
 * invia. Solo qui (e nel {@link B2BLegalNotificationMapper} co-locato) si fa riferimento al DTO
 * OpenAPI del BFF: l'interfaccia e i chiamanti conoscono solo il dominio interno.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class B2BLegalNotificationGateway implements LegalNotificationGateway {

    private static final Duration DEFAULT_STATUS_TIMEOUT = Duration.ofMinutes(6);
    private static final Duration DEFAULT_STATUS_INTERVAL = Duration.ofSeconds(10);

    private final LegalNotificationRestClient restClient;
    private final B2BDeliveryRestClient deliveryRestClient;
    private final NotificationContext notificationContext;
    private final B2BLegalNotificationMapper mapper;
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

//        var creationCall = restClient.create(bffRequest).withoutPolling();
//        log.info("Risposta creazione notifica: {}", creationCall.getRaw().getRawContent());
//        BffNewNotificationResponse response = restClient.create(bffRequest).withoutPolling().get();
        BffNewNotificationResponse response = restClient.create(bffRequest).withoutPolling().get();
        notificationContext.setBffNewNotificationResponse(response);

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
        log.info("Notification with IUN {} reached target status {}", response.getNotificationRequestId(), targetStatus);
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
                .withoutPolling()
                .map(mapper::toDomain)
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
        return channel == Channel.B2B;
    }
}

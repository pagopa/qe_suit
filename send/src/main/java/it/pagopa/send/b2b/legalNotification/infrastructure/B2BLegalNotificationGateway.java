package it.pagopa.send.b2b.legalNotification.infrastructure;

import it.pagopa.send.b2b.delivery.infrastructure.B2BDeliveryRestClient;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.controller.creazione_notifica.NotificationContext;
import it.pagopa.send.generated.openapi.clients.bff.model.BffFullNotificationV1;
import it.pagopa.send.generated.openapi.clients.bff.model.BffLegalNotificationsResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationStatus;
import it.pagopa.send.generated.openapi.clients.bff.model.BffRequestStatus;
import it.pagopa.send.infrastructure.template.ApiResponse;
import it.pagopa.send.infrastructure.template.PollingStrategy;
import it.pagopa.send.infrastructure.template.PollingUtils;
import it.pagopa.send.legalnotification.application.LegalNotificationGateway;
import it.pagopa.send.legalnotification.infrastructure.LegalNotificationRestClient;
import it.pagopa.send.utils.IUNHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class B2BLegalNotificationGateway implements LegalNotificationGateway {

    private static final Duration DEFAULT_STATUS_TIMEOUT = Duration.ofMinutes(6);
    private static final Duration DEFAULT_STATUS_INTERVAL = Duration.ofSeconds(10);

    private final LegalNotificationRestClient restClient;
    private final B2BDeliveryRestClient deliveryRestClient;
    private final NotificationContext notificationContext;

    @Override
    public void sendNotification(BffNewNotificationRequest request, BffNotificationStatus targetStatus) {
        BffNewNotificationResponse response = restClient.create(request)
                .withoutPolling()
                .get();
        notificationContext.setBffNewNotificationResponse(response);

        PollingUtils.pollUntil(
                () -> {
                    var responseRe = deliveryRestClient.retrieveNotificationRequestStatusV26(response.getNotificationRequestId()).withoutPolling().get();
                    System.out.println("Polling response: " + responseRe);
                    return responseRe;

                },
                statusResponse -> statusResponse.getNotificationRequestStatus().equals(targetStatus.getValue()),
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
                response -> response.getNotificationStatus().equals(BffNotificationStatus.CANCELLED),
                DEFAULT_STATUS_TIMEOUT,
                DEFAULT_STATUS_INTERVAL
        );
    }

    @Override
    public BffFullNotificationV1 readNotification(String iun) {
        return restClient.read(iun)
                .withoutPolling()
                .get();
    }

    @Override
    public BffLegalNotificationsResponse searchNotification(Map<String, String> overrides) {
        return restClient.search(overrides)
                .withoutPolling()
                .get();
    }

    private PollingStrategy matchesStatus(BffNotificationStatus targetStatus) {
        return (ApiResponse response) -> response.is2xxSuccessful()
                && targetStatus.equals(response.as(BffFullNotificationV1.class).getNotificationStatus());
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.B2B;
    }
}

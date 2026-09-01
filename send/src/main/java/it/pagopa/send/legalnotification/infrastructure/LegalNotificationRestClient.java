package it.pagopa.send.legalnotification.infrastructure;

import it.pagopa.send.generated.openapi.clients.bff.api.NotificationSentApi;
import it.pagopa.send.generated.openapi.clients.bff.model.BffFullNotificationV1;
import it.pagopa.send.generated.openapi.clients.bff.model.BffLegalNotificationsResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationRequest;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNewNotificationResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffRequestStatus;
import it.pagopa.send.infrastructure.template.RestClient;
import it.pagopa.send.infrastructure.template.TestChain;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class LegalNotificationRestClient extends RestClient {

    private final NotificationSentApi notificationSentApi;

    public LegalNotificationRestClient(NotificationSentApi notificationSentApi) {
        this.notificationSentApi = notificationSentApi;
    }

    public TestChain<BffNewNotificationResponse> create(BffNewNotificationRequest payload) {
        return execute(
                () -> notificationSentApi.newSentNotificationV1().body(payload).execute(Function.identity()),
                BffNewNotificationResponse.class
        );
    }

    public TestChain<BffRequestStatus> delete(String iun) {
        return execute(
                () -> notificationSentApi.notificationCancellationV1().iunPath(iun).execute(Function.identity()),
                BffRequestStatus.class
        );
    }

    public TestChain<BffFullNotificationV1> read(String iun) {
        return execute(
                () -> notificationSentApi.getSentNotificationV1().iunPath(iun).execute(Function.identity()),
                BffFullNotificationV1.class
        );
    }

    public TestChain<BffLegalNotificationsResponse> search(Map<String, String> overrides) {
        return execute(
                () -> notificationSentApi.searchSentNotificationsV1()
                        .iunMatchQuery(overrides.get("iunMatch"))
                        .statusQuery(overrides.get("status"))
                        .recipientIdQuery(overrides.get("recipientId"))
                        .startDateQuery(overrides.get("startDate"))
                        .endDateQuery(overrides.get("endDate"))
                        .execute(Function.identity()),
                BffLegalNotificationsResponse.class
        );
    }
}

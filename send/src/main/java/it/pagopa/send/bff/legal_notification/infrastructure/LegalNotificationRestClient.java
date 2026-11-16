package it.pagopa.send.bff.legal_notification.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.send.generated.openapi.clients.bff.api.NotificationSentApi;
import it.pagopa.send.generated.openapi.clients.bff.model.BffFullNotificationV1;
import it.pagopa.send.generated.openapi.clients.bff.model.BffLegalNotificationsResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.BffNotificationTimelineResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.NewNotificationRequestV26;
import it.pagopa.send.generated.openapi.clients.bff.model.NewNotificationResponse;
import it.pagopa.send.generated.openapi.clients.bff.model.RequestStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class LegalNotificationRestClient extends RestClient {

    private final NotificationSentApi notificationSentApi;

    public LegalNotificationRestClient(TestChainFactory chainFactory, NotificationSentApi notificationSentApi) {
        super(chainFactory);
        this.notificationSentApi = notificationSentApi;
    }

    public TestChain<NewNotificationResponse> create(NewNotificationRequestV26 payload) {
        return execute(
                () -> notificationSentApi.newSentNotificationV1().body(payload).execute(Function.identity()),
                NewNotificationResponse.class
        );
    }

    public TestChain<RequestStatus> delete(String iun) {
        return execute(
                () -> notificationSentApi.notificationCancellationV1().iunPath(iun).execute(Function.identity()),
                RequestStatus.class
        );
    }

    public TestChain<BffFullNotificationV1> read(String iun) {
        return execute(
                () -> notificationSentApi.getSentNotificationV1().iunPath(iun).execute(Function.identity()),
                BffFullNotificationV1.class
        );
    }

    public TestChain<BffNotificationTimelineResponse> readTimeline(String iun) {
        return execute(
                () -> notificationSentApi.getSentNotificationTimelineV1().iunPath(iun).execute(Function.identity()),
                BffNotificationTimelineResponse.class
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

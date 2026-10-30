package it.pagopa.send.bff.delivery.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.api.SenderInformalNotificationsApi;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffFullSentInformalNotificationTimelineV1;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffFullSentInformalNotificationV1;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BffComboRestClient extends RestClient {

    private final SenderInformalNotificationsApi senderInformalNotificationsApi;

    public BffComboRestClient(TestChainFactory testChainFactory, SenderInformalNotificationsApi senderInformalNotificationsApi) {
        super(testChainFactory);
        this.senderInformalNotificationsApi = senderInformalNotificationsApi;
    }

    public TestChain<BffFullSentInformalNotificationV1> getComboDetails(String campaignId, String iun) {
        return execute(
                () -> senderInformalNotificationsApi
                        .getSentInformalNotificationV1()
                        .iunPath(iun)
                        .execute(Function.identity()),
                BffFullSentInformalNotificationV1.class
        );
    }

    public TestChain<BffFullSentInformalNotificationTimelineV1> getComboTimeline(String campaignId, String iun) {
        return execute(
                () -> senderInformalNotificationsApi
                        .getSentInformalNotificationTimelineV1()
                        .iunPath(iun)
                        .execute(Function.identity()),
                BffFullSentInformalNotificationTimelineV1.class
        );
    }
}

package it.pagopa.send.bff.informal_notification.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.send.generated.openapi.clients.informal.api.NewInformalNotificationApi;
import it.pagopa.send.generated.openapi.clients.informal.api.SenderReadInformalNotificationB2BApi;
import it.pagopa.send.generated.openapi.clients.informal.model.FullSentInformalNotificationV1;
import it.pagopa.send.generated.openapi.clients.informal.model.InformalNotificationRequestV1;
import it.pagopa.send.generated.openapi.clients.informal.model.NewNotificationResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class InformalNotificationRestClient extends RestClient {
    private final NewInformalNotificationApi newInformalNotificationApi;
    private final SenderReadInformalNotificationB2BApi senderReadInformalNotificationB2BApi;

    public InformalNotificationRestClient(
            TestChainFactory chainFactory,
            NewInformalNotificationApi newInformalNotificationApi,
            SenderReadInformalNotificationB2BApi senderReadInformalNotificationB2BApi
    ) {
        super(chainFactory);
        this.newInformalNotificationApi = newInformalNotificationApi;
        this.senderReadInformalNotificationB2BApi = senderReadInformalNotificationB2BApi;
    }

    public TestChain<NewNotificationResponse> createInformalRequest(InformalNotificationRequestV1 body) {
        return execute(
                () -> newInformalNotificationApi
                        .sendNewInformalNotificationV1()
                        .body(body)
                        .execute(Function.identity()),
                NewNotificationResponse.class
        );
    }

    public TestChain<FullSentInformalNotificationV1> getInformalRequest(String iun) {
        return execute(
                () -> senderReadInformalNotificationB2BApi
                        .getSentInformalNotificationV1()
                        .iunPath(iun)
                        .execute(Function.identity()),
                FullSentInformalNotificationV1.class
        );
    }
}

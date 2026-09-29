package it.pagopa.send.bff.delivery.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.send.generated.openapi.clients.delivery.api.SenderReadB2BApi;
import it.pagopa.send.generated.openapi.clients.delivery.model.NewNotificationRequestStatusResponseV26;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BffDeliveryRestClient extends RestClient {
    private final SenderReadB2BApi senderReadB2BApi;

    public BffDeliveryRestClient(TestChainFactory testChainFactory, SenderReadB2BApi senderReadB2BApi) {
        super(testChainFactory);
        this.senderReadB2BApi = senderReadB2BApi;
    }

    public TestChain<NewNotificationRequestStatusResponseV26> retrieveNotificationRequestStatusV26(String notificationRequestId) {
        return execute(
                () -> senderReadB2BApi.retrieveNotificationRequestStatusV26().notificationRequestIdQuery(notificationRequestId).execute(Function.identity()),
                NewNotificationRequestStatusResponseV26.class
        );
    }
}

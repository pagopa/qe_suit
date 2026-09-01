package it.pagopa.send.b2b.delivery.infrastructure;

import it.pagopa.send.generated.openapi.clients.delivery.api.SenderReadB2BApi;
import it.pagopa.send.generated.openapi.clients.delivery.model.NewNotificationRequestStatusResponse;
import it.pagopa.send.generated.openapi.clients.delivery.model.NewNotificationRequestStatusResponseV26;
import it.pagopa.send.infrastructure.template.RestClient;
import it.pagopa.send.infrastructure.template.TestChain;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class B2BDeliveryRestClient extends RestClient {
    private final SenderReadB2BApi senderReadB2BApi;

    public B2BDeliveryRestClient(SenderReadB2BApi senderReadB2BApi) {
        this.senderReadB2BApi = senderReadB2BApi;
    }

    public TestChain<NewNotificationRequestStatusResponseV26> retrieveNotificationRequestStatusV26(String notificationRequestId) {
        return execute(
                () -> senderReadB2BApi.retrieveNotificationRequestStatusV26().notificationRequestIdQuery(notificationRequestId).execute(Function.identity()),
                NewNotificationRequestStatusResponseV26.class
        );
    }
}

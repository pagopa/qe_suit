package it.pagopa.send.bff.campaigns.infrastructure;

import it.pagopa.infrastructure.template.RestClient;
import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.send.generated.openapi.clients.bff.model.*;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.api.SenderInformalNotificationsApi;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.BffCampaignSearchResponseV1;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CampaignsRestClient extends RestClient {
    private final SenderInformalNotificationsApi senderInformalNotificationsApi;

    public CampaignsRestClient(TestChainFactory chainFactory, SenderInformalNotificationsApi senderInformalNotificationsApi) {
        super(chainFactory);
        this.senderInformalNotificationsApi = senderInformalNotificationsApi;
    }

    public TestChain<BffCampaignSearchResponseV1> getCampaigns() {
        return execute(
                () -> senderInformalNotificationsApi.getListCampaignsV1().execute(Function.identity()),
                BffCampaignSearchResponseV1.class
        );
    }
}

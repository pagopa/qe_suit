package it.pagopa.send.bff.campaigns.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.send.common.campaigns.application.CampaignsGateway;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.CampaignSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BffCampaignsGateway implements CampaignsGateway {
    private final CampaignsRestClient restClient;
    private final EntityStore entityStore;

    @Override
    public boolean supports(@NonNull Channel channel) {
        return channel == Channel.BFF;
    }

    @Override
    public List<CampaignSummary> getCampaigns() {
        return Objects.requireNonNull(restClient.getCampaigns().withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get()).getResultsPage();
    }
}

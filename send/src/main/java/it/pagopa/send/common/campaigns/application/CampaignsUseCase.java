package it.pagopa.send.common.campaigns.application;

import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.CampaignSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignsUseCase {
    private final CampaignsGateway campaignsGateway;
    public List<CampaignSummary> getCampaigns() {
        return campaignsGateway.getCampaigns();
    }

    public CampaignSummary getCampaignByID(String id) {
        return campaignsGateway.getCampaignByID(id);
    }
}

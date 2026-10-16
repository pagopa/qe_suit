package it.pagopa.send.common.campaigns.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignsUseCase {
    private final CampaignsGateway campaignsGateway;
    public void getCampaigns() {
        campaignsGateway.getCampaigns();
    }
}

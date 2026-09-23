package it.pagopa.send.common.campaigns.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignUserCase {
    private final CampaignGateway campaignGateway;
    public void selectCampaign(int campaignNumber) {
        campaignGateway.selectCampaign(campaignNumber);
    }
}

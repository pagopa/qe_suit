package it.pagopa.send.common.campaigns.application;


import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.generated.openapi.clients.sender.informal.bff.model.CampaignSummary;
import org.springframework.plugin.core.Plugin;

import java.util.List;

public interface CampaignsGateway extends Plugin<Channel> {
    List<CampaignSummary> getCampaigns();
}

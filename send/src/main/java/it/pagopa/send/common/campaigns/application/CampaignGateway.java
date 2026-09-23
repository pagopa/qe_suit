package it.pagopa.send.common.campaigns.application;


import it.pagopa.send.common.kernel.domain.Channel;
import org.springframework.plugin.core.Plugin;

public interface CampaignGateway extends Plugin<Channel> {
    void selectCampaign(int campaignNumber);
}

package it.pagopa.send.web.campagne.infrastructure;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.common.campaigns.application.CampaignGateway;
import it.pagopa.send.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebCampaignGateway implements CampaignGateway {
    private final WebPresentationGateway webPresentationGateway;


    @Override
    public void selectCampaign(int campaignNumber) {
        log.info(String.valueOf(webPresentationGateway.getLocation()));
    }

    @Override
    public boolean supports(@NonNull Channel delimiter) {
        return delimiter == Channel.WEB_BROWSER;
    }
}

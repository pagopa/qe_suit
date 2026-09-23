package it.pagopa.send.common.campaigns.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.common.campaigns.application.CampaignUserCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CampaignSteps {
    private final CampaignUserCase campaignUserCase;

    @When("seleziona la campagna numero {int}")
    public void selectCampaign(int campaignNumber) {
        campaignUserCase.selectCampaign(campaignNumber);
    }
}

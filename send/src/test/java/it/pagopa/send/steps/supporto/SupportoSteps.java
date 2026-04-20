package it.pagopa.send.steps.supporto;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.steps.mittenti.APIKey;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SupportoSteps {
    private final WebPresentationGateway uiGateway;

    @When("come utente di supporto non posso visualizzare le API Keys")
    public void verifySupportCannotSeeApiKeys() {
        APIKey apiKeyPage = uiGateway.bind(APIKey.class);
        apiKeyPage.assertSupportCannotSeeApiKey();
    }
}

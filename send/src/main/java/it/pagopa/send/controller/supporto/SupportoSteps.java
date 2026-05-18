package it.pagopa.send.controller.supporto;

import io.cucumber.java.en.When;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.domain.web.pages.mittente.APIKeyPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SupportoSteps {
    private final WebPresentationGateway uiGateway;

    @When("come utente di supporto non posso visualizzare le API Keys")
    public void verifySupportCannotSeeApiKeys() {
        APIKeyPage apiKeyPage = uiGateway.bind(APIKeyPage.class);
        apiKeyPage.assertSupportCannotSeeApiKey();
    }
}

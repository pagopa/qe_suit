package it.pagopa.send.steps;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.steps.mittenti.APIKeyPage;

public class ApiKeysPageSteps {
    private final APIKeyPage currentPage;

    public ApiKeysPageSteps(WebPresentationGateway uiGateway) {
        this.currentPage = uiGateway.bind(APIKeyPage.class);
    }

}

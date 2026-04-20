package it.pagopa.send.steps;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.steps.mittenti.APIKey;

public class ApiKeysPageSteps {
    private final APIKey currentPage;

    public ApiKeysPageSteps(WebPresentationGateway uiGateway) {
        this.currentPage = uiGateway.bind(APIKey.class);
    }

}

package it.pagopa.infrastructure.context;

import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;

public class CucumberLastApiResponseStore implements LastApiResponseStore {
    private ApiResponse lastResponse;

    @Override
    public ApiResponse getLastResponse() {
        return lastResponse;
    }

    @Override
    public void setLastResponse(ApiResponse lastResponse) {
        this.lastResponse = lastResponse;
    }
}

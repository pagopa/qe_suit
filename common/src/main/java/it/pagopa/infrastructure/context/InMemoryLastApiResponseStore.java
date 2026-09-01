package it.pagopa.infrastructure.context;

import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;

public class InMemoryLastApiResponseStore implements LastApiResponseStore {
    private final ThreadLocal<ApiResponse> lastResponse = new ThreadLocal<>();

    @Override
    public ApiResponse getLastResponse() {
        return lastResponse.get();
    }

    @Override
    public void setLastResponse(ApiResponse lastResponse) {
        this.lastResponse.set(lastResponse);
    }
}

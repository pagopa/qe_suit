package it.pagopa.interop.common.infrastructure.context.inmemory;

import it.pagopa.interop.common.infrastructure.context.LastApiResponseStore;
import it.pagopa.interop.common.infrastructure.response.ApiResponse;

public class InMemoryLastApiResponseStore implements LastApiResponseStore {
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

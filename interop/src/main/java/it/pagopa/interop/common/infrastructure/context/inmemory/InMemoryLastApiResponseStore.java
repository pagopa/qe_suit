package it.pagopa.interop.common.infrastructure.context.inmemory;

import it.pagopa.interop.common.kernel.context.LastApiResponseStore;
import it.pagopa.interop.common.infrastructure.response.ApiResponse;

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

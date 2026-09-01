package it.pagopa.kernel.context;

import it.pagopa.interop.common.infrastructure.response.ApiResponse;

public interface LastApiResponseStore {
    ApiResponse getLastResponse();

    void setLastResponse(ApiResponse lastResponse);
}

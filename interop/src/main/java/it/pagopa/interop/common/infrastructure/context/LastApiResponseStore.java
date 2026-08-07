package it.pagopa.interop.common.infrastructure.context;

import it.pagopa.interop.common.infrastructure.response.ApiResponse;

public interface LastApiResponseStore {
    ApiResponse getLastResponse();

    void setLastResponse(ApiResponse lastResponse);
}

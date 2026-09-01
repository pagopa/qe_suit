package it.pagopa.application.context;

import it.pagopa.infrastructure.response.ApiResponse;

public interface LastApiResponseStore {
    ApiResponse getLastResponse();

    void setLastResponse(ApiResponse lastResponse);
}

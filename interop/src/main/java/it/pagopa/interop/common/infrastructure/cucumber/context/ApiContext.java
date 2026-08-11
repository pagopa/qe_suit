package it.pagopa.interop.common.infrastructure.cucumber.context;

import it.pagopa.interop.common.infrastructure.context.LastApiResponseStore;
import it.pagopa.interop.common.infrastructure.response.ApiResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiContext implements LastApiResponseStore {
    private ApiResponse lastResponse;
}

package it.pagopa.interop.common.infrastructure.context.cucumber;

import it.pagopa.kernel.context.LastApiResponseStore;
import it.pagopa.infrastructure.response.ApiResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CucumberLastApiResponseStore implements LastApiResponseStore {
    private ApiResponse lastResponse;
}

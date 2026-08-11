package it.pagopa.interop.common.infrastructure.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.infrastructure.context.LastApiResponseStore;
import it.pagopa.interop.common.infrastructure.response.ApiResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Getter
@Setter
public class ApiContext implements LastApiResponseStore {
    private ApiResponse lastResponse;
}

package it.pagopa.interop.new_arch.common.infrastructure.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.new_arch.common.infrastructure.response.ApiResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Getter
@Setter
public class ApiContext {
    private ApiResponse lastResponse;
}

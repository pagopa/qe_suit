package it.pagopa.interop.common.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.domain.model.Eservice;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class EserviceContext extends AbstractContext<Eservice> {
}

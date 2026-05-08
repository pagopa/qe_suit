package it.pagopa.interop.domain.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.domain.model.ClientAssertion;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ClientAssertionContext extends AbstractContext<ClientAssertion> {
}

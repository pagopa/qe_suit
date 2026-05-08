package it.pagopa.interop.domain.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ScenarioScope
public class ClientAssertionContext extends AbstractContext<ClientAssertion> {
    private Map<ClientAssertion, ClientAssertionValidation> validationMap = new HashMap<>();

    public void addValidation(ClientAssertion clientAssertion, ClientAssertionValidation validation) {
        validationMap.put(clientAssertion, validation);
    }
}

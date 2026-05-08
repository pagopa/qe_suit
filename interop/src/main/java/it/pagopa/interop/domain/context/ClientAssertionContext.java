package it.pagopa.interop.domain.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.ClientAssertionValidationResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ScenarioScope
public class ClientAssertionContext extends AbstractContext<ClientAssertion> {
    private Map<ClientAssertion, ClientAssertionValidationResult> validationMap = new HashMap<>();

    public void addValidation(ClientAssertion clientAssertion, ClientAssertionValidationResult validation) {
        validationMap.put(clientAssertion, validation);
    }

    public ClientAssertionValidationResult getValidation(ClientAssertion clientAssertion) {
        return validationMap.get(clientAssertion);
    }
}

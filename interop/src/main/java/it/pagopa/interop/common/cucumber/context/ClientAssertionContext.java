package it.pagopa.interop.common.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.domain.model.ClientAssertion;
import it.pagopa.interop.common.domain.model.ClientAssertionValidationResult;
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

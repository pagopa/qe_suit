package it.pagopa.interop.common.client_assertion;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.client.Client;
import it.pagopa.interop.common.purpose.Purpose;
import it.pagopa.interop.common.utils.JwtBuilderUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientAssertionController {
    private final CreateClientAssertionService clientAssertionService;
    private final ScenarioContext scenarioContext;

    @Given("una client assertion valida generata usando il {currentClient} e la {currentPurpose}")
    public void createClientAssertion(Client client, Purpose purpose) throws NoSuchAlgorithmException, JsonProcessingException {
        saveClientAssertion(clientAssertionService.createClientAssertion(client, purpose));
    }

    @Given("una client assertion generata usando il {currentClient}, la {currentPurpose} e:")
    public void createClientAssertion(Client client, Purpose purpose, List<JwtBuilderUtils.JwtClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        saveClientAssertion(clientAssertionService.createClientAssertion(client, purpose, overrides));
    }

    @Given("una client assertion valida generata usando il {currentClient}")
    public void createClientAssertion(Client client) throws NoSuchAlgorithmException, JsonProcessingException {
        saveClientAssertion(clientAssertionService.createClientAssertion(client));
    }

    @Given("una client assertion generata usando il {currentClient} e:")
    public void createClientAssertion(Client client, List<JwtBuilderUtils.JwtClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        saveClientAssertion(clientAssertionService.createClientAssertion(client, overrides));
    }

    private void saveClientAssertion(String rawClientAssertion) {
        ClientAssertion clientAssertion = new ClientAssertion(rawClientAssertion);
        scenarioContext.upsert(clientAssertion);
    }
}
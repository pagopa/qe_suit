package it.pagopa.interop.common.client.infrastructure.cucumber;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import it.pagopa.interop.common.client.application.ClientAssertionUseCase;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientAssertionClaimOverride;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RequiredArgsConstructor
public class ClientAssertionSteps {

    private final ClientAssertionUseCase clientAssertionUseCase;

    @Given("una client assertion generata usando il {currentClient} e:")
    public void createClientAssertion(Client client, List<ClientAssertionClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        clientAssertionUseCase.createClientAssertion(client,  overrides);
    }

    @Given("una client assertion (valida )generata usando il {currentClient}")
    public void createClientAssertion(Client client) throws NoSuchAlgorithmException, JsonProcessingException {
        clientAssertionUseCase.createClientAssertion(client);
    }

    @Given("una client assertion generata usando il {currentClient}, la {currentPurpose} e:")
    public void createClientAssertion(Client client, Purpose purpose, List<ClientAssertionClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        clientAssertionUseCase.createClientAssertion(client, purpose, overrides);
    }

    @Given("una client assertion valida generata usando il {currentClient} e la {currentPurpose}")
    public void createClientAssertion(Client client, Purpose purpose) throws NoSuchAlgorithmException, JsonProcessingException {
        clientAssertionUseCase.createClientAssertion(client, purpose);
    }
}

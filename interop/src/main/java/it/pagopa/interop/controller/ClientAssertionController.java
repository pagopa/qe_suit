package it.pagopa.interop.controller;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import it.pagopa.interop.domain.context.ClientAssertionContext;
import it.pagopa.interop.domain.model.Client;
import it.pagopa.interop.domain.model.ClientAssertion;
import it.pagopa.interop.domain.model.Purpose;
import it.pagopa.interop.service.client_assertion.CreateClientAssertionService;
import it.pagopa.interop.utils.JwtBuilderUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientAssertionController {
    private final CreateClientAssertionService clientAssertionService;
    private final ClientAssertionContext clientAssertionContext;

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

    private void saveClientAssertion(String clientAssertion) {
        clientAssertionContext.upsert(new ClientAssertion(clientAssertion));
    }
}
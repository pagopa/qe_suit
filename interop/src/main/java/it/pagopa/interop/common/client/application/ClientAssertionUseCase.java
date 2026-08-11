package it.pagopa.interop.common.client.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientAssertion;
import it.pagopa.interop.common.client.domain.ClientAssertionClaimOverride;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientAssertionUseCase {

    private final ClientAssertionGateway clientAssertionGateway;

    public ClientAssertion createClientAssertion(Client client) throws NoSuchAlgorithmException, JsonProcessingException {
        return clientAssertionGateway.createClientAssertion(client, null, null, List.of());
    }

    public ClientAssertion createClientAssertion(Client client, Purpose purpose) throws NoSuchAlgorithmException, JsonProcessingException {
        return clientAssertionGateway.createClientAssertion(client, purpose, null, List.of());
    }

    public ClientAssertion createClientAssertion(Client client, List<ClientAssertionClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        return clientAssertionGateway.createClientAssertion(client, null, null, overrides);
    }

    public ClientAssertion createClientAssertion(Client client, Purpose purpose, List<ClientAssertionClaimOverride> overrides) throws NoSuchAlgorithmException, JsonProcessingException {
        return clientAssertionGateway.createClientAssertion(client, purpose, null, overrides);
    }

    public ClientAssertion createClientAssertion(Client client, Purpose purpose, KeyPair keyPair) throws NoSuchAlgorithmException, JsonProcessingException {
        return clientAssertionGateway.createClientAssertion(client, purpose, keyPair, List.of());
    }
}
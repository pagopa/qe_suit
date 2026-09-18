package it.pagopa.interop.common.infrastructure.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.client.application.ClientAssertionUseCase;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientAssertion;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.kernel.security.AccessToken;
import it.pagopa.kernel.security.DPoPProof;
import it.pagopa.kernel.security.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccessTokenProvider {

    private final ClientAssertionUseCase clientAssertionUseCase;
    private final AuthRestClient authApiClient;

    public AccessToken createAPIAccessToken(Client client, @Nullable DPoPProof dPoPProof) throws NoSuchAlgorithmException, JsonProcessingException {
        if (client == null || client.getKind() != ClientKind.API)
            throw new IllegalArgumentException("Client non valido o non di tipo API");

        return internalGetAccessToken(client, null, client.getLastKey().pair(), dPoPProof);
    }

    public AccessToken createEServiceAccessToken(Client client, Purpose purpose, @Nullable DPoPProof dPoPProof) throws NoSuchAlgorithmException, JsonProcessingException {
        if (client == null || client.getKind() != ClientKind.CONSUMER)
            throw new IllegalArgumentException("Client non valido o non di tipo CONSUMER");

        return internalGetAccessToken(client, purpose, client.getLastKey().pair(), dPoPProof);
    }

    private AccessToken internalGetAccessToken(Client client, Purpose purpose, KeyPair keyPair, @Nullable DPoPProof dPoPProof) throws NoSuchAlgorithmException, JsonProcessingException {

        ClientAssertion clientAssertion = clientAssertionUseCase.createClientAssertion(client, purpose, keyPair);

        return authApiClient.createToken(client.getId(), clientAssertion.getClientAssertion(), dPoPProof != null ? dPoPProof.getJwt() : null)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(credentials ->
                        new AccessToken(
                                credentials.getAccessToken(),
                                TokenType.valueOf(credentials.getTokenType().name().toUpperCase()),
                                credentials.getExpiresIn()
                        )
                )
                .get();
    }
}

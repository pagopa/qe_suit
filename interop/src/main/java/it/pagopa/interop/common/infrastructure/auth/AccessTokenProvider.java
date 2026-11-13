package it.pagopa.interop.common.infrastructure.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.interop.common.client.application.ClientAssertionGateway;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientKind;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.kernel.security.AccessToken;
import it.pagopa.kernel.security.DPoPProof;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccessTokenProvider {

    private final ClientAssertionGateway clientAssertionGateway;

    public AccessToken createAPIAccessToken(Client client, @Nullable DPoPProof dPoPProof) throws NoSuchAlgorithmException, JsonProcessingException {
        if (client == null || client.getKind() != ClientKind.API)
            throw new IllegalArgumentException("Client non valido o non di tipo API");

        return clientAssertionGateway.generateVoucher(client, null, dPoPProof);
    }

    public AccessToken createEServiceAccessToken(Client client, Purpose purpose, @Nullable DPoPProof dPoPProof) throws NoSuchAlgorithmException, JsonProcessingException {
        if (client == null || client.getKind() != ClientKind.CONSUMER)
            throw new IllegalArgumentException("Client non valido o non di tipo CONSUMER");

        return clientAssertionGateway.generateVoucher(client, purpose, dPoPProof);
    }
}

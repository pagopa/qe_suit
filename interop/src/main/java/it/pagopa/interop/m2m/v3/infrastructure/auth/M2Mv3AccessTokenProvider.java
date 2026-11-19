package it.pagopa.interop.m2m.v3.infrastructure.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.infrastructure.auth.AccessTokenProvider;
import it.pagopa.kernel.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class M2Mv3AccessTokenProvider {

    @Value("${interop.auth.oauth.server.sync}")
    private String syncHtu;

    private final AccessTokenProvider accessTokenProvider;
    private final DPoPProofService dPoPProofService;

    public M2Mv3AccessTokenContext create(Client client)
            throws NoSuchAlgorithmException, JsonProcessingException {

        KeyPair dpopKeyPair = KeyPairUtils.generate(
                KeyAlgorithm.EC
        );

        return issueToken(
                client,
                dpopKeyPair
        );
    }

    private M2Mv3AccessTokenContext issueToken(
            Client client,
            KeyPair dpopKeyPair)
            throws NoSuchAlgorithmException, JsonProcessingException {

        DPoPProof tokenProof =
                dPoPProofService.buildDPoPProof(
                        dpopKeyPair,
                        DPoPProofService.HttpMethod.POST,
                        syncHtu
                );

        Instant issuedAt = Instant.now();

        AccessToken accessToken =
                accessTokenProvider.createAPIAccessToken(
                        client,
                        tokenProof
                );

        Instant expiresAt =
                issuedAt.plusSeconds(
                        accessToken.expiresIn()
                );

        return new M2Mv3AccessTokenContext(
                client,
                accessToken,
                dpopKeyPair,
                expiresAt
        );
    }
}